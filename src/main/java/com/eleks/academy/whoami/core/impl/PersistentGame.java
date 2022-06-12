package com.eleks.academy.whoami.core.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import com.eleks.academy.whoami.core.Game;
import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.state.GameFinished;
import com.eleks.academy.whoami.core.state.GameState;
import com.eleks.academy.whoami.core.state.WaitingForPlayers;
import com.eleks.academy.whoami.model.response.PlayerWithState;

public class PersistentGame implements Game, SynchronousGame {

	private final String id;
	
	private int maxPlayers;
	
	private List<PlayerWithState> gamePlayers = new CopyOnWriteArrayList<>();

	private final Queue<GameState> turns = new LinkedBlockingQueue<>();

	/**
	 * Creates a new game (game room) and makes a first enrolment turn by a current player
	 * so that he won't have to enroll to the game he created
	 *
	 * @param hostPlayer player to initiate a new game
	 */
	public PersistentGame(String hostPlayer, Integer maxPlayers) {
		this.id = String.format("%d-%d",
				Instant.now().toEpochMilli(),
				Double.valueOf(Math.random() * 999).intValue());

	}
	
	public PersistentGame(Integer maxPlayers) {
		this.id = String.format("%d-%d",
				Instant.now().toEpochMilli(),
				Double.valueOf(Math.random() * 999).intValue());

		this.maxPlayers = maxPlayers;
		this.turns.add(new WaitingForPlayers(this.maxPlayers));
	}

	@Override
	public Optional<SynchronousPlayer> findPlayer(String player) {
		return this.applyIfPresent(this.turns.peek(), gameState -> gameState.findPlayer(player));
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public SynchronousPlayer enrollToGame(String player) {
		var newPlayer = turns.peek().add(new PersistentPlayer(player));
		gamePlayers.add(new PlayerWithState(newPlayer, null, null));
		return newPlayer;
	}
	
	@Override
	public void deletePlayerFromGame(String player) {
		turns.peek().remove(player);
		gamePlayers.removeIf(p -> p.getPlayer().getName().contentEquals(player));
	}
	
	@Override
	public String getTurn() {
		return this.applyIfPresent(this.turns.peek(), GameState::getCurrentTurn);
	}

	@Override
	public void askQuestion(String player, String message) {

	}

	@Override
	public void answerQuestion(String player, Answer answer) {
		// TODO: Implement method
	}

	@Override
	public SynchronousGame start() {
		return null;
	}

	@Override
	public boolean isAvailable() {
		if (gamePlayers.size() == maxPlayers && turns.peek() instanceof WaitingForPlayers) {
			turns.add(turns.poll().next());
		}
		return gamePlayers.size() < maxPlayers && turns.peek() instanceof WaitingForPlayers; 
	}

	@Override
	public String getStatus() {
		return this.applyIfPresent(this.turns.peek(), GameState::getStatus);
	}

	@Override
	public List<PlayerWithState> getPlayersInGame() {
		// TODO: Implement
		return gamePlayers;
	}

	@Override
	public boolean isFinished() {
		return this.turns.isEmpty();
	}


	@Override
	public boolean makeTurn() {
		return true;
	}

	@Override
	public void changeTurn() {

	}

	@Override
	public void initGame() {

	}

	@Override
	public void play() {
		while (!(this.turns.peek() instanceof GameFinished)) {
			this.makeTurn();
		}
	}

	private <T, R> R applyIfPresent(T source, Function<T, R> mapper) {
		return this.applyIfPresent(source, mapper, null);
	}

	private <T, R> R applyIfPresent(T source, Function<T, R> mapper, R fallback) {
		return Optional.ofNullable(source)
				.map(mapper)
				.orElse(fallback);
	}

}
