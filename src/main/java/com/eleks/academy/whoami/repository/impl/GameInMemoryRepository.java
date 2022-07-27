package com.eleks.academy.whoami.repository.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.repository.GameRepository;

@Repository
public class GameInMemoryRepository implements GameRepository {

	private static final int DURATION = 3;
	private static final TimeUnit UNIT = TimeUnit.MINUTES;

	private final Map<String, SynchronousGame> games = new ConcurrentHashMap<>();

	private final Map<String, String> playersInGame = new ConcurrentHashMap<>();

	private final Map<String, String> allPlayers = new ConcurrentHashMap();


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
		return Optional.ofNullable(this.playersInGame.get(player));
	}

	@Override
	public void savePlayer(String player) {
		this.playersInGame.put(player, player);
	}

	@Override
	public void deletePlayerByHeader(String player) {
		this.playersInGame.remove(player);
		this.allPlayers.remove(player);
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

	private Future<String> getPlayer(String player){
		try {
			return CompletableFuture.completedFuture(this.playersInGame.get(player));
		}catch (NullPointerException e){
			return CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public int playersOnlineInfo() {
		return this.allPlayers.keySet().size();
	}

	@Override
	public void checkPlayerStatus(String player) {
//		String tempPlayer = null;
//		for (String p : allPlayers.keySet()) {
//			try {
//				for (String p2 : this.playersInGame.keySet()) {
//					if (p.equals(p2)) {
//						tempPlayer = p;
//					}
//				}
//			} catch (NullPointerException e) {
//				System.out.println("ERROR 404");
//				//this.allPlayers.remove(p);
//			}
//			this.allPlayers.remove(p);
//		}

//		try {
//			String p2 = getPlayer(player).get(DURATION,UNIT);
//			if(!allPlayers.get(player).equals(p2)){
//				this.allPlayers.remove(player);
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		} catch (TimeoutException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			if (tempPlayer == null) {
//				allPlayers.remove(player);
//			}
//		}catch (NullPointerException e){
//			System.out.println("ERROR 404");
//		}
	}

	@Override
	public String clearGames(String player) {
		this.playersInGame.clear();
		this.games.clear();
		this.allPlayers.clear();
		return "Games is cleared";
	}

	@Override
	public void savePlayersOnline(String player) {
		this.allPlayers.put(player, player);
	}

	@Override
	public void deleteGame(SynchronousGame game){
		this.games.remove(game.getId());
	}

	@Override
	public boolean findPlayerInGame(String player) {
		if(!this.allPlayers.isEmpty() && !this.playersInGame.isEmpty()){
			try {

//				if (this.allPlayers.get(player).equals(this.playersInGame.get(player))) {
//					return true;
//				}
			}catch (NullPointerException e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
