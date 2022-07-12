package com.eleks.academy.whoami.model.response;

import com.eleks.academy.whoami.core.SynchronousPlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePlayerModel {

	private String username;
	
	private String nickname;
	
	private String gameCharacter;
	
	private String characterSuggestion;
	
	private boolean isSuggested;
	
	private boolean isCharacterAssigned;

	public String getUserName(){
		return this.username;
	}
	
	public static BasePlayerModel of(SynchronousPlayer player) {
		return BasePlayerModel.builder()
				.username(player.getUserName())
				.nickname(player.getNickName())
				.gameCharacter(player.getGameCharacter())
				.characterSuggestion(player.getCharacterSuggestion())
				.isSuggested(player.isSuggest())
				.isCharacterAssigned(player.isCharacterAssigned())
				.build();
	}
}
