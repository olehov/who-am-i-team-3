package com.eleks.academy.whoami.model.response;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
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

	private CharacterSuggestion character;

	private PlayerState state;

	public static PlayerWithState of(SynchronousPlayer synchronousPlayer, QuestionAnswer answer, PlayerState state){
		return PlayerWithState.builder()
				.player(synchronousPlayer)
				.answer(answer)
				.state(state)
				.build();
	}

	public static PlayerWithState of(SynchronousPlayer synchronousPlayer, CharacterSuggestion character, PlayerState state){
		return PlayerWithState.builder()
				.player(synchronousPlayer)
				.character(character)
				.state(state)
				.build();
	}

}
