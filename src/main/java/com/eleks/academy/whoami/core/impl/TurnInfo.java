package com.eleks.academy.whoami.core.impl;

import com.eleks.academy.whoami.core.Player;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.Turn;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.response.PlayerWithState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TurnInfo implements Turn {

    private SynchronousPlayer asker;
    private Question question;
    private List<PlayerWithState> answers;

    public TurnInfo(SynchronousPlayer asker, Question question) {
        this.asker = asker;
        this.question = question;
        this.answers = new ArrayList<>();
    }

    public void addAnswer(PlayerWithState answer){
        answers.add(answer);
    }
}
