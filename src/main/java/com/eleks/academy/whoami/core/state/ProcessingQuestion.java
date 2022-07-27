package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.GameException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;
import com.eleks.academy.whoami.core.impl.StartGameAnswer;
import com.eleks.academy.whoami.model.chat.ChatHistory;
import com.eleks.academy.whoami.model.response.PlayerState;
import com.eleks.academy.whoami.model.response.PlayerWithState;

// TODO: Implement makeTurn(...) and next() methods, pass a turn to next player
public final class ProcessingQuestion implements GameState {

	private static final String QUESTION = "QUESTION";

	private static final String GUESS = "GUESS";

	private final ChatHistory chatHistory = new ChatHistory();

	private final String currentPlayer;

	private final Map<String, PlayerWithState> players;

	private final Map<String, String> playerCharacterMap;

	public ProcessingQuestion(Map<String, PlayerWithState> players) {
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
	public Optional<PlayerWithState> findPlayer(String player) {
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
		return this.getClass().toString();
	}

	@Override
	public boolean isReadyToNextState() {
		return false;
	}

	@Override
	public Optional<SynchronousPlayer> remove(String player) {
		if (findPlayer(player).isPresent()) {
			return Optional.ofNullable(this.players.remove(player).getPlayer());
		} else {
			throw new PlayerNotFoundException("[" + player + "] not found.");
		}
	}

	@Override
	public Stream<PlayerWithState> getPlayersList() {
		return this.players.values().stream();
	}

	@Override
	public int getPlayersInGame() {
		return players.size();
	}

	public void askQuestion(String player, String question) {
		PlayerWithState playerWithState = players.get(currentPlayer);
		if (playerWithState.getState().equals(PlayerState.ASKING)) {
			StartGameAnswer gameAnswer = new StartGameAnswer(player);
			gameAnswer.setMessage(question);
			this.chatHistory.addQuestion(player, QUESTION, question);
		}else {
			throw new GameException("Player " + player + " doesn't ask question in this turn");
		}
	}
}
