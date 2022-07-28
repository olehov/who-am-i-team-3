package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.model.request.CharacterSuggestion;

public interface SynchronousPlayer {

	String getUserName();

	String getCharacterSuggestion();
	
	void suggest(CharacterSuggestion suggestion);

	void setNickName(String nickName);

	boolean isSuggest();

	void setSuggested(boolean suggested);

	String getNickName();

	String getGameCharacter();

	void setGameCharacter(String gameCharacter);

	boolean isCharacterAssigned();

}
