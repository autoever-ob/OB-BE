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
import com.campick.server.common.dto.PageResponseDto;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.exception.UnauthorizedException;
import com.campick.server.common.jwt.JWTUtil;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String ACCESS_TOKEN_PREFIX = "AT:";
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 1000 * 60 * 30L; // 30분으로 지정

    @Transactional
    public void signUp(MemberSignUpRequestDto requestDto) {

        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_ACCOUNT_EXCEPTION.getMessage());
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_MISMATCH_EXCEPTION.getMessage());
        }

//        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = requestDto.toEntity(requestDto.getPassword());
        memberRepository.save(member);

        if (requestDto.getRole() == Role.DEALER) {
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

    public MemberLoginResponseDto login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));

//        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
//            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
//        }

        if (!requestDto.getPassword().equals(member.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
        }

        String accessToken = jwtUtil.createJwt("access", member.getId(), member.getRole().name(), ACCESS_TOKEN_EXPIRATION_MS);

        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + member.getId(), accessToken, ACCESS_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);

        return MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .phoneNumber(member.getMobileNumber())
                .dealerId(member.getDealer() != null ? member.getDealer().getId() : null)
                .role(member.getRole().name())
                .profileImageUrl(member.getProfileImageUrl())
                .profileThumbnailUrl(member.getProfileThumbnailUrl())
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new UnauthorizedException("Access token is missing or invalid.");
        }
        String token = accessToken.substring(7);

        Long memberId;
        try {
            memberId = jwtUtil.getId(token);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 ID를 가져와야 로그아웃 처리가 가능
            memberId = e.getClaims().get("id", Long.class);
        }

        // Redis에서 토큰 삭제
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + memberId);
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));
        member.delete();
        memberRepository.save(member);
    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmailAndIsDeletedFalse(email).isPresent();
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.findByNicknameAndIsDeletedFalse(nickname).isPresent();
    }

    @Transactional
    public void updatePassword(String email, @Valid PasswordUpdateRequestDto requestDto) {
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_MISMATCH_EXCEPTION.getMessage());
        }

        Member targetMember = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        targetMember.updatePassword(encodedPassword);
        memberRepository.save(targetMember);
    }

    @Transactional
    public Map<String, String> updateProfileImage(String email, MultipartFile file) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));
        Map<String, String> imageUrls = firebaseStorageService.uploadProfileImage(member.getId(), file);
        member.updateProfileImage(imageUrls.get("profileImageUrl"), imageUrls.get("profileThumbnailUrl"));
        memberRepository.save(member);
        return imageUrls;
    }

    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateRequestDto requestDto) {
        // 멤버가 있는지 확인
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        // 닉네임이 중복인지 확인하는게 필요
        String newNickname = requestDto.getNickname();
        if (newNickname != null && !newNickname.isBlank() && !newNickname.equals(member.getNickname())) {
            if (isNicknameDuplicate(newNickname)) {
                throw new BadRequestException(ErrorStatus.DUPLICATE_NICKNAME_EXCEPTION.getMessage());
            }
            member.updateNickname(newNickname);
        }else{
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        // 모바일 업데이트
        String newMobileNumber = requestDto.getMobileNumber();
        if (newMobileNumber != null && !newMobileNumber.isBlank()) {
            member.updateMobileNumber(newMobileNumber);
        } else{
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        // 자기소개 업데이트
        // 자기소개는 빈칸이 가능함
        String newDescription = requestDto.getDescription();
        if (newDescription != null) {
            member.updateDescription(newDescription);
        }

        memberRepository.save(member);
    }

    @Transactional
    public MemberLoginResponseDto reissueToken(String accessToken) {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new UnauthorizedException(ErrorStatus.USER_UNAUTHORIZED.getMessage());
        }
        String token = accessToken.substring(7);

        Long id;
        String role;

        // id와 role을 추출함
        try {
            id = jwtUtil.getId(token);
            role = jwtUtil.getRole(token);
        } catch (ExpiredJwtException e) {
            // 메서드가 제대로 동작하지 않더라도 id와 role을 추출
            id = e.getClaims().get("id", Long.class);
            role = e.getClaims().get("role", String.class);
        }

        String storedToken = (String) redisTemplate.opsForValue().get(ACCESS_TOKEN_PREFIX + id);

        if (storedToken == null || !storedToken.equals(token)) {
            throw new UnauthorizedException(ErrorStatus.MALFORMED_ACCESS_TOKEN_EXCEPTION.getMessage());
        }

        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        String newAccessToken = jwtUtil.createJwt("access", id, role, ACCESS_TOKEN_EXPIRATION_MS);

        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + id, newAccessToken, ACCESS_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);

        return MemberLoginResponseDto.builder()
                .accessToken(newAccessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .phoneNumber(member.getMobileNumber())
                .dealerId(member.getDealer() != null ? member.getDealer().getId() : null)
                .role(member.getRole().name())
                .profileImageUrl(member.getProfileImageUrl())
                .profileThumbnailUrl(member.getProfileThumbnailUrl())
                .build();
    }

    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));
        List<Review> reviews = reviewRepository.findByTargetIdWithAuthor(id);
        return MemberResponseDto.of(member, reviews);
    }

    // N + 1 문제를 한번 스스로 생각해보기
    public PageResponseDto<ProductAvailableSummaryDto> getMemberProducts(Long id, Pageable pageable) {
        if (memberRepository.findByIdAndIsDeletedFalse(id).isEmpty()) {
            throw new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage());
        }

        // 레포에서 데이터를 받아온다
        // 하지만 여러번의 조인으로 인해서 N+1 문제가 발생해 성능 위기가 발생할 수 있다.
        // JPQL을 사용해서 FETCH JOIN으로 가능한 모든 ROW와 이와 연관된 테이블들의 정보 뷰를 만들어내어 N+1 문제를 제거
        Page<Product> products = productRepository.findProductByMemberIdWithDetails(id, pageable);
        // 찾아왔으면 원하는 값에 알맞게 채워줌
        // 여러개의 products를 하나씩 보내면서 Dto를 만든다
        // 원래는 배열로 가능하나 Stream으로 편리하게 가능
        Page<ProductAvailableSummaryDto> productAvailableSummaryDtos = products.map(ProductAvailableSummaryDto::from);
        return new PageResponseDto<>(productAvailableSummaryDtos);
    }

    // 내가 샀으니깐 판 사람의 id를 가지고 조회할게
    public PageResponseDto<TransactionResponseDto> getMemberBought(Long buyerId, Pageable pageable) {
        // 멤버가 존재하는지 확인
        Member buyer = memberRepository.findById(buyerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        //! TODO N + 1은 추후에 풀어보기 ( 복습 )
        Page<Transaction> transactions = transactionRepository.findTransactionsByBuyer(buyer, pageable);
        Page<TransactionResponseDto> transactionDtos = transactions.map(transaction -> TransactionResponseDto.from(transaction, "SOLD"));
        return new PageResponseDto<>(transactionDtos);
    }

    // 내가 판
    public PageResponseDto<TransactionResponseDto> getMemberSold(Long sellerId, Pageable pageable) {
        // 멤버가 존재하는지 확인
        Member seller = memberRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        Page<Transaction> transactions = transactionRepository.findTransactionsBySeller(seller, pageable);
        Page<TransactionResponseDto> transactionDtos = transactions.map(transaction -> TransactionResponseDto.from(transaction, "BUY"));
        return new PageResponseDto<>(transactionDtos);
    }

    public PageResponseDto<ReviewResponseDto> getReviewById(Long memberId, Pageable pageable) {
        // 멤버가 존재하는지 확인
        memberRepository.findByIdAndIsDeletedFalse(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        Page<Review> reviews = reviewRepository.findByTargetIdWithAuthor(memberId, pageable);
        Page<ReviewResponseDto> reviewResponseDtos = reviews.map(ReviewResponseDto::from);
        return new PageResponseDto<>(reviewResponseDtos);
    }

    public boolean checkPasswordValidation(Long memberId, String password) {
        // 멤버가 존재하는지 확인
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );
        return member.getPassword().equals(password);
    }

    public PageResponseDto<ProductAvailableSummaryDto> getMemberProductsAll(Long memberId, Pageable pageable) {
        // 멤버가 존재하는지 확인
        Member seller = memberRepository.findByIdAndIsDeletedFalse(memberId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        Page<Product> products = productRepository.findProductsBySeller(seller, pageable);
        Page<ProductAvailableSummaryDto> productAvailableSummaryDtos = products.map(ProductAvailableSummaryDto::from);
        return new PageResponseDto<>(productAvailableSummaryDtos);
    }
}
