package com.chat;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 해싱 및 검증 유틸리티
 * BCrypt 알고리즘 사용 (카카오톡 수준의 보안)
 */
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * 비밀번호를 BCrypt로 해싱
     * @param plainPassword 평문 비밀번호
     * @return 해시된 비밀번호
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * 비밀번호 검증
     * @param plainPassword 입력된 평문 비밀번호
     * @param hashedPassword 저장된 해시 비밀번호
     * @return 일치 여부
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * 비밀번호 강도 검증
     * 카카오톡 수준: 최소 8자, 영문+숫자+특수문자 조합
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasLetter && hasDigit && hasSpecial;
    }
}
