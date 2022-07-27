package com.eleks.academy.whoami.model.chat;

import com.eleks.academy.whoami.model.request.QuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
public class ChatAsk {

    private String player;

    private String type;

    private String message;

    private final List<ChatAnswer> answers = new LinkedList<>();

    public ChatAsk(String player,String type, String message) {
        this.player = player;
        this.type = type;
        this.message = message;
    }

    public void addAnswer(String player, QuestionAnswer answer) {
        this.answers.add(new ChatAnswer(player, answer));
    }

}
