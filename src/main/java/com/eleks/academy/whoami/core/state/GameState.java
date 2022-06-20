package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;

import java.util.Optional;

public sealed interface GameState permits AbstractGameState {

	/**
	 * Used for presentation purposes only
	 *
	 * @return state of the current game
	 */
	GameState getCurrentState();
	
	/**
	 * Used for presentation purposes only
	 *
	 * @return the status of the current state to show to players
	 */
	String getStatus();
	
	/**
	 * Used for presentation purposes only
	 *
	 * @return {@code true} if meet all requirements to start a game
	 * or {@code false} if not
	 * 
	 */
	boolean isReadyToNextState();
	
	/*
	 * Switch to next game state if all tests passed 
	 *
	 * @return nextGameState
	 * @throw ResponseStatusException(400)
	 */
	GameState next();
	
	/**
	 * Used for presentation purposes only
	 *
	 * @return the count of the players currently in game
	 */
	int getPlayersInGame();
	
	/* @return player
	 * @throw ResponseStatusException(404) -> PlayerNotFoundException
	 */
	Optional<SynchronousPlayer> findPlayer(String player);

	/* @return player that was removed from game
	 * @throw ResponseStatusException(404) -> PlayerNotFoundException
	 */	
	Optional<SynchronousPlayer> remove(String player);
	
}
