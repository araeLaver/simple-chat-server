package com.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/guest")
    public ResponseEntity<Map<String, String>> guestLogin(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        
        String nickname = request.get("nickname");
        
        if (nickname == null || nickname.trim().isEmpty()) {
            response.put("error", "닉네임을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (nickname.length() > 20) {
            response.put("error", "닉네임은 20자 이하로 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            response.put("message", "게스트 로그인 성공");
            response.put("username", nickname);
            response.put("userType", "guest");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("게스트 로그인 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "게스트 로그인 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
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
        
        // 이메일 형식 검증 (선택사항)
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                response.put("error", "올바른 이메일 형식이 아닙니다.");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        // 중복 검사
        if (userRepository.existsByUsername(username)) {
            response.put("error", "이미 사용 중인 사용자 이름입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 간단한 해시 (실제로는 BCrypt 등을 사용해야 함)
            String hashedPassword = simpleHash(password);
            
            UserEntity user;
            if (email != null && !email.trim().isEmpty()) {
                user = new UserEntity(username, hashedPassword, email);
            } else {
                user = new UserEntity(username, hashedPassword);
            }
            
            userRepository.save(user);
            
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("username", username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("회원가입 오류: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
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
        
        // 마지막 로그인 시간 업데이트
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        response.put("message", "로그인 성공");
        response.put("userId", String.valueOf(user.getId()));
        response.put("username", username);
        response.put("email", user.getEmail());
        response.put("profileImage", user.getProfileImage());
        response.put("statusMessage", user.getStatusMessage());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile/update")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        String userId = request.get("userId");
        String profileImage = request.get("profileImage");
        String statusMessage = request.get("statusMessage");

        if (userId == null) {
            response.put("error", "사용자 ID가 필요합니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<UserEntity> userOpt = userRepository.findById(Long.parseLong(userId));

            if (userOpt.isEmpty()) {
                response.put("error", "존재하지 않는 사용자입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            UserEntity user = userOpt.get();

            if (profileImage != null) {
                user.setProfileImage(profileImage);
            }

            if (statusMessage != null) {
                user.setStatusMessage(statusMessage);
            }

            userRepository.save(user);

            response.put("message", "프로필이 업데이트되었습니다.");
            response.put("profileImage", user.getProfileImage());
            response.put("statusMessage", user.getStatusMessage());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("프로필 업데이트 오류: " + e.getMessage());
            response.put("error", "프로필 업데이트 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<UserEntity> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                response.put("error", "존재하지 않는 사용자입니다.");
                return ResponseEntity.badRequest().body(response);
            }

            UserEntity user = userOpt.get();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("profileImage", user.getProfileImage());
            userInfo.put("statusMessage", user.getStatusMessage());
            userInfo.put("lastLogin", user.getLastLogin());

            response.put("success", true);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "사용자 정보 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();

        try {
            var users = userRepository.findByUsernameContaining(keyword);

            response.put("success", true);
            response.put("users", users);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "사용자 검색 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // 간단한 해시 함수 (실제로는 BCrypt 사용 권장)
    private String simpleHash(String password) {
        return String.valueOf(password.hashCode());
    }
}