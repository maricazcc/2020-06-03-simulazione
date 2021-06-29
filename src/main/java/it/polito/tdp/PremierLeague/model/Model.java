package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge>grafo;
	private Map <Integer, Player> idMap;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}	

	public String creaGrafo(double g) {
		this.grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		
		//Aggiungo vertici
		this.dao.getVertici(g, idMap);		
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo archi
		for(Adiacenza a: this.dao.getAdiacenze(idMap)) {
			if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
				
				if(a.getPeso()>0)
		         Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				
				else if(a.getPeso()<0)
					Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), ((double) -1)* a.getPeso());
			}
		}
		
		return String.format("Grafo creato con %d vertici e %d archi.\n", 
				this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}

	public Graph<Player, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public TopPlayer getTopPlayer() {
		Player top = null;
		Integer max = Integer.MIN_VALUE;
		
		for(Player p : this.grafo.vertexSet()) {
			if(grafo.outDegreeOf(p) > max) {
				max = grafo.outDegreeOf(p);
				top = p;
			}
		}
		
		TopPlayer topPlayer = new TopPlayer();
		topPlayer.setPlayer(top);
		List<Battuto> battuti = new ArrayList<>();
		
		for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(topPlayer.getPlayer())) {
			battuti.add(new Battuto(grafo.getEdgeTarget(e), (int) grafo.getEdgeWeight(e)));
		}
		
		Collections.sort(battuti);
		topPlayer.setBattuti(battuti);
		return topPlayer;
	}
	
	

}
