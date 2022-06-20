package com.eleks.academy.whoami.model.response;

import com.eleks.academy.whoami.core.SynchronousPlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveModel {
	
	private String username;
	
	private String gameId;
	
	public static LeaveModel of(SynchronousPlayer player, String gameId) {
		return LeaveModel.builder()
				.username(player.getUserName())
				.gameId(gameId)
				.build();
	}
}
