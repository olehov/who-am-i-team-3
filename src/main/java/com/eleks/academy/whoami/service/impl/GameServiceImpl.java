package com.eleks.academy.whoami.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.impl.PersistentGame;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.NewGameRequest;
import com.eleks.academy.whoami.model.response.GameDetails;
import com.eleks.academy.whoami.model.response.GameLight;
import com.eleks.academy.whoami.model.response.QuickGame;
import com.eleks.academy.whoami.model.response.TurnDetails;
import com.eleks.academy.whoami.repository.GameRepository;
import com.eleks.academy.whoami.service.GameService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

	private final GameRepository gameRepository;

	@Override
	public List<GameLight> findAvailableGames(String player) {
		return this.gameRepository.findAllAvailable(player)
				.map(GameLight::of)
				.toList();
	}

	@Override
	public GameDetails createGame(String player, NewGameRequest gameRequest) {
		final var game = this.gameRepository.save(new PersistentGame(player, gameRequest.getMaxPlayers()));

		return GameDetails.of(game);
	}

	@Override
	public SynchronousPlayer enrollToGame(String id, String player) {
		
		if (gameRepository.findPlayerByHeader(player).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		} else gameRepository.savePlayer(player);
		
		final SynchronousGame game = gameRepository.findById(id).get();
		
		return game.enrollToGame(player);
				
	}

	@Override
	public Optional<GameDetails> findByIdAndPlayer(String id, String player) {
		return this.gameRepository.findById(id)
				.filter(game -> game.findPlayer(player).isPresent())
				.map(GameDetails::of);
	}

	@Override
	public void suggestCharacter(String id, String player, CharacterSuggestion suggestion) {
		this.gameRepository.findById(id)
				.flatMap(game -> game.findPlayer(player))
				.ifPresent(p -> p.setCharacter(suggestion.getCharacter()));
	}

	@Override
	public Optional<GameDetails> startGame(String id, String player) {
		return this.gameRepository.findById(id)
				.map(SynchronousGame::start)
				.map(GameDetails::of);
	}

	@Override
	public void askQuestion(String gameId, String player, String message) {
		this.gameRepository.findById(gameId)
				.ifPresent(game -> game.askQuestion(player, message));
	}

	@Override
	public Optional<TurnDetails> findTurnInfo(String id, String player) {
		return Optional.empty();
	}

	@Override
	public void submitGuess(String id, String player, String guess) {

	}

	@Override
	public void answerQuestion(String id, String player, String answer) {

	}

	@Override
	public Optional<QuickGame> findQuickGame(String player) {
		
		Map<String, SynchronousGame> games = gameRepository.findAvailableQuickGames();
		
		if (games.isEmpty()) {
			final SynchronousGame game = gameRepository.save(new PersistentGame(4));
			enrollToGame(game.getId(), player);
			return gameRepository.findById(game.getId()).map(QuickGame::of); 
		}
		
		var FirstGame = games.keySet().stream().findFirst().get();
		enrollToGame(games.get(FirstGame).getId(), player);
		return gameRepository.findById(games.get(FirstGame).getId()).map(QuickGame::of);
	}

	@Override
	public void leaveGame(String id, String player) {
		
		if (this.gameRepository.findPlayerByHeader(player).isPresent()) {
			this.gameRepository.findById(id)
					.ifPresentOrElse(game -> game.deletePlayerFromGame(player), 
							() -> {
								throw new ResponseStatusException(HttpStatus.NOT_FOUND);
							}
					);
			this.gameRepository.deletePlayerByHeader(player);
		} else throw new ResponseStatusException(HttpStatus.CONFLICT);
	}
	
}
