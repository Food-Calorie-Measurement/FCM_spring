package com.fcm;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptService {
    public String encodeBcrypt(String planeText, int strength) {
        return new BCryptPasswordEncoder(strength).encode(planeText);
    }

    public boolean matchesBcrypt(String planeText, String hashValue, int strength) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(strength);
        return passwordEncoder.matches(planeText, hashValue);
    }
}
