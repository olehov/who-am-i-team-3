package com.eleks.academy.whoami.service;

import java.util.List;
import java.util.Optional;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.NewGameRequest;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.response.*;

public interface GameService {

	List<GameLight> findAvailableGames(String player);

	GameDetails createGame(String player, NewGameRequest gameRequest);

	SynchronousPlayer enrollToGame(String id, String player);

	Optional<GameDetails> findByIdAndPlayer(String id, String player);

	Optional<PlayerSuggestion> suggestCharacter(String id, String player, CharacterSuggestion suggestion);

	Optional<StartGameModel> startGame(String id, String player);

	void askQuestion(String id, String player, Question question);

	Optional<TurnDetails> findTurnInfo(String id, String player);

	void submitGuess(String id, String player, String guess);

	void answerQuestion(String id, String player, String answer);

	Optional<QuickGame> findQuickGame(String player);

	Optional<LeaveModel> leaveGame(String id, String player);

	List<AllFields> findAllGamesInfo(String player);

	void changePlayersOnline(String player, int playersOnline);

	Optional<Integer> playersInGame(String player, String id);

	Optional<Integer> playersOnlineInfo(String player);

	public String clearGame(String player);
	
}
