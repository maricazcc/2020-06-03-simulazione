package it.polito.tdp.PremierLeague.model;

import java.util.List;

public class TopPlayer {
	private Player player;
	private List<Battuto> battuti;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<Battuto> getBattuti() {
		return battuti;
	}

	public void setBattuti(List<Battuto> battuti) {
		this.battuti = battuti;
	}
	
	

}
