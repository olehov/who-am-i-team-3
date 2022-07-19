package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.GameException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// TODO: Implement makeTurn(...) and next() methods, pass a turn to next player
public final class ProcessingQuestion implements GameState {

	private final String currentPlayer;

	private final Map<String, SynchronousPlayer> players;

	private final Map<String, String> playerCharacterMap;

	public ProcessingQuestion(Map<String, SynchronousPlayer> players) {
		this.players = players;
		this.playerCharacterMap = new ConcurrentHashMap<>();

		this.currentPlayer = players.keySet()
				.stream()
				.findAny()
				.orElse(null);
	}

	@Override
	public GameState next() {
		throw new GameException("Not implemented");
	}

	@Override
	public Optional<SynchronousPlayer> findPlayer(String player) {
		return Optional.ofNullable(this.players.get(player));
	}

	public Map<String, String> getMap() {
		return this.playerCharacterMap;
	}

	public String getCurrentTurn() {
		return this.currentPlayer;
	}

	@Override
	public GameState getCurrentState() {
		return this;
	}

	@Override
	public String getStatus() {
		return null;
	}

	@Override
	public boolean isReadyToNextState() {
		return false;
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {
		throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}

	@Override
	public Stream<SynchronousPlayer> getPlayersList() {
		return this.players.values().stream();
	}

	@Override
	public int getPlayersInGame() {
		return 0;
	}
}
