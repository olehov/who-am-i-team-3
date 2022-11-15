package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.GameException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;
import com.eleks.academy.whoami.core.impl.TurnInfo;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.response.PlayerState;
import com.eleks.academy.whoami.model.response.PlayerWithState;
import com.eleks.academy.whoami.model.response.TurnDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// TODO: Implement makeTurn(...) and next() methods, pass a turn to next player
public final class ProcessingQuestion implements GameState {

	private final String currentPlayer;

	private final Map<String, SynchronousPlayer> players;

	private final Map<String, String> playerCharacterMap;

	private final List<PlayerWithState> playersWithState;

	private final List<TurnInfo> turns;

	public ProcessingQuestion(Map<String, SynchronousPlayer> players) {
		this.players = players;
		this.playerCharacterMap = new ConcurrentHashMap<>();
		this.playersWithState = new ArrayList<>();
		this.turns = new ArrayList<>();

		this.currentPlayer = players.keySet()
				.stream()
				.findAny()
				.orElse(null);
	}

	@Override
	public GameState next() {
		return Optional.of(this)
				.filter(ProcessingQuestion::isReadyToNextState)
				.map(then -> new GameFinished(players.size()))
				.orElseThrow(() -> new GameException("Game is not finished"));
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
		return this.getClass().getName();
	}

	@Override
	public boolean isReadyToNextState() {
		if(players.size() == 1){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {
		if (findPlayer(player).isPresent()) {
			return Optional.ofNullable(this.players.remove(player));
		} else {
			throw new PlayerNotFoundException("[" + player + "] not found.");
		}
	}

	@Override
	public Stream<SynchronousPlayer> getPlayersList() {
		return this.players.values().stream();
	}

	@Override
	public int getPlayersInGame() {
		return this.players.size();
	}

	public void askQuestion(SynchronousPlayer player, Question question){
		if(!player.getPlayerState().equals(PlayerState.ASKING)){
			throw new GameException("Player [" + player.getUserName() + "] not asker");
		}else{
			this.turns.add(new TurnInfo(player, question));
			player.setPlayerState(PlayerState.WAITING_ANSWERS);
		}
	}
}
