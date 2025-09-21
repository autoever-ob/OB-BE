package com.campick.server.api.member.service;


import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.PasswordReset;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.member.repository.PasswordResetRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final MemberRepository memberRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    @Value("cnckddn0146@gmail.com")
    private String serviceEmail;


    // 1. 인증 코드 생성 및 이메일 발송
    public void sendResetLink(String email) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.EMAIL_NOT_FOUND.getMessage()));

        String code = emailService.generateCode();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(15);

        PasswordReset reset = PasswordReset.builder()
                .email(email)
                .code(code)
                .expirationTime(expiration)
                .isVerified(false)
                .build();

        passwordResetRepository.save(reset);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("캠픽 <%s>",serviceEmail));
        message.setTo(email);
        message.setSubject("[Campick] 비밀번호 재설정 안내");
        message.setText("비밀번호를 재설정 코드입니다. (제한시간 15분):\n\n" + code);
        mailSender.send(message);
    }

    // 2. 인증코드 검증 및 비밀번호 재설정
    public void resetPasswordWithCode(String code) {
        PasswordReset reset = passwordResetRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PASSWORD_RESET_INVALID_CODE.getMessage()));

        if (reset.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_RESET_EXPIRED_CODE.getMessage());
        }

        if (reset.isVerified()) {
            throw new BadRequestException(ErrorStatus.PASSWORD_RESET_CODE_ALREADY_USED.getMessage());
        }


        Member member = memberRepository.findByEmailAndIsDeletedFalse(reset.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        String newPassword = generateRandomPassword();

//        member.updatePassword(passwordEncoder.encode(newPassword));
        member.updatePassword(newPassword);
        memberRepository.save(member);

        reset.markVerified(); // 인증코드 사용 처리

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(String.format("캠픽 <%s>",serviceEmail));
        message.setTo(member.getEmail());
        message.setSubject("[Campick] 비밀번호 초기화 안내");
        message.setText("비밀번호가 초기화 되었습니다 :\n\n" + newPassword);
        mailSender.send(message);
        passwordResetRepository.save(reset);
    }

    private String generateRandomPassword() {
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String NUMBERS = "0123456789";
        final String SPECIAL = "!@#$%^&*()-_=+<>?";
        final String ALL_CHARS = LOWERCASE + UPPERCASE + NUMBERS + SPECIAL;
        final int PASSWORD_LENGTH = 7;

        SecureRandom random = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();

        // 1. 각 종류의 문자를 하나씩 추가
        passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordChars.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 2. 나머지 길이를 모든 문자 종류에서 랜덤하게 채움
        for (int i = passwordChars.size(); i < PASSWORD_LENGTH; i++) {
            passwordChars.add(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 3. 예측 불가능하도록 리스트를 섞음
        Collections.shuffle(passwordChars, random);

        // 4. 최종 비밀번호 문자열 생성
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (Character ch : passwordChars) {
            password.append(ch);
        }

        return password.toString();
    }
}