package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        
        // 입력 검증
        if (username == null || username.trim().isEmpty()) {
            response.put("error", "사용자 이름을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (password == null || password.length() < 4) {
            response.put("error", "비밀번호는 4자 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 중복 검사
        if (userRepository.existsByUsername(username)) {
            response.put("error", "이미 사용 중인 사용자 이름입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (email != null && !email.isEmpty() && userRepository.existsByEmail(email)) {
            response.put("error", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 간단한 해시 (실제로는 BCrypt 등을 사용해야 함)
            String hashedPassword = simpleHash(password);
            
            UserEntity user = new UserEntity(username, hashedPassword, email);
            userRepository.save(user);
            
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("username", username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            response.put("error", "사용자 이름과 비밀번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            response.put("error", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        UserEntity user = userOpt.get();
        String hashedPassword = simpleHash(password);
        
        if (!user.getPassword().equals(hashedPassword)) {
            response.put("error", "비밀번호가 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 로그인 시간 업데이트
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        response.put("message", "로그인 성공");
        response.put("username", username);
        
        return ResponseEntity.ok(response);
    }
    
    // 간단한 해시 함수 (실제로는 BCrypt 사용 권장)
    private String simpleHash(String password) {
        return String.valueOf(password.hashCode());
    }
}