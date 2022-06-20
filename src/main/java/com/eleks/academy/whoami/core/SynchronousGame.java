package com.eleks.academy.whoami.core;

import java.util.Optional;

public interface SynchronousGame {

	Optional<SynchronousPlayer> findPlayer(String player);

	String getId();

	SynchronousPlayer enrollToGame(String player);

	String getPlayersInGame();

	String getStatus();

	boolean isAvailable();

	SynchronousGame start();

	Optional<SynchronousPlayer> deletePlayerFromGame(String player);

}
