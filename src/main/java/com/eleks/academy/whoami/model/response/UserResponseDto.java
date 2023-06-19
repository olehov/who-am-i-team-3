package com.eleks.academy.whoami.model.response;

import lombok.Data;

@Data
public class UserResponseDto {

    private long id;

    private String nickname;

    private String email;

}
