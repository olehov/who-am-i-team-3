package com.eleks.academy.whoami.controller;

import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.*;
import com.eleks.academy.whoami.model.response.*;
import com.eleks.academy.whoami.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.eleks.academy.whoami.utils.StringUtils.Headers.PLAYER;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

	private final GameService gameService;

	@GetMapping
	public List<GameLight> findAvailableGames(@RequestHeader(PLAYER) String player) {
		return this.gameService.findAvailableGames(player);
	}
	
	@GetMapping("/info")
	public List<AllFields> findAllGamesInfo(@RequestHeader(PLAYER) String player) {
		return this.gameService.findAllGamesInfo(player);
	}
	
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public GameDetails createGame(@RequestHeader(PLAYER) String player,
								  @Valid @RequestBody NewGameRequest gameRequest) {
		
		/*return this.gameService.createGame(player, gameRequest);*/
		return this.gameService.createGame(player,gameRequest);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GameDetails> findById(@PathVariable("id") String id,
												@RequestHeader(PLAYER) String player) {
		return this.gameService.findByIdAndPlayer(id, player)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/{id}/players")
	public SynchronousPlayer enrollToGame(@PathVariable("id") String id,
										  @RequestHeader(PLAYER) String player) {
		return this.gameService.enrollToGame(id, player);
	}

	@PostMapping("/{id}/characters")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PlayerSuggestion> suggestCharacter(@PathVariable("id") String id,
															@RequestHeader(PLAYER) String player,
															@Valid @RequestBody CharacterSuggestion suggestion) {
		
		return this.gameService.suggestCharacter(id, player, suggestion)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@GetMapping("/{id}/turn")
	public ResponseEntity<TurnDetails> findTurnInfo(@PathVariable("id") String id,
													@RequestHeader(PLAYER) String player) {
		
		return this.gameService.findTurnInfo(id, player)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PostMapping("/{id}")
	public ResponseEntity<StartGameModel> startGame(@PathVariable("id") String id,
												 @RequestHeader(PLAYER) String player) {
		return this.gameService.startGame(id, player)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/{id}/questions")
	public void askQuestion(@PathVariable("id") String id,
							@RequestHeader(PLAYER) String player, @RequestBody Question question) {
		
		this.gameService.askQuestion(id, player, question);
	}

	@PostMapping("/{id}/guess")
	public void submitGuess(@PathVariable("id") String id,
							@RequestHeader(PLAYER) String player, @RequestBody Message message) {
		
		this.gameService.submitGuess(id, player, message.getMessage());
	}

	@PostMapping("/{id}/answer")
	public void answerQuestion(@PathVariable("id") String id,
							   @RequestHeader(PLAYER) String player, @RequestBody QuestionAnswer answer) {
		
		this.gameService.answerQuestion(id, player, answer);

	}
	
	@PostMapping("/quick")
	public ResponseEntity<QuickGame> findQuickGame(@RequestHeader(PLAYER) String player) {
		
		return this.gameService.findQuickGame(player)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}/leave")
	public ResponseEntity<LeaveModel> leaveGame(@PathVariable("id") String id,
												@RequestHeader(PLAYER) String player) {

		return this.gameService.leaveGame(id, player)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/all-players-count")
	public ResponseEntity<Integer> playersOnlineInfo(@RequestHeader(PLAYER) String player) {
		return this.gameService.playersOnlineInfo(player)
				.map(ResponseEntity::ok)
				.orElseGet(()->ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/players-in-game")
	public ResponseEntity<Integer> playersInInline(@RequestHeader(PLAYER) String player, @PathVariable("id") String id){
		return this.gameService.playersInGame(player,id)
				.map(ResponseEntity::ok)
				.orElseGet(()->ResponseEntity.notFound().build());
	}

	@GetMapping("/clear")
	public String clearGames(@RequestHeader(PLAYER) String player){
		return this.gameService.clearGame(player);
	}
}
