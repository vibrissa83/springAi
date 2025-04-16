package com.eddy.springAi.biz.auth.service;

import com.eddy.springAi.cmm.util.CookieUtil;
import com.eddy.springAi.cmm.util.JwtTokenUtil;
import com.eddy.springAi.cmm.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증 및 사용자 세션 관리를 담당하는 서비스 클래스.
 * 로그인, 로그아웃, 세션 갱신 등의 기능을 제공합니다.
 */
@Service
public class AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final SessionUtil sessionUtil;
    private final CookieUtil cookieUtil;

    // 실제 사용자 정보를 관리하는 곳 (예시용)
    private final String DUMMY_USERNAME = "user";
    private final String DUMMY_PASSWORD = "password";

    /**
     * AuthService 생성자
     *
     * @param jwtTokenUtil    JWT 토큰 처리를 위한 유틸리티
     * @param passwordEncoder 비밀번호 암호화 및 검증을 위한 암호화 객체
     * @param sessionUtil     세션 관리 유틸리티
     * @param cookieUtil      쿠키 관리 유틸리티
     */
    public AuthService(JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder,
                       SessionUtil sessionUtil, CookieUtil cookieUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.sessionUtil = sessionUtil;
        this.cookieUtil = cookieUtil;
    }

    /**
     * 사용자의 로그인 요청을 처리합니다.
     *
     * @param username 사용자가 입력한 사용자 이름
     * @param password 사용자가 입력한 비밀번호
     * @param request  클라이언트의 요청 객체
     * @param response 클라이언트의 응답 객체 (쿠키 설정에 사용)
     * @return 로그인 성공 시 true, 실패 시 false
     */
    public boolean login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        // 사용자 정보 검증
        if (!username.equals(DUMMY_USERNAME) || !passwordEncoder.matches(password, passwordEncoder.encode(DUMMY_PASSWORD))) {
            return false;
        }

        // 세션 생성 및 사용자 이름 저장
        sessionUtil.setAttribute(request, "username", username);

        // JWT 생성 및 쿠키 설정
        String token = jwtTokenUtil.generateToken(username);
        cookieUtil.addCookie(response, "jwt", token, true, -1); // 세션 쿠키

        return true;
    }

    /**
     * 사용자의 로그아웃을 처리합니다.
     * - 세션을 무효화합니다.
     * - JWT 쿠키를 삭제합니다.
     *
     * @param request  클라이언트의 요청 객체 (세션 관리에 사용)
     * @param response 클라이언트의 응답 객체 (쿠키 삭제에 사용)
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        sessionUtil.invalidate(request); // 세션 무효화
        cookieUtil.deleteCookie(response, "jwt"); // JWT 쿠키 삭제
    }

    /**
     * 현재 세션 정보를 기반으로 JWT 토큰을 새로 발급합니다.
     *
     * @param request  클라이언트의 요청 객체 (세션에서 사용자 정보 추출)
     * @param response 클라이언트의 응답 객체 (JWT 쿠키 재설정)
     * @return 세션 갱신 성공 시 true, 실패 시 false
     */
    public boolean refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String username = sessionUtil.getUsername(request);
        if (username == null) {
            return false; // 세션 정보 없음
        }

        // JWT 재발급 및 쿠키 갱신
        String newToken = jwtTokenUtil.generateToken(username);
        cookieUtil.addCookie(response, "jwt", newToken, true, -1); // 세션 쿠키

        return true;
    }
}
