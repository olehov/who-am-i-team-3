package com.eleks.academy.whoami.core.impl;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;

public class PersistentPlayer implements SynchronousPlayer {

	private final String username;
	
	private final CharacterSuggestion characterSuggestion = new CharacterSuggestion();
	
	private boolean isSuggested = Boolean.FALSE;

	private String gameCharacter;
	
	private boolean isCharacterAssigned = Boolean.FALSE;
	
	public PersistentPlayer(String username, String nickName) {
		this.username = Objects.requireNonNull(username);
		this.characterSuggestion.setNickname(Objects.requireNonNull(nickName));
	}
	
	@Override
	public boolean isSuggest() {
		return isSuggested;
	}
	
	@Override
	public boolean isCharacterAssigned() {
		return isCharacterAssigned;
	}

	@Override
	public void setNickName(String nickname) {
		this.characterSuggestion.setNickname(nickname);
	}

	private void setCharacter(String character) {
		this.characterSuggestion.setCharacter(character);
	}
	
	@Override
	public String getUserName() {
		return this.username;
	}
	
	@Override
	public String getNickName() {
		return this.characterSuggestion.getNickname();
	}
	
	@Override
	public String getCharacterSuggestion() {
		return this.characterSuggestion.getCharacter();
	}
	
	@Override
	public String getGameCharacter() {
		return gameCharacter;
	}
	
	@Override
	public void setGameCharacter(String gameCharacter) {
		if (this.isCharacterAssigned == false) {
			this.isCharacterAssigned = Boolean.TRUE;
			this.gameCharacter = gameCharacter;
		}
	}
	
	@Override
	public void suggest(CharacterSuggestion suggestion) {
		if (this.isSuggested == false) {
			this.isSuggested = Boolean.TRUE;
			setNickName(suggestion.getNickname());
			setCharacter(suggestion.getCharacter());
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suggestion has already been submitted!");
		}
		
	}

}
