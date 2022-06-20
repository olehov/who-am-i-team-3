package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class WaitingForPlayers extends AbstractGameState {

	private final Map<String, SynchronousPlayer> players;

	public WaitingForPlayers(int maxPlayers) {
		super(0, maxPlayers);
		this.players = new HashMap<>(maxPlayers);
	}

	@Override
	public GameState next() {
		return Optional.of(this)
				.filter(WaitingForPlayers::isReadyToNextState)
				.map(then -> new SuggestingCharacters(this.players))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
	}

	@Override
	public Optional<SynchronousPlayer> findPlayer(String player) {
		return Optional.ofNullable(this.players.get(player));
	}

	@Override
	public int getPlayersInGame() {
		return this.players.size();
	}
	
	public SynchronousPlayer add(SynchronousPlayer player) {
		players.put(player.getUserName(), player);
		return player;
	}
	
	@Override
	public GameState getCurrentState() {
		return this;
	}

	@Override
	public boolean isReadyToNextState() {
		return players.size() == getMaxPlayers();
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {
		
		if (findPlayer(player).isPresent()) {
			return Optional.of(this.players.remove(player));
		} else throw new PlayerNotFoundException("[" + player + "] not found.");
	}
}
