package com.eleks.academy.whoami.model.chat;

import com.eleks.academy.whoami.model.request.QuestionAnswer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChatHistory {
    private final List<ChatAsk> askHistory = new ArrayList<>();

    @Setter
    private ChatAsk currentQuestion;

    public void addQuestion(String player, String type, String question) {
        this.currentQuestion = new ChatAsk(player, type, question);
        this.askHistory.add(this.currentQuestion);
    }

    public void addAnswer(String player, QuestionAnswer answer) {
        this.askHistory
                .stream()
                .filter(question -> question.equals(this.currentQuestion))
                .findAny()
                .ifPresent(question -> question.addAnswer(player, answer));
    }
}
