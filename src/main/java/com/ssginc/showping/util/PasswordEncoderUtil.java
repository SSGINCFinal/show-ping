package com.ssginc.showping.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "1234";
        String encodedPasswordUser = encoder.encode(rawPassword);
        String encodedPasswordAdmin = encoder.encode(rawPassword);

        System.out.println("INSERT INTO users (username, password) VALUES ('user', '" + encodedPasswordUser + "');");
        System.out.println("INSERT INTO users (username, password) VALUES ('admin', '" + encodedPasswordAdmin + "');");
    }
}

