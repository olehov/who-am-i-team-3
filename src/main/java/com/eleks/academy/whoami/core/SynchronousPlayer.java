package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.model.request.CharacterSuggestion;

public interface SynchronousPlayer {

	String getUserName();

	String getCharacterSuggestion();
	
	void suggest(CharacterSuggestion suggestion);

	boolean isSuggest();

	String getNickName();

	String getGameCharacter();

	void setGameCharacter(String gameCharacter);

	boolean isCharacterAssigned();

}
