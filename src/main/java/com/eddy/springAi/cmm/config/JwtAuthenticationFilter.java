package com.eddy.springAi.cmm.config;


import com.eddy.springAi.cmm.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Cookie에서 JWT 토큰 가져오기
        String token = null;
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }


        // JWT 토큰 검증
        if (token != null && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);

            // 토큰 만료 시간이 임박한 경우 새 토큰 발급
            long expirationTime = jwtTokenUtil.getClaims(token).getExpiration().getTime();
            long currentTime = System.currentTimeMillis();

            // 5분 이하로 남으면 새 토큰 발급
            if (expirationTime - currentTime <= 5 * 60 * 1000) { // 5분
                String newToken = jwtTokenUtil.generateToken(username);
                Cookie newCookie = new Cookie("jwt", newToken);
                newCookie.setHttpOnly(true);
                newCookie.setPath("/");
                newCookie.setMaxAge(-1); // 세션 쿠키
                response.addCookie(newCookie);
            }

            // 사용자 인증 설정
            User principal = new User(username, "", java.util.Collections.emptyList());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }



        chain.doFilter(request, response);
    }
}
