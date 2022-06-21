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
public class BasePlayerModel {

	private SynchronousPlayer player;
	
	public static BasePlayerModel of(SynchronousPlayer player) {
		return BasePlayerModel.builder()
				.player(player)
				.build();
	}
}
