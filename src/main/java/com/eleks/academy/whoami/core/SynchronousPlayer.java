package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.response.PlayerState;

public interface SynchronousPlayer {

	String getUserName();

	String getCharacterSuggestion();
	
	void suggest(CharacterSuggestion suggestion);

	boolean isSuggest();

	String getNickName();

	String getGameCharacter();

    PlayerState getPlayerState();

	void setPlayerState(PlayerState playerState);

	void setGameCharacter(String gameCharacter);

	boolean isCharacterAssigned();

}
