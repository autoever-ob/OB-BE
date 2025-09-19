package com.campick.server.api.member.service;

import com.campick.server.api.dealer.entity.Dealer;
import com.campick.server.api.dealer.repository.DealerRepository;
import com.campick.server.api.dealership.entity.DealerShip;
import com.campick.server.api.dealership.repository.DealershipRepository;
import com.campick.server.api.member.dto.*;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.Role;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.product.entity.Product;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.api.review.entity.Review;
import com.campick.server.api.review.repository.ReviewRepository;
import com.campick.server.api.transaction.entity.Transaction;
import com.campick.server.api.transaction.repository.TransactionRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.exception.UnauthorizedException;
import com.campick.server.common.jwt.JWTUtil;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final JWTUtil jwtUtil;
    private final FirebaseStorageService firebaseStorageService;
    private final DealershipRepository dealershipRepository;
    private final DealerRepository dealerRepository;
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 days
    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void signUp(MemberSignUpRequestDto requestDto) {

        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_ACCOUNT_EXCEPTION.getMessage());
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_MISMATCH_EXCEPTION.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = requestDto.toEntity(encodedPassword);
        memberRepository.save(member);

        if (requestDto.getRole() == Role.ROLE_DEALER) {
            DealerShip dealerShip = dealershipRepository.findByRegistrationNumber(requestDto.getDealershipRegistrationNumber())
                    .orElseGet(() -> dealershipRepository.save(DealerShip.builder()
                            .name(requestDto.getDealershipName())
                            .registrationNumber(requestDto.getDealershipRegistrationNumber())
                            .build()));

            Dealer dealer = Dealer.builder()
                    .businessNo(requestDto.getDealershipRegistrationNumber())
                    .rating(0.0)
                    .user(member)
                    .dealerShip(dealerShip)
                    .build();
            dealerRepository.save(dealer);

            member.assignDealer(dealer);
            memberRepository.save(member);
        }
    }

    public Map<String, Object> login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
        }

        String accessToken = jwtUtil.createJwt("access", member.getId(), member.getRole().name(), 1000 * 60 * 30L); // 30 minutes
        String refreshToken = jwtUtil.createJwt("refresh", member.getId(), member.getRole().name(), REFRESH_TOKEN_EXPIRATION_MS);

        // redis에서 리프레시 토큰을 다시 설정
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + member.getId(),
                refreshToken,
                REFRESH_TOKEN_EXPIRATION_MS,
                TimeUnit.MILLISECONDS
        );

        Long dealerId = member.getDealer() == null ? null : member.getDealer().getId();

        MemberLoginResponseDto dto = new MemberLoginResponseDto(
                accessToken,
                member.getId(),
                dealerId,
                member.getProfileImage(),
                member.getProfileImage()
        );

        Map<String, Object> result =
                Map.of("loginResponseDto", dto, "refreshToken", refreshToken);

        return result;
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }
        Long memberId = jwtUtil.getId(refreshToken);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + memberId);
    }


    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmailAndIsDeletedFalse(email).isPresent();
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.findByNicknameAndIsDeletedFalse(nickname).isPresent();
    }

    @Transactional
    public void updatePassword(String email, @Valid PasswordUpdateRequestDto requestDto) {
        if(!requestDto.getPassword().equals(requestDto.getConfirmPassword())){
            throw new BadRequestException(ErrorStatus.PASSWORD_MISMATCH_EXCEPTION.getMessage());
        }

        Member targetMember = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(()-> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));


        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        targetMember.updatePassword(encodedPassword);
        memberRepository.save(targetMember);
    }



    @Transactional
    public String updateProfileImage(String email, MultipartFile file) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));
        try {
            String imageUrl = firebaseStorageService.uploadProfileImage(member.getId(), file);
            member.updateProfileImage(imageUrl);
            memberRepository.save(member);
            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    @Transactional
    public Map<String, Object> reissueToken(String refresh) {

        // 리프레시 토큰이 없었을 경우
        if (refresh == null) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        // 리프레시가 만료되었을경우
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_EXPIRED.getMessage());
        }

//        // category를 가져옴
//        String category = jwtUtil.getCategory(refresh);
//        if (!"refresh".equals(category)) {
//            throw new UnauthorizedException(ErrorStatus.MALFORMED_REFRESH_TOKEN_EXCEPTION.getMessage());
//        }

        // accessToken에서 id, role 조회
        Long id = jwtUtil.getId(refresh);
        String role = jwtUtil.getRole(refresh);

        // 레디스를 이용해 서버에서 저장하는 리프레시 토큰을 불러옴
        String tokenFromRedis = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + id);
        if (tokenFromRedis == null) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        // 토큰이 일치하는지 조회
        if (!refresh.equals(tokenFromRedis)) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_EQUAL.getMessage());
        }

        // 멤버를 찾아야함
        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        // 엑세스 토큰과 리프레시 토큰을 재발행
        String newAccessToken = jwtUtil.createJwt("access", id, role, 1000 * 60 * 30L);
        String newRefreshToken = jwtUtil.createJwt("refresh", id, role, REFRESH_TOKEN_EXPIRATION_MS);

        // 새로운 리프레시 토큰을 redis에 저장해줌
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + id,
                newRefreshToken,
                REFRESH_TOKEN_EXPIRATION_MS,
                TimeUnit.MILLISECONDS
        );

        Long dealerId = member.getDealer() == null ? null : member.getDealer().getId();

        MemberLoginResponseDto dto = new MemberLoginResponseDto(
                newAccessToken,
                member.getId(),
                dealerId,
                member.getProfileImage(),
                member.getProfileImage()
        );

        Map<String, Object> result =
                Map.of("loginResponseDto", dto, "refreshToken", newRefreshToken);

        return result;
    }


    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));
        List<Review> reviews = reviewRepository.findByTargetIdWithAuthor(id);
        return MemberResponseDto.of(member, reviews);
    }


    public List<ProductAvailableSummaryDto> getMemberProducts(Long id) {

        if(memberRepository.findByIdAndIsDeletedFalse(id).isEmpty()) {
            throw new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage());
        }

        List<Product> products = productRepository.findProductByMemberIdWithDetails(id);

        return products.stream()
                .map(ProductAvailableSummaryDto::from)
                .toList();
    }

    // ! TODO 여기서 수정해야함
    public List<TransactionResponseDto> getMemberBought(Long buyerId) {
        Member buyer = memberRepository.findById(buyerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        List<Transaction> transactions = transactionRepository.findTransactionsByBuyer(buyer);

        return transactions.stream()
                .map(TransactionResponseDto::from)
                .toList();
    }

    // ! TODO 여기서 수정해야함
    public List<TransactionResponseDto> getMemberSold(Long sellerId) {
        Member seller = memberRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        List<Transaction> transactions = transactionRepository.findTransactionsBySeller(seller);
        return transactions.stream()
                .map(TransactionResponseDto::from)
                .toList();
    }
}
