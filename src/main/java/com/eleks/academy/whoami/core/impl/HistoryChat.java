package com.eleks.academy.whoami.core.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class HistoryChat {
    @Getter
    private final List<HistoryQuestion> questions = new ArrayList<>();
    private HistoryQuestion currentQuestion;

    public void addQuestion(String question, String player) {
        final var historyQuestion = new HistoryQuestion(player, question);
        this.questions.add(historyQuestion);
        this.currentQuestion = historyQuestion;
    }

    public void addAnswer(String answer, String player) {
        this.questions
                .stream()
                .filter(question -> question.equals(this.currentQuestion))
                .findAny()
                .ifPresent(question -> question.addAnswer(answer, player));
    }

}

@Data
@NoArgsConstructor
class HistoryQuestion {
    private String player;
    private String question;
    private List<HistoryAnswer> answers = new ArrayList<>();

    public HistoryQuestion(String player, String question) {
        this.player = player;
        this.question = question;
    }

    public void addAnswer(String answer, String player) {
        this.answers.add(new HistoryAnswer(player, answer));
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class HistoryAnswer {
    private String player;
    private String answer;
}
