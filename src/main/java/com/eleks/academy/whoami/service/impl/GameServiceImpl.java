package com.eleks.academy.whoami.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.eleks.academy.whoami.core.state.ProcessingQuestion;
import com.eleks.academy.whoami.core.state.WaitingForPlayers;
import com.eleks.academy.whoami.model.chat.ChatAsk;
import com.eleks.academy.whoami.model.request.Message;
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

	@Override
	public List<ChatAsk> viewHistory(String id, String player) {
		return this.gameRepository.findById(id).get().viewHistory();
	}

	/*
	 *TODO: check gameState
	 */
	@Override
	public void suggestCharacter(String id, String player, CharacterSuggestion suggestion) {

//        this.gameRepository.findById(id)
//                .filter(game -> game.getState() instanceof SuggestingCharacters)
//                .ifPresentOrElse(game -> game.suggestCharacter(player, suggestion),
//                        () -> {
//                            throw new GameNotFoundException("SUGGESTING-CHARACTERS: Game with id[" + id + "] not found.");
//                        }
//                );
		SynchronousGame game = this.gameRepository.findById(id).get();
		if(game.getState() instanceof SuggestingCharacters){
			game.suggestCharacter(player, suggestion);
		}else{
			throw new GameNotFoundException("SUGGESTING-CHARACTERS: Game with id[" + id + "] not found.");

		}



		//SynchronousPlayer inGamePlayer = gameRepository.findById(id).flatMap(game -> game.findPlayer(player)).get();
		//return Optional.of(inGamePlayer);
		//return Optional.of(PlayerSuggestion.of(inGamePlayer, suggestion));
	}

	@Override
	public Optional<StartGameModel> startGame(String id, String player) {
		return this.gameRepository.findById(id)
				.filter(g -> g.getState() instanceof ProcessingQuestion)
				.map(SynchronousGame::start)
				.map(StartGameModel::of);
	}

	@Override
	public void askQuestion(String gameId, String player, Message message) {
		this.gameRepository.findById(gameId)
				.filter(game -> game.getState() instanceof ProcessingQuestion)
				.ifPresentOrElse(game -> game.askQuestion(player, message.getMessage()),
						() -> {
							throw new GameNotFoundException("PROCESSING-QUESTION: Game with id[" + gameId + "] not found.");
						}
				);
//		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public Optional<TurnDetails> findTurnInfo(String id, String player) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public void submitGuess(String id, String player, String guess) {
		throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
	}

	@Override
	public void answerQuestion(String id, String player, String answer) {
		this.gameRepository.findById(id).get().answerQuestion(player, answer);
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
	public Optional<Integer> playersOnlineInfo(String player) {
		return Optional.ofNullable(this.gameRepository.playersOnlineInfo());
	}

	@Override
	public Optional<Integer> playersInGame(String player, String id) {
		return Optional.of(this.gameRepository.findById(id).get().getPlayersList().size());

	}

	@Override
	public String clearGame(String player){
		return this.gameRepository.clearGames(player);
	}

	@Override
	public void savePlayersOnline(String player) {
		this.gameRepository.savePlayersOnline(player);
	}

	@Override
	public boolean findPlayerInGame(String player) {
		return this.gameRepository.findPlayerInGame(player);
	}

	@Override
	public void checkPlayerStatus(String player) {
		this.gameRepository.checkPlayerStatus(player);
	}
}