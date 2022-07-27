package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;

import java.util.*;
import java.util.stream.Stream;

import com.eleks.academy.whoami.model.response.PlayerState;
import com.eleks.academy.whoami.model.response.PlayerWithState;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class WaitingForPlayers implements GameState {

	private final int maxPlayers;
	private final Map<String, PlayerWithState> players;
	private List<PlayerWithState> playersOnline;
	//private boolean isAvailableToNextState = false;

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
	public Stream<PlayerWithState> getPlayersList() {
		List<SynchronousPlayer> synchronousPlayerList = new LinkedList<>();
		players.values().forEach(p->synchronousPlayerList.add(p.getPlayer()));
		//return synchronousPlayerList.stream();
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
	public Optional<PlayerWithState> findPlayer(String player) {
		return Optional.ofNullable(this.players.get(player));
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {

		if (findPlayer(player).isPresent()) {
			return Optional.ofNullable(this.players.remove(player).getPlayer());
		} else {
			throw new PlayerNotFoundException("[" + player + "] not found.");
		}
	}

	public SynchronousPlayer add(SynchronousPlayer player) {
		this.players.put(player.getUserName(),
				new PlayerWithState(player, null, PlayerState.NOT_READY));
		return player;
	}

	@Override
	public boolean isReadyToNextState() {
		//this.isAvailableToNextState = playersOnline.size() == maxPlayers;
		return playersOnline.size() == maxPlayers;
	}

}
