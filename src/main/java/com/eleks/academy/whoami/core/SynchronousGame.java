package com.eleks.academy.whoami.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eleks.academy.whoami.core.state.GameState;
import com.eleks.academy.whoami.model.chat.ChatHistory;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.response.BasePlayerModel;
import com.eleks.academy.whoami.model.response.PlayerWithState;

public interface SynchronousGame {

	Optional<PlayerWithState> findPlayer(String player);

	String getId();

	SynchronousPlayer enrollToGame(String player);

	String getPlayersInGame();

	String getStatus();

	boolean isAvailable();

	void suggestCharacter(String player, CharacterSuggestion suggestion);

	SynchronousGame start();

	void askQuestion(String player, String question);

	Optional<SynchronousPlayer> deletePlayerFromGame(String player);

	List<PlayerWithState> getPlayersList();

	List<PlayerWithState> getPlayersListInGame();

	GameState getState();

	ChatHistory viewHistory();

	Map<String, String> getMap();

	void setState(GameState state);

	void answerQuestion(String player, String answer);
}
