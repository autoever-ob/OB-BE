//package com.campick.server.common.jwt;
//
//import com.campick.server.api.member.entity.Member;
//import com.campick.server.api.member.repository.MemberRepository;
//import com.campick.server.common.config.security.SecurityMember;
//import com.campick.server.common.exception.BadRequestException;
//import com.campick.server.common.response.ErrorStatus;
//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class JWTFilter extends OncePerRequestFilter {
//
//    private final JWTUtil jwtUtil;
//    private final MemberRepository memberRepository;
//
//    public JWTFilter(JWTUtil jwtUtil, MemberRepository memberRepository) {
//        this.jwtUtil = jwtUtil;
//        this.memberRepository = memberRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 헤더에서 access키에 담긴 토큰을 꺼냄
//        String accessToken = resolveToken(request);
//
//        // 토큰이 없다면 다음 필터로 넘김
//        if (accessToken == null) {
//
//            filterChain.doFilter(request, response);
//
//            return;
//        }
//
//        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
//        try {
//            jwtUtil.isExpired(accessToken);
//        } catch (ExpiredJwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json;charset=UTF-8");
//
//            PrintWriter writer = response.getWriter();
//            writer.write(ErrorStatus.EXPIRED_TOKEN_EXCEPTION.getMessage());
//            writer.flush();
//            return;
//        }
//
//        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(accessToken);
//
//        if (!category.equals("access")) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json;charset=UTF-8");
//
//            PrintWriter writer = response.getWriter();
//            writer.write(ErrorStatus.MALFORMED_ACCESS_TOKEN_EXCEPTION.getMessage());
//            writer.flush();
//            return;
//
//        }
//
//
//
//
//
//        Member member = memberRepository.findById(jwtUtil.getId(accessToken))
//                .orElseThrow(() -> new BadRequestException(ErrorStatus.NOT_REGISTER_USER_EXCEPTION.getMessage()));
//
//        if (member.getBlock() == 2) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            response.setContentType("application/json;charset=UTF-8");
//            PrintWriter writer = response.getWriter();
//            writer.write("{\"message\": \"" + ErrorStatus.MEMBER_BLOCKED.getMessage() + "\"}");
//            writer.flush();
//            return;
//        }
//
//        if (member.getBlock() == 1) {
//            LocalDateTime unblockDate = member.getBlockDate().plusDays(7);
//
//            if (unblockDate.isBefore(LocalDateTime.now())) {
//                member.unblock();
//            } else {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//                String formattedDate = unblockDate.format(formatter);
//
//                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                response.setContentType("application/json;charset=UTF-8");
//                PrintWriter writer = response.getWriter();
//                writer.write("{\"message\": \"정지된 회원입니다. 해제일: " + formattedDate + "\"}");
//                writer.flush();
//                return;
//            }
//        }
//
//
//        SecurityMember securityMember = SecurityMember.builder()
//                .id(member.getId())
//                .email(member.getEmail())
//                .password(member.getPassword())
//                .role(member.getRole())
//                .build();
//
//
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().name()));
//
//
//        Authentication authToken = new UsernamePasswordAuthenticationToken(securityMember, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
//
//
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearer = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
//        return null;
//    }
//
//}
