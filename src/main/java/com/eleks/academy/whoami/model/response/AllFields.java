package com.eleks.academy.whoami.model.response;

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
	
	private String playersInGame;
	
	public static AllFields of(SynchronousGame game) {
		return AllFields.builder()
				.id(game.getId())
				.status(game.getStatus())
				.accessibility(game.isAvailable())
				.playersInGame(game.getPlayersInGame())
				.build();
	}
}
