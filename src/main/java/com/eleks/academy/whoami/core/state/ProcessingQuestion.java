package com.eleks.academy.whoami.core.state;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.GameException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;
import com.eleks.academy.whoami.core.impl.StartGameAnswer;
import com.eleks.academy.whoami.model.chat.ChatAsk;
import com.eleks.academy.whoami.model.chat.ChatHistory;
import com.eleks.academy.whoami.model.request.QuestionAnswer;
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

		for (int i = 0; i < this.players.size(); i++) {
			if(i==0){
				this.players.values().stream().toList().get(i).setState(PlayerState.ASKING);
			}else{
				this.players.values().stream().toList().get(i).setState(PlayerState.ANSWERING);
			}
		}
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
		PlayerWithState playerWithState = players.get(player);
		if (playerWithState.getState().equals(PlayerState.ASKING)) {
			this.chatHistory.addQuestion(player, QUESTION, question);
		}else {
			throw new GameException("Player " + player + " doesn't ask question in this turn");
		}
	}

	public void answerQuestion(String player, String answer){
		PlayerWithState playerWithState = players.get(player);
		if (playerWithState.getState().equals(PlayerState.ANSWERING)) {
			switch(answer) {
				case "YES":
					this.chatHistory.addAnswer(player, QuestionAnswer.YES);
					break;
				case "NO":
					this.chatHistory.addAnswer(player, QuestionAnswer.NO);
					break;
				case "NOT_SURE":
					this.chatHistory.addAnswer(player, QuestionAnswer.NOT_SURE);
					break;
				default:
					throw new GameException("Answer is not accepted");
			}

			//this.chatHistory.addAnswer(player, answer);
		}else {
			throw new GameException("Player " + player + " doesn't answer question in this turn but his ask this question");
		}
	}

	public List<ChatAsk> viewHistory(){
		return this.chatHistory.getAskHistory();
	}
}
