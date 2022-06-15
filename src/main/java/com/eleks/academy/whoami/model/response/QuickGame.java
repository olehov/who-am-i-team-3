package com.eleks.academy.whoami.model.response;

import java.util.List;

import com.eleks.academy.whoami.core.SynchronousGame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickGame {
	
	private String id;
	
	private String status;
	
	private boolean accessibility;
	
	private List<String> players;
	
	private String turn;
	
	public static QuickGame of(SynchronousGame game) {
		return QuickGame.builder()
				.id(game.getId())
				.status(game.getStatus())
				.accessibility(game.isAvailable())
				.players(game.getPlayersInGame().stream().map(PlayerWithState::getPlayer).map(p -> p.getName()).toList())
				.turn(game.getTurn())
				.build();
	}

}
