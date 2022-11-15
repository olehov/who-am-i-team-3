package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.request.QuestionAnswer;
import com.eleks.academy.whoami.model.response.PlayerWithState;
import com.eleks.academy.whoami.model.response.TurnDetails;

import java.util.List;

public interface Turn {
	
	SynchronousPlayer getAsker();

	Question getQuestion();

	List<PlayerWithState> getAnswers();

}
