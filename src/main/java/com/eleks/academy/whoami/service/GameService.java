package com.eleks.academy.whoami.service;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.chat.ChatHistory;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.Message;
import com.eleks.academy.whoami.model.request.NewGameRequest;
import com.eleks.academy.whoami.model.response.*;

public interface GameService {

	List<GameLight> findAvailableGames(String player);

	GameDetails createGame(String player, NewGameRequest gameRequest);

	SynchronousPlayer enrollToGame(String id, String player);

	Optional<GameDetails> findByIdAndPlayer(String id, String player);

	void suggestCharacter(String id, String player, CharacterSuggestion suggestion);

	Optional<StartGameModel> startGame(String id, String player);

	void askQuestion(String gameId, String player, Message message);

	Optional<TurnDetails> findTurnInfo(String id, String player);

	void submitGuess(String id, String player, String guess);

	void answerQuestion(String id, String player, String answer);

	Optional<QuickGame> findQuickGame(String player);

	Optional<LeaveModel> leaveGame(String id, String player);

	List<AllFields> findAllGamesInfo(String player);

	Optional<Integer> playersInGame(String player, String id);

	Optional<Integer> playersOnlineInfo(String player);

	String clearGame(String player);

	void savePlayersOnline(String player);

	boolean findPlayerInGame(String player);

	void checkPlayerStatus(String player);

	Optional<ChatHistory> viewHistory(String id, String player);
	
}
