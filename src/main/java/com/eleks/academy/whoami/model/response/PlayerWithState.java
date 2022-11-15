package com.eleks.academy.whoami.model.response;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.request.QuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerWithState {

	private SynchronousPlayer player;

	private QuestionAnswer answer;

	public static PlayerWithState of(SynchronousPlayer synchronousPlayer, QuestionAnswer answer1){
		return PlayerWithState.builder()
				.player(synchronousPlayer)
				.answer(answer1)
				.build();
	}

}
