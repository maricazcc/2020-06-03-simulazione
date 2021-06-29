package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(double g, Map<Integer, Player> idMap){
		String sql = "SELECT p.PlayerID AS id, p.Name AS nome "
				+ "FROM players p, actions a "
				+ "WHERE a.PlayerID = p.PlayerID "
				+ "GROUP BY id, nome "
				+ "HAVING AVG(a.Goals) > ?";
	
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, g);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				Player player = new Player(res.getInt("id"), res.getString("nome"));
				idMap.put(player.getPlayerID(), player);
			}
			
			conn.close();
					
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Adiacenza> getAdiacenze(Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID AS id1 , a2.PlayerID AS id2, (SUM(a1.TimePlayed) - SUM(a2.TimePlayed)) AS peso "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.PlayerID > a2.PlayerID "
				+ "AND a1.MatchID = a2.MatchID AND a1.TeamID != a2.TeamID "
				+ "AND a1.Starts = 1 AND a2.Starts = 1 "
				+ "GROUP BY id1, id2";
	
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				if(idMap.containsKey(res.getInt("id1")) && idMap.containsKey(res.getInt("id2"))) {
					Player p1 = idMap.get(res.getInt("id1"));
					Player p2 = idMap.get(res.getInt("id2"));
				    Adiacenza a = new Adiacenza(p1, p2, res.getInt("peso"));
				    result.add(a);
				}
			}
						
			conn.close();
			return result;
					
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
