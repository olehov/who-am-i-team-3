package com.eleks.academy.whoami.core.impl;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.eleks.academy.whoami.core.Player;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;

public class PersistentPlayer implements Player, SynchronousPlayer {

	private final String username;
	
	private String nickname;
	private String character;
	private boolean value = Boolean.FALSE;
	
	private final CompletableFuture<String> characterFuture = new CompletableFuture<>();


	private Queue<String> questionQueue;
	private volatile CompletableFuture<String> question;
	private volatile CompletableFuture<String> currentAnswer;
	private volatile CompletableFuture<Boolean> readyForAnswerFuture;

	public PersistentPlayer(String username) {
		this.username = Objects.requireNonNull(username);
	}
	
//	@Override
//	public void setCharacter(String character) {
//		if (!this.characterFuture.complete(character)) {
//			throw new IllegalStateException("Character has already been suggested!");
//		}
//	}
	
	@Override
	public String getUserName() {
		return this.username;
	}
	
	@Override
	public String getNickName() {
		return this.nickname;
	}
	
	@Override
	public String getCharacter() {
		return this.character;
	}

	@Override
	public Future<String> suggestCharacter() {
		return characterFuture;
	}
	
	@Override
	public void suggest(CharacterSuggestion suggestion) {
		if (value == false) {
			value = Boolean.TRUE;
			setNickName(suggestion.getNickname());
			setCharacter(suggestion.getCharacter());
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Suggestion has already been submitted!");
		}
		
	}
	
	@Override
	public boolean isSuggest() {
		return value;
	}
	
	private void setNickName(String nickname) {
		this.nickname = nickname;
	}

	private void setCharacter(String character) {
		this.character = character;
	}
	
	@Override
	public Future<String> getQuestion() {
		return null;
	}

	@Override
	public Future<String> answerQuestion(String question, String character) {
		return null;
	}

	@Override
	public Future<String> getGuess() {
		return null;
	}

	@Override
	public Future<Boolean> isReadyForGuess() {
		return null;
	}

	@Override
	public Future<String> answerGuess(String guess, String character) {
		return null;
	}

	@Override
	public void close() {

	}

}
