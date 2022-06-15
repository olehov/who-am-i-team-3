package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract sealed class AbstractGameState implements GameState
		permits SuggestingCharacters, WaitingForPlayers, ProcessingQuestion, GameFinished {

	private final int playersInGame;
	private final int maxPlayers;

	// TODO: Implement for each state
	@Override
	public String getStatus() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @return {@code null} as default implementation
	 */
	public String getCurrentTurn() {
		return null;
	}
	
	@Override
	public SynchronousPlayer add(SynchronousPlayer player) {
		return player;
	}
}
