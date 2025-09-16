package com.campick.server.api.member.service;

import com.campick.server.api.member.dto.MemberLoginRequestDto;
import com.campick.server.api.member.dto.MemberLoginResponseDto;
import com.campick.server.api.member.dto.MemberSignUpRequestDto;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.jwt.JWTUtil;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public void signUp(MemberSignUpRequestDto requestDto) {

        // 만약 이미 해당 이메일로 가입된 정보가 있다면 예외처리
        if (memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_ACCOUNT_EXCEPTION.getMessage());
        }

        // 비밀번호랑 비밀번호 재확인 값이 다를 경우 예외처리
        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_MISMATCH_EXCEPTION.getMessage());
        }

        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
//        String imageUrl = requestDto.getProfileImage();

        Member member = requestDto.toEntity(encodedPassword);
        memberRepository.save(member);
    }

public MemberLoginResponseDto login(MemberLoginRequestDto requestDto) {

        // 1. 이메일로 사용자 조회
        Member member = memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD_EXCEPTION.getMessage());
        }



        // 3. JWT 생성
        String accessToken = jwtUtil.createJwt("access", member.getId(), member.getRole().name(), 1000 * 60 * 30L);
        String refreshToken = jwtUtil.createJwt("refresh", member.getId(), member.getRole().name(), 1000L * 60 * 60 * 24 * 7);

        // 4. Refresh 토큰 저장 (DB에)
        member.updateRefreshToken(refreshToken, 1000L * 60 * 60 * 24 * 7);
        memberRepository.save(member);

        return new MemberLoginResponseDto(accessToken,refreshToken);



    }
}
