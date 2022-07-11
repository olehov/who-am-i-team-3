//package com.eleks.academy.whoami.repository.impl;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Test;
//
//import com.eleks.academy.whoami.core.impl.PersistentGame;
//import com.eleks.academy.whoami.repository.GameRepository;
//
//class GameInMemoryRepositoryTest {
//
//	private final GameRepository gameRepository = new GameInMemoryRepository();
//
//	@Test
//	void findAvailableQuickGames_FindEmptyTest() {
//		assertThat(gameRepository.findAvailableQuickGames()).isNotNull().isEmpty();
//	}
//
//	@Test
//	void findAvailableQuickGames_FindGamesTest() {
//		gameRepository.save(new PersistentGame(4));
//
//		assertThat(gameRepository.findAvailableQuickGames()).isNotNull().isNotEmpty();
//	}
//
//}
