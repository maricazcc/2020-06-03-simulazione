package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
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
	private int bestGrado;
	private List<Player> dreamTeam;
	
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

	public List<Player> getDreamTeam(int k) {
		this.bestGrado = 0;
		this.dreamTeam = new ArrayList<>();
		
		List<Player> parziale = new ArrayList<>();
		
		this.ricorsione(parziale, new ArrayList<Player>(this.grafo.vertexSet()), k);
		return dreamTeam;	
	}

	private void ricorsione(List<Player> parziale, List<Player> vertici, int k) {
		//caso terminale
		if(parziale.size() == k) {
			int grado = this.getGrado(parziale);
			if(grado > this.bestGrado) {
				dreamTeam = new ArrayList<>(parziale);
				bestGrado = grado;
			}
			return;
		}
		
		//caso generale
		for(Player p : vertici) {
			if(!parziale.contains(p)) {
				parziale.add(p);
				//Togliamo i battuti
				List<Player> giocatoriRimasti = new ArrayList<> (vertici);
				giocatoriRimasti.removeAll(Graphs.successorListOf(grafo, p));
				ricorsione(parziale, giocatoriRimasti, k);
				parziale.remove(p);
			}
		}
		
	}

	private int getGrado(List<Player> parziale) {
		int grado = 0;
		int in;
		int out;
		
		for(Player p : parziale) {
			in = 0;
			out = 0;
			
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(p))
				in += (int) this.grafo.getEdgeWeight(e);
			
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p))
				out += (int) this.grafo.getEdgeWeight(e);
			
			grado += (out-in);
		}
		
		return grado;
	}

	public int getBestGrado() {
		return bestGrado;
	}
	
}
