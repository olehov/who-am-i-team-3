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
import com.eleks.academy.whoami.core.impl.Answer;
import com.eleks.academy.whoami.core.impl.TurnInfo;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.request.QuestionAnswer;
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

	public Optional<TurnDetails> findTurnInfo(){
		TurnInfo currentTurn = turns.get(turns.size()-1);
		return Optional.ofNullable(TurnDetails.of(currentTurn));
	}

	public void askQuestion(SynchronousPlayer player, Question question){
		if(!player.getPlayerState().equals(PlayerState.ASKING)){
			throw new GameException("Player [" + player.getUserName() + "] not asker");
		}else{
			this.turns.add(new TurnInfo(player, question));
			player.setPlayerState(PlayerState.WAITING_ANSWERS);
		}
	}

	public void answerQuestion(SynchronousPlayer player, QuestionAnswer answer) {
		if(player.getPlayerState().equals(PlayerState.ANSWERED)){
			throw new GameException("Player [" + player.getUserName() + "] answered");
		}else if(!player.getPlayerState().equals(PlayerState.ANSWERING)){
			throw new GameException("Player [" + player.getUserName() + "] not answering question");
		}else {
			var turn = turns.get(turns.size() - 1);
			turn.addAnswer(PlayerWithState.of(player, answer));
			player.setPlayerState(PlayerState.ANSWERED);
			if(isReadyToNextPlayer()){
				changeTurn();
			}
		}
	}

	public void submitGuess(SynchronousPlayer player, Question guess){
		if(!player.getPlayerState().equals(PlayerState.ASKING)){
			throw new GameException("Player [" + player.getUserName() + "] not asker");
		}else{
			this.turns.add(new TurnInfo(player, guess));
			player.setPlayerState(PlayerState.WAITING_ANSWERS);
		}
	}

	public void answerGuess(SynchronousPlayer player, QuestionAnswer answer){
		if(player.getPlayerState().equals(PlayerState.ANSWERED)){
			throw new GameException("Player [" + player.getUserName() + "] answered");
		}else if(!player.getPlayerState().equals(PlayerState.ANSWERING)){
			throw new GameException("Player [" + player.getUserName() + "] not answering question");
		}else {
			var turn = turns.get(turns.size() - 1);
			turn.addAnswer(PlayerWithState.of(player, answer));
			player.setPlayerState(PlayerState.ANSWERED);
			if(!isWinner(turn)){
				changeTurn();
			}else{
				if(isReadyToNextState()){
					next();
				}
			}
		}
	}

	private Boolean isWinner(TurnInfo turn){
		if(turn.getAnswers().size() == players.size()){
			int positiveCount = 0;
			int negativeCount = 0;
				for (var answer : turn.getAnswers()) {
					if (answer.getAnswer().equals(QuestionAnswer.YES)) {
						positiveCount++;
					} else {
						negativeCount++;
					}
				}

				if(positiveCount >= negativeCount){
					changeTurn();
					players.remove(turn.getAsker());
					return true;
				}else {
					return false;
				}
			}else {
//			return true; if throw new GameException equals FALSE ------> uncomment
			throw new GameException("Not all players answered question");
		}

	}

	private Boolean isReadyToNextPlayer(){
		int positiveCount = 0;
		int negativeCount = 0;
		var turn = turns.get(turns.size() - 1);
		if(turn.getAnswers().size() == players.size()) {
			for (var answer : turn.getAnswers()) {
				if (answer.getAnswer().equals(QuestionAnswer.YES) || answer.getAnswer().equals(QuestionAnswer.NOT_SURE)) {
					positiveCount++;
				} else {
					negativeCount++;
				}
			}

			if(positiveCount >= negativeCount){
				players.get(turn.getAsker()).setPlayerState(PlayerState.ASKING);
				for(var player:players.values()){
					if(!player.getPlayerState().equals(PlayerState.ASKING)){
						player.setPlayerState(PlayerState.ANSWERING);
					}
				}
				return false;
			}else {
				return true;
			}
		}else{
			throw new GameException("Don't all players answer question");
		}
	}

	private void changeTurn(){
		SynchronousPlayer currentPlayer = turns.get(turns.size()-1).getAsker();
		int playerPosition = currentPlayerPosition(currentPlayer) + 1;
		if(playerPosition == players.size()){
			playerPosition = 0;
		}
		for(int i = 0; i < players.values().size(); i++){
			if(playerPosition == i){
				players.values().stream().toList().get(i).setPlayerState(PlayerState.ASKING);
			}else{
				players.values().stream().toList().get(i).setPlayerState(PlayerState.ANSWERING);
			}
		}
	}

	private int currentPlayerPosition(SynchronousPlayer currentPlayer){
		for(int i = 0; i < players.values().size(); i++){
			if(players.values().equals(currentPlayer)){
				return i;
			}
		}
		throw new GameException("Player [" + currentPlayer.getUserName() + "] position not found");
	}

}
