package com.eddy.springAi.biz.auth;

import com.eddy.springAi.cmm.util.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        // TODO: 실제 사용자는 DB에서 조회 필요
        String dummyUsername = "user";
        String dummyPassword = passwordEncoder.encode("password");

        if (username.equals(dummyUsername) && passwordEncoder.matches(password, dummyPassword)) {
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok().body(token);
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
