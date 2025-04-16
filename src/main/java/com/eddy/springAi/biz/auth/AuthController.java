package com.eddy.springAi.biz.auth;

import com.eddy.springAi.cmm.util.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    // TODO: 실제 유저 저장소(UserRepository)와 인증 정보 필요
    public AuthController(JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        // TODO: 실제 사용자는 DB에서 조회 필요
        String dummyUsername = "user";
        String dummyPassword = passwordEncoder.encode("password");

        if (username.equals(dummyUsername) && passwordEncoder.matches(password, dummyPassword)) {
            String token = jwtTokenUtil.generateToken(username);

            // JWT를 쿠키로 설정
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true); // 클라이언트에서 JS로 접근 불가
            cookie.setMaxAge(-1); // 세션 쿠키 설정 (브라우저 종료 시 삭제)
            cookie.setPath("/"); // 모든 경로에서 쿠키 사용
            response.addCookie(cookie);

            return ResponseEntity.ok().body("Login Successful");
        }


        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshSession(HttpServletResponse response) {
        // 인증된 사용자 정보를 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username != null) {
            // JWT 토큰 재발급
            String newToken = jwtTokenUtil.generateToken(username);

            // 새 JWT를 쿠키에 추가
            Cookie cookie = new Cookie("jwt", newToken);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(-1); // 세션 쿠키 설정 (브라우저 종료 시 삭제)
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok().body("Session refreshed");
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

}
