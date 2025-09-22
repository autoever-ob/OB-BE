package com.campick.server.api.member.service;

import com.campick.server.api.member.entity.EmailVerification;
import com.campick.server.api.member.repository.EmailVerificationRepository;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.exception.UnauthorizedException;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

//    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MailSender mailSender;

    @Value("cnckddn0146@gmail.com")
    private String serviceEmail;

    public void sendVerificationEmail(String email, LocalDateTime requestedAt) {
        // 이메일이 중복인가 확인
        // 회원 탈퇴되지 않은 멤버가 있으면 이미 있는 계정
        if(memberRepository.findByEmailAndIsDeletedFalse(email).isPresent()){
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_ACCOUNT_EXCEPTION.getMessage());
        }

        // 이메일이 없다면
        // 기존에 존재하는 EmailVerification 테이블 값 삭제
        emailVerificationRepository.findByEmail(email)
                .ifPresent(emailVerificationRepository::delete);


        // 랜덤한 7자리 코드 발생
        String code = generateCode();
        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expirationTimeInSeconds(190)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(emailVerification);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(String.format("캠픽 <%s>",serviceEmail));
        mailMessage.setTo(email);
        mailMessage.setSubject("캠픽 회원가입 인증코드");
        mailMessage.setText(emailVerification.generateCodeMessage());
        mailSender.send(mailMessage);
    }

    public String generateCode(){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(7);

        // 랜던함 값을 얻기 위한 0-9 문자열 중 하나 선택
        String chars = "0123456789";
        for(int i=0; i < 7; i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }


    public void verifyEmail(String code, LocalDateTime requestedAt) {
        EmailVerification verification = emailVerificationRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(
                        ErrorStatus.WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage()));

        if (verification.isExpired(requestedAt)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage());
        }

        verification.setIsVerified(true);
        emailVerificationRepository.save(verification);
    }
}
