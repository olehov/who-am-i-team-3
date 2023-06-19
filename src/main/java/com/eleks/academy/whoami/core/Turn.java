package com.eleks.academy.whoami.core;

import com.eleks.academy.whoami.core.impl.PersistentPlayer;

import java.util.List;

public interface Turn {

    PersistentPlayer getCurrentPlayer();

    List<PersistentPlayer> getOtherPlayers();

    Turn changeTurn();

    void removePLayer(String playerId);

    List<PersistentPlayer> getAllPlayers();

}
