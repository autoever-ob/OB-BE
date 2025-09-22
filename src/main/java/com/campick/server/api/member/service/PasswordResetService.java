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

import java.time.LocalDateTime;
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

        passwordResetRepository.findByEmail(email).ifPresent(passwordResetRepository::delete);

//        String code = UUID.randomUUID().toString().replace("-", "");
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
    public void verifyCode(String code) {
        // 리세 코드를 만듦
        PasswordReset reset = passwordResetRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PASSWORD_RESET_INVALID_CODE.getMessage()));

        // 만료 시간 체크
        if (reset.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ErrorStatus.PASSWORD_RESET_EXPIRED_CODE.getMessage());
        }

        // 코드가 유효합지 체크
        if (reset.isVerified()) {
            throw new BadRequestException(ErrorStatus.PASSWORD_RESET_CODE_ALREADY_USED.getMessage());
        }
        
        reset.markVerified(); // 인증코드 사용 처리
        passwordResetRepository.save(reset);

    }

    // 3. 비밀번호 변경
    public void resetPassword(String email,  String newPassword){
        PasswordReset reset = passwordResetRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PASSWORD_RESET_INVALID_CODE.getMessage()));

        Member member = memberRepository.findByEmailAndIsDeletedFalse(reset.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage()));

        if(member.getPassword().equals(newPassword)){
            throw new BadRequestException(ErrorStatus.PASSWORD_RESET_INVALID_CODE.getMessage());
        }

//        member.changePassword(passwordEncoder.encode(newPassword));
        member.changePassword(newPassword);
        memberRepository.save(member);

        
    }
}
