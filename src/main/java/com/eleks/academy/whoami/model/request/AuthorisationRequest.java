package com.eleks.academy.whoami.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorisationRequest {
    @NotNull
    @NotBlank(message = "Please fill all the fields")
    @Pattern(regexp = "^\\w+[\\w-\\.]@\\w+((-\\w+)|(\\w))\\.[a-z]{2,3}$")
    @Pattern(regexp = "^[\\S]{8,32}$")
    private String email;

    @NotNull
    @NotBlank(message = "Please fill all the fields")
    @Pattern(regexp = "^[\\S]{8,50}$")
    private String password;
}
