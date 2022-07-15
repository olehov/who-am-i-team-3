package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class WaitingForPlayers implements GameState {

	private final int maxPlayers;
	private final Map<String, SynchronousPlayer> players;
	private List<SynchronousPlayer> playersOnline;

	public WaitingForPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
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
	public int getPlayersInGame() {
		this.playersOnline = this.players.values().stream().toList();
		return this.playersOnline.size();
	}

	private GameState setNextState(){
		return new SuggestingCharacters(this.players);
	}

	@Override
	public Stream<SynchronousPlayer> getPlayersList() {
		return this.players.values().stream();
	}

	@Override
	public GameState getCurrentState() {
		return this;
	}

	@Override
	public String getStatus() {
		return this.getClass().getName();
	}

	public static void main(String[] args) {
		WaitingForPlayers w = new WaitingForPlayers(4);
		System.out.println(w.getStatus());
	}

	@Override
	public Optional<SynchronousPlayer> findPlayer(String player) {
		return Optional.ofNullable(this.players.get(player));
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {

		if (findPlayer(player).isPresent()) {
			return Optional.ofNullable(this.players.remove(player));
		} else {
			throw new PlayerNotFoundException("[" + player + "] not found.");
		}
	}

	public SynchronousPlayer add(SynchronousPlayer player) {
		players.put(player.getUserName(), player);
		playersOnline = players.values().stream().toList();
		return player;
	}

	@Override
	public boolean isReadyToNextState() {
		return players.size() == maxPlayers;
	}

}
