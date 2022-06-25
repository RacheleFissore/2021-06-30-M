package it.polito.tdp.genes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.genes.model.Adiacenza;
import it.polito.tdp.genes.model.Genes;


public class GenesDao {
	
	public List<Genes> getAllGenes(){
		String sql = "SELECT DISTINCT GeneID, Essential, Chromosome FROM Genes";
		List<Genes> result = new ArrayList<Genes>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Genes genes = new Genes(res.getString("GeneID"), 
						res.getString("Essential"), 
						res.getInt("Chromosome"));
				result.add(genes);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}
	
	public List<Integer> getVertici() {
		String sql = "SELECT DISTINCT Chromosome "
				+ "FROM genes "
				+ "WHERE Chromosome > 0";
		
		List<Integer> result = new ArrayList<Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getInt("Chromosome"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
	}

	public List<Adiacenza> getArchi() {
		String sql = "SELECT DISTINCT g1.Chromosome AS c1, g2.Chromosome AS c2, SUM(DISTINCT i.Expression_Corr) AS peso "
				+ "FROM genes AS g1, genes AS g2, interactions AS i "
				+ "WHERE g1.Chromosome <> g2.Chromosome "
				+ "AND g1.Chromosome > 0 "
				+ "AND g2.Chromosome > 0 "
				+ "AND (g1.GeneID, g2.GeneID) IN (SELECT GeneID1, GeneID2 FROM interactions) "
				+ "AND (g1.GeneID, g2.GeneID) = (i.GeneID1, i.GeneID2) "
				+ "GROUP BY g1.Chromosome, g2.Chromosome";
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(new Adiacenza(res.getInt("c1"), res.getInt("c2"), res.getDouble("peso")));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e) ;
		}
		
	}
	
	
	
	
}
