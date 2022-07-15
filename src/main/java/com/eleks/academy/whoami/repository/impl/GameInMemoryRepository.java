package com.eleks.academy.whoami.repository.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eleks.academy.whoami.model.response.HomePageInfo;
import org.springframework.stereotype.Repository;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.repository.GameRepository;

@Repository
public class GameInMemoryRepository implements GameRepository {

	private final Map<String, SynchronousGame> games = new ConcurrentHashMap<>();

	private final Map<String, String> players = new ConcurrentHashMap<>();

	private final HomePageInfo homeInfo = new HomePageInfo();

	@Override
	public Stream<SynchronousGame> findAllAvailable(String player) {
		Predicate<SynchronousGame> freeToJoin = SynchronousGame::isAvailable;

//		Predicate<SynchronousGame> playersGame = game ->
//				game.findPlayer(player).isPresent();

		return this.games.values()
				.stream()
				.filter(freeToJoin);

	}

	public Stream<SynchronousGame> findAllGames(String player) {
		return this.games.values().stream();
	}

	@Override
	public SynchronousGame save(SynchronousGame game) {
		this.games.put(game.getId(), game);

		return game;
	}

	@Override
	public Optional<SynchronousGame> findById(String id) {
		return Optional.ofNullable(this.games.get(id));
	}

	@Override
	public Optional<String> findPlayerByHeader(String player) {
		return Optional.ofNullable(this.players.get(player));
	}

	@Override
	public void savePlayer(String player) {
		this.players.put(player, player);
	}

	@Override
	public void deletePlayerByHeader(String player) {
		this.players.remove(player);
	}

	@Override
	public Map<String, SynchronousGame> findAvailableQuickGames() {
		return filterByValue(games, SynchronousGame::isAvailable);
	}

	private static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate) {
		return map.entrySet()
				.stream()
				.filter(entry -> predicate.test(entry.getValue()))
				.collect(Collectors.toConcurrentMap(Entry::getKey, Entry::getValue));
	}

	@Override
	public void changePlayersOnline(int playersOnline) {
		this.homeInfo.setPlayersOnline(playersOnline);
	}

	@Override
	public int playersOnlineInfo() {
		return this.players.size();
	}

	@Override
	public String clearGames(String player) {
		this.homeInfo.setPlayersOnline(0);
		this.players.clear();
		this.games.clear();
		return "Games is cleared";
	}
}
