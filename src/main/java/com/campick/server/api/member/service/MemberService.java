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

    public MemberLoginResponseDto login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
        }

        String accessToken = jwtUtil.createJwt("access", member.getId(), member.getRole().name(), ACCESS_TOKEN_EXPIRATION_MS);

        // Redis에 Access Token 저장
        redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + member.getId(), accessToken, ACCESS_TOKEN_EXPIRATION_MS, TimeUnit.MILLISECONDS);

        return MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .memberId(member.getId())
                .nickname(member.getNickname())
                .phoneNumber(member.getMobileNumber())
                .dealerId(member.getDealer() != null ? member.getDealer().getId() : null)
                .role(member.getRole().name())
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
            // 메서드가 제대로 동작하지 ㅎ
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
                .build();
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

    public List<TransactionResponseDto> getMemberBought(Long buyerId) {
        Member buyer = memberRepository.findByIdAndIsDeletedFalse(buyerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        List<Transaction> transactions = transactionRepository.findTransactionsByBuyer(buyer);

        return transactions.stream()
                .map(TransactionResponseDto::from)
                .toList();
    }

    public List<TransactionResponseDto> getMemberSold(Long sellerId) {
        Member seller = memberRepository.findByIdAndIsDeletedFalse(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        List<Transaction> transactions = transactionRepository.findTransactionsBySeller(seller);
        return transactions.stream()
                .map(TransactionResponseDto::from)
                .toList();
    }
}
