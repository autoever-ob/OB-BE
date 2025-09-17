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
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.exception.UnauthorizedException;
import com.campick.server.common.jwt.JWTUtil;
import com.campick.server.common.response.ErrorStatus;
import com.campick.server.common.storage.FirebaseStorageService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
            Dealer savedDealer = dealerRepository.save(dealer);

            member.assignDealer(savedDealer);
            memberRepository.save(member);
        }
    }

public MemberLoginResponseDto login(MemberLoginRequestDto requestDto) {

        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
        }

        String accessToken = jwtUtil.createJwt("access", member.getId(), member.getRole().name(), 1000 * 60 * 30L);
        String refreshToken = jwtUtil.createJwt("refresh", member.getId(), member.getRole().name(), 1000L * 60 * 60 * 24 * 7);

        member.updateRefreshToken(refreshToken, 1000L * 60 * 60 * 24 * 7);
        memberRepository.save(member);

        return new MemberLoginResponseDto(accessToken,refreshToken);

    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        Long memberId = jwtUtil.getId(refreshToken);

        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        member.updateRefreshToken(null, 0L);
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
                .orElseThrow(()-> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));


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
    public MemberLoginResponseDto reissueToken(String refresh) {


        if (refresh == null) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new UnauthorizedException(ErrorStatus.MALFORMED_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        Long id = jwtUtil.getId(refresh);
        String role = jwtUtil.getRole(refresh);

        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        if (!refresh.equals(member.getRefreshToken())) {
            throw new UnauthorizedException(ErrorStatus.REFRESH_TOKEN_NOT_EQUAL.getMessage());
        }

        String newAccessToken = jwtUtil.createJwt("access", id, role, 1000 * 60 * 30L);
        String newRefreshToken = jwtUtil.createJwt("refresh", id, role, 1000L * 60 * 60 * 24 * 7);

        member.updateRefreshToken(newRefreshToken,1000L * 60 * 60 * 24 * 7);
        memberRepository.save(member);

        return new MemberLoginResponseDto(newAccessToken, newRefreshToken);
    }


    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));
        List<Review> reviews = reviewRepository.findByTargetIdWithAuthor(id);
        return MemberResponseDto.of(member, reviews);
    }


    // N + 1 문제를 한번 스스로 생각해보기
    public List<ProductAvailableSummaryDto> getMemberProducts(Long id) {

        Member member = memberRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(()-> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        // 레포에서 데이터를 받아온다
        // 하지만 여러번의 조인으로 인해서 N+1 문제가 발생해 성능 위기가 발생할 수 있다.
        // JPQL을 사용해서 FETCH JOIN으로 가능한 모든 ROW와 이와 연관된 테이블들의 정보 뷰를 만들어내어 N+1 문제를 제거
        List<Product> products = productRepository.findProductByMemberIdWithDetails(id);

        // 찾아왔으면 원하는 값에 알맞게 채워줌
        return ProductAvailableSummaryDto.from(products)
    }
}
