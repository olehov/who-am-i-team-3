package com.eleks.academy.whoami.model.chat;

import com.eleks.academy.whoami.model.request.QuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAnswer {

    private String player;

    private QuestionAnswer answer;


}
