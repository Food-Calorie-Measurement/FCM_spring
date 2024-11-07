package com.fcm.dto;

import lombok.Getter;

@Getter
public class loginRequestDTO {
    private String email;
    private String password;

    public loginRequestDTO() {}

    public loginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
