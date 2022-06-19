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
public class AllFields {

private String id;
	
	private String status;
	
	private boolean accessibility;
	
	private boolean readyToStart;
	
	private List<PlayerWithState> players;
	
	private String turn;
	
	public static AllFields of(SynchronousGame game) {
		return AllFields.builder()
				.id(game.getId())
				.status(game.getStatus())
				.accessibility(game.isAvailable())
				.readyToStart(game.isReadyToStart())
				.players(game.getPlayersInGame())
				.turn(game.getTurn())
				.build();
	}
}
