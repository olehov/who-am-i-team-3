package com.eleks.academy.whoami.core.impl;

import com.eleks.academy.whoami.core.Turn;
import com.eleks.academy.whoami.core.exception.TurnException;
import com.eleks.academy.whoami.enums.PlayerState;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TurnImpl implements Turn {

    private final List<PersistentPlayer> players;
    private PersistentPlayer currentPlayer;
    private Queue<PersistentPlayer> orderedPlayers;

    public TurnImpl(List<PersistentPlayer> players) {
        Function<PersistentPlayer, Integer> randomComparator = value ->
                Double.valueOf(Math.random() * 1000).intValue();

        this.players = players
                .stream()
                .sorted(Comparator.comparing(randomComparator))
                .collect(Collectors.toList());

        this.orderedPlayers = new LinkedList<>(this.players);

        this.currentPlayer = this.orderedPlayers.poll();
    }

    private TurnImpl(List<PersistentPlayer> players, Queue<PersistentPlayer> orderedPlayers) {
        this.players = players;
        this.orderedPlayers = orderedPlayers;

        if (orderedPlayers.size() == 0) {
            throw new TurnException("No players left");
        }
        this.currentPlayer = this.orderedPlayers.poll();
        if (currentPlayer != null) {
            currentPlayer.setPlayerState(PlayerState.ASK_QUESTION);
        }
    }

    @Override
    public PersistentPlayer getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public List<PersistentPlayer> getOtherPlayers() {
        return orderedPlayers.stream().toList();
    }

    @Override
    public List<PersistentPlayer> getAllPlayers() {
        return players;
    }

    public List<PersistentPlayer> getPlayers() {
        return players;
    }

    @Override
    public Turn changeTurn() {
        this.currentPlayer.setPlayerState(PlayerState.ANSWER_QUESTION);
        this.orderedPlayers.add(this.currentPlayer);
        return new TurnImpl(this.players, this.orderedPlayers);
    }

    @Override
    public void removePLayer(String playerId) {
        this.players.removeIf(player -> player.getId().equals(playerId));

        this.orderedPlayers.removeIf(player -> player.getId().equals(playerId));

        if (this.currentPlayer != null) {
            if (this.currentPlayer.getId().equals(playerId)) {
                this.currentPlayer = this.orderedPlayers.poll();
                if (this.currentPlayer != null) {
                    this.currentPlayer.setPlayerState(PlayerState.ASK_QUESTION);
                }
            }
        }
    }

}
