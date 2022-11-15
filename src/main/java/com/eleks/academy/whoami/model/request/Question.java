package com.eleks.academy.whoami.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @NotNull(message = "Question must not be null")
    private String question;
}
