package com.campick.server.api.member.dto;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.entity.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignUpRequestDto {
    
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자리 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자리 이상이어야 합니다.")
    private String checkedPassword;
    
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
    private String nickname;
    
    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String mobileNumber;

    @NotNull(message = "Role을 지정해주세요 [ROLE_USER, ROLE_DEALER]")
    private Role role;

    private String dealershipName;
    private String dealershipRegistrationNumber;


    public Member toEntity(String encodedPassword){
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .mobileNumber(mobileNumber)
                .role(role)
                .profileImage("") // 추후 이미지 추가시에
                .description(null)
                .deletedAt(null)
                .isDeleted(false)
                .build();
    }

}
