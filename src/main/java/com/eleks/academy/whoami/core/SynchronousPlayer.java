package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.model.request.CharacterSuggestion;

public interface SynchronousPlayer {

	String getUserName();

	String getCharacter();
	
	void suggest(CharacterSuggestion suggestion);

//	void setCharacter(String character);

	boolean isSuggest();

	String getNickName();

}
