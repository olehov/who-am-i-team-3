package com.eleks.academy.whoami.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eleks.academy.whoami.core.exception.GameException;
import com.eleks.academy.whoami.core.impl.PersistentPlayer;
import com.eleks.academy.whoami.core.state.ProcessingQuestion;
import com.eleks.academy.whoami.core.state.WaitingForPlayers;
import com.eleks.academy.whoami.model.request.Question;
import com.eleks.academy.whoami.model.request.QuestionAnswer;
import com.eleks.academy.whoami.model.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.core.exception.GameNotFoundException;
import com.eleks.academy.whoami.core.exception.PlayerAlreadyInGameException;
import com.eleks.academy.whoami.core.exception.PlayerNotFoundException;
import com.eleks.academy.whoami.core.impl.PersistentGame;
import com.eleks.academy.whoami.core.state.SuggestingCharacters;
import com.eleks.academy.whoami.model.request.CharacterSuggestion;
import com.eleks.academy.whoami.model.request.NewGameRequest;
import com.eleks.academy.whoami.repository.GameRepository;
import com.eleks.academy.whoami.service.GameService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

	private final GameRepository gameRepository;

	@Override
	public List<GameLight> findAvailableGames(String player) {
		return this.gameRepository.findAllAvailable(player).map(GameLight::of).toList();
	}

	@Override
	public GameDetails createGame(String player, NewGameRequest gameRequest) {
		final var game = this.gameRepository.save(new PersistentGame(player, gameRequest.getMaxPlayers()));

		return GameDetails.of(game);
	}

	//TODO: implement validations for create custom game
	@Override
	public SynchronousPlayer enrollToGame(String id, String player) {

		if (this.gameRepository.findPlayerByHeader(player).isPresent()) {
			throw new PlayerAlreadyInGameException("ENROLL-TO-GAME: [" + player + "] already in other game.");
		} else this.gameRepository.savePlayer(player);
		return this.gameRepository.findById(id).get().enrollToGame(player);
	}

	@Override
	public Optional<GameDetails> findByIdAndPlayer(String id, String player) {
		return this.gameRepository.findById(id).filter(game -> game.findPlayer(player).isPresent())
				.map(GameDetails::of);
	}

	/*
	 *TODO: check gameState
	 */
	@Override
	public Optional<PlayerSuggestion> suggestCharacter(String id, String player, CharacterSuggestion suggestion) {

		this.gameRepository.findById(id)
				.filter(g -> !g.isAvailable() && g.getState() instanceof SuggestingCharacters)
				.map(game -> game.findPlayer(player))
				.ifPresentOrElse(p -> p.ifPresentOrElse(then -> then.suggest(suggestion),
								() -> {
									throw new PlayerNotFoundException("SUGGESTING_CHARACTERS: [" + player + "] in game with id[" + id + "] not found.");
								}
						),
						() -> {
							throw new GameNotFoundException("SUGGESTING_CHARACTERS: Game with id[" + id + "] not found.");
						}
				);

		PlayerSuggestion inGamePlayer = (PlayerSuggestion) gameRepository.findById(id).flatMap(game -> game.findPlayer(player)).get();
		return Optional.ofNullable(inGamePlayer);
	}

	@Override
	public Optional<StartGameModel> startGame(String id, String player) {
		return this.gameRepository.findById(id)
				.filter(g -> g.getState().isReadyToNextState() && g.getState() instanceof SuggestingCharacters)
				.map(SynchronousGame::start)
				.map(StartGameModel::of);
	}

	@Override
	public void askQuestion(String id, String player, Question question) {
		Optional<SynchronousGame> game = this.gameRepository.findById(id);
		if (game.isPresent() && (game.get().getState() instanceof ProcessingQuestion)){
			if (game.get().findPlayerInGame(player)) {
				((ProcessingQuestion) game.get().getState()).askQuestion(game.get().findPlayer(player).get(),question);
			}else {
				throw new PlayerNotFoundException("Player [" + player + "] not found");
			}
		}else {
			throw new GameNotFoundException("Game with id:" + id + " not found");
		}
	}

	@Override
	public Optional<TurnDetails> findTurnInfo(String id, String player) {
		Optional<SynchronousGame> game = this.gameRepository.findById(id);
		if (game.isPresent() && (game.get().getState() instanceof ProcessingQuestion)) {
			return ((ProcessingQuestion) game.get().getState()).findTurnInfo();
		} else {
			throw new GameNotFoundException("Game with id:" + id + " not found");
		}
	}

	@Override
	public void submitGuess(String id, String player, Question guess) {
		Optional<SynchronousGame> game = this.gameRepository.findById(id);
		if (game.isPresent() && (game.get().getState() instanceof ProcessingQuestion)){
			if (game.get().findPlayerInGame(player)) {
				((ProcessingQuestion) game.get().getState()).submitGuess(game.get().findPlayer(player).get(),guess);
			}else {
				throw new PlayerNotFoundException("Player [" + player + "] not found");
			}
		}else {
			throw new GameNotFoundException("Game with id:" + id + " not found");
		}
	}

	@Override
	public void answerGuess(String id, String player, QuestionAnswer answer) {
		Optional<SynchronousGame> game = this.gameRepository.findById(id);
		if (game.isPresent() && (game.get().getState() instanceof ProcessingQuestion)){
			if (game.get().findPlayerInGame(player)) {
				((ProcessingQuestion) game.get().getState()).answerGuess(game.get().findPlayer(player).get(),answer);
			}else {
				throw new PlayerNotFoundException("Player [" + player + "] not found");
			}
		}else {
			throw new GameNotFoundException("Game with id:" + id + " not found");
		}
	}

	@Override
	public void answerQuestion(String id, String player, QuestionAnswer answer) {
		Optional<SynchronousGame> game = this.gameRepository.findById(id);
		if (game.isPresent() && (game.get().getState() instanceof ProcessingQuestion)){
			if (game.get().findPlayerInGame(player)) {
				((ProcessingQuestion) game.get().getState()).answerQuestion(game.get().findPlayer(player).get(),answer);
			}else {
				throw new PlayerNotFoundException("Player [" + player + "] not found");
			}
		}else {
			throw new GameNotFoundException("Game with id:" + id + " not found");
		}
	}

	@Override
	public Optional<QuickGame> findQuickGame(String player) {
		
		if (!this.gameRepository.findPlayerByHeader(player).isPresent()) {
			Map<String, SynchronousGame> games = gameRepository.findAvailableQuickGames();
			
			if (games.isEmpty()) {
				
				final SynchronousGame game = gameRepository.save(new PersistentGame(4));
				enrollToGame(game.getId(), player);
				
				return gameRepository.findById(game.getId()).map(QuickGame::of);
			}
			
			var FirstGame = games.keySet().stream().findFirst().get();
			enrollToGame(games.get(FirstGame).getId(), player);
			
			return gameRepository.findById(games.get(FirstGame).getId()).map(QuickGame::of);
		} else throw new PlayerAlreadyInGameException("QUICK-GAME: [" + player + "] already in other game.");
	}

	@Override
	public Optional<LeaveModel> leaveGame(String id, String player) {
		
		if (this.gameRepository.findPlayerByHeader(player).isPresent()) {

			var game = this.gameRepository.findById(id);
			
			if (game.isPresent() && (game.get().getState() instanceof WaitingForPlayers || game.get().getState() instanceof SuggestingCharacters)) {
				SynchronousPlayer synchronousPlayer = game.get().deletePlayerFromGame(player).get();
				this.gameRepository.deletePlayerByHeader(player);

				if(game.get().getPlayersInGame().equals("0")){
					this.gameRepository.deleteGame(game.get());
				}

				return Optional.of(LeaveModel.of(synchronousPlayer, id));
				
			} else throw new GameNotFoundException("Game with id[" + id + "] not found.");
			
		} else throw new PlayerNotFoundException("[" + player + "] in game with id[" + id + "] not found.");

	}

	@Override
	public List<AllFields> findAllGamesInfo(String player) {
		return this.gameRepository.findAllGames(player).map(AllFields::of).toList();
	}

	@Override
	public void changePlayersOnline(String player, int playersOnline) {
		if(playersOnline <= 0){
			this.gameRepository.changePlayersOnline(0);
		}else {
			this.gameRepository.changePlayersOnline(playersOnline);
		}
	}

	@Override
	public Optional<Integer> playersOnlineInfo(String player) {
		return Optional.ofNullable(this.gameRepository.playersOnlineInfo());
	}

	@Override
	public Optional<Integer> playersInGame(String player, String id) {
		return Optional.ofNullable(this.gameRepository.findById(id).get().getPlayersList().size());
	}

	@Override
	public String clearGame(String player){
		return this.gameRepository.clearGames(player);
	}
}
