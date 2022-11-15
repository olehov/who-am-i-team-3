package com.eleks.academy.whoami.model.response;

import com.eleks.academy.whoami.core.SynchronousGame;
import com.eleks.academy.whoami.core.SynchronousPlayer;
import com.eleks.academy.whoami.model.request.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnDetails {

	private SynchronousPlayer currentPlayer;

	private Question question;

	private List<PlayerWithState> players;


	public static TurnDetails of(List<PlayerWithState> players, SynchronousPlayer player){
		return TurnDetails.builder()
				.currentPlayer(player)
				.players(players)
				.build();
	}

}
