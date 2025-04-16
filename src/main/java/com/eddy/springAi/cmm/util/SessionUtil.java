package com.eddy.springAi.cmm.util;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {

    /** 세션에서 값 가져오기 (값이 없으면 null 반환) */
    public Object getAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        return (session != null) ? session.getAttribute(key) : null;
    }

    /** 세션에 값 저장 */
    public void setAttribute(HttpServletRequest request, String key, Object value) {
        HttpSession session = request.getSession(true); // 세션이 없으면 생성
        session.setAttribute(key, value);
    }

    /** 세션 무효화 (로그아웃 시 호출) */
    public void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /** 세션에 저장된 사용자 이름 가져오기 */
    public String getUsername(HttpServletRequest request) {
        Object username = getAttribute(request, "username");
        return (username != null) ? username.toString() : null;
    }
}
