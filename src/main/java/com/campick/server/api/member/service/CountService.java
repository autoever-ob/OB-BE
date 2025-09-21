package com.campick.server.api.member.service;

import com.campick.server.api.member.entity.Member;
import com.campick.server.api.member.repository.MemberRepository;
import com.campick.server.api.product.entity.ProductStatus;
import com.campick.server.api.product.repository.ProductRepository;
import com.campick.server.common.exception.NotFoundException;
import com.campick.server.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public Integer getMemberProductAvailableCount(Long sellerId) {
        Member seller = memberRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );
        return productRepository.countProductsBySellerAndStatusIn(seller, List.of(ProductStatus.AVAILABLE, ProductStatus.RESERVED));
    }

    public Integer getMemberProductSoldCount(Long sellerId) {
        Member seller = memberRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );
        return productRepository.countProductsBySellerAndStatus(seller, ProductStatus.SOLD);
    }

    public Integer getMemberAllProductCount(Long sellerId) {
        Member seller = memberRepository.findById(sellerId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.MEMBER_NOT_FOUND.getMessage())
        );

        return productRepository.countProductsBySeller(seller);
    }
}
