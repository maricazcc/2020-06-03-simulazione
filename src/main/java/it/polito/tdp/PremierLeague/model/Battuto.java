package it.polito.tdp.PremierLeague.model;

public class Battuto implements Comparable <Battuto>{
	private Player player;
	private int peso;
	
	public Battuto(Player player, int peso) {
		this.player = player;
		this.peso = peso;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}

	@Override
	public int compareTo(Battuto o) {

		return -(this.peso - o.peso);
	}

	@Override
	public String toString() {
		return player.toString() + " | " + peso;
	}
	
	
	
	
	
	

}
