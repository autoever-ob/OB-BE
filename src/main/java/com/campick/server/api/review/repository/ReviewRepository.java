package com.campick.server.api.review.repository;

import com.campick.server.api.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTarget_Id(Long targetId);


    // 나한테 작성한 리뷰 멤버들을 모두 조인
    // Review 엔티티의 target 필드(Member)의 id를 기준으로 조회
    @Query("SELECT r FROM Review r JOIN FETCH r.author WHERE r.target.id = :targetId")
    List<Review> findByTargetIdWithAuthor(@Param("targetId") Long targetId);

    @Query(value = "SELECT r FROM Review r JOIN FETCH r.author WHERE r.target.id = :targetId",
            countQuery = "SELECT count(r) FROM Review r WHERE r.target.id = :targetId")
    Page<Review> findByTargetIdWithAuthor(@Param("targetId") Long targetId, Pageable pageable);
}
