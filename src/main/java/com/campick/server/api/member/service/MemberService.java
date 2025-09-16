package com.campick.server.api.member.service;

import com.campick.server.api.member.dto.MemberSignUpRequestDto;
import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.common.exception.BadRequestException;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private MemberRepository memberRepository;

    public void signUp(MemberSignUpRequestDto requestDto) {
        // 기존에 있는 이메일인지 확인하는 코드
        if(memberRepository.findByEmailAndIsDeletedFalse(requestDto.getEmail()).isPresent()){
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_ACCOUNT_EXCEPTION.getMessage());
        }
    }

//    public List<UserResponseDto> getAllUsers() {
//        return userRepository.findAll()
//                .stream()
//                .map(UserResponseDto::new)
//                .collect(Collectors.toList());
//    }
}
