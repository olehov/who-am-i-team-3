package com.eleks.academy.whoami.service.impl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.core.impl.PersistentGame;
import com.eleks.academy.whoami.model.request.NewGameRequest;
import com.eleks.academy.whoami.repository.impl.GameInMemoryRepository;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

	private final GameInMemoryRepository mockGameRepository = mock(GameInMemoryRepository.class);	
	private final GameServiceImpl gameService = new GameServiceImpl(mockGameRepository);
	private final NewGameRequest gameRequest = new NewGameRequest();
	
	@BeforeEach
	void setup() {
		gameRequest.setMaxPlayers(2);
	}
	
	@Test
	void leaveGameSuccessfulTest() {
		SynchronousGame game = new PersistentGame(4);
		Optional<SynchronousGame> gameById = Optional.of(game);

		when(mockGameRepository.findById(game.getId())).thenReturn(gameById);
		
		Assertions.assertDoesNotThrow(() -> gameService.leaveGame(game.getId(), "player"));
		
		verify(mockGameRepository, times(1)).findById(game.getId());
	}
	
	void leaveGameFailedWithNotFoundTest() {
		final String id = "542332";
		Optional<SynchronousGame> gameById = Optional.empty();
		
		when(mockGameRepository.findById(eq(id))).thenReturn(gameById);
		
		Assertions.assertThrows(ResponseStatusException.class, () -> gameService.leaveGame(id, "player"));
		
		verify(mockGameRepository, times(1)).findById(eq(id));
	}

}
