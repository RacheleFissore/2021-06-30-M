package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private GenesDao dao;
	private List<Adiacenza> adiacenze;
	private List<Adiacenza> adiacenzeMagg;
	private Double pesoMin;
	private Double pesoMax;
	private List<Integer> best;
	private int mag = 0;
	private int min = 0;
	private Double pesoMaxCammino;
	
	public Model() {
		dao = new GenesDao();
		pesoMax = 0.0;
		pesoMin = 0.0;
	}
	
	public void creaGrafo() {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		adiacenze = new ArrayList<>();
		Graphs.addAllVertices(grafo, dao.getVertici());
		
		for(Adiacenza adiacenza : dao.getArchi()) {
			Graphs.addEdgeWithVertices(grafo, adiacenza.getVertice1(), adiacenza.getVertice2(), adiacenza.getPeso());
			adiacenze.add(adiacenza);
		}
		
		Collections.sort(adiacenze);
	}
	
	public Integer getNVertici() {
		return grafo.vertexSet().size();
	}
	 
	public Integer getNArchi() {
		return grafo.edgeSet().size();
	}
	
	public String pesoMinMax() {
		String string = "";
		pesoMin = adiacenze.get(0).getPeso();
		pesoMax = adiacenze.get(adiacenze.size()-1).getPeso();
		string += "Peso minimo: " + pesoMin + " Peso massimo: " + pesoMax;
		return string;
	}
	
	public String contaArchi(int soglia) {
		adiacenzeMagg = new ArrayList<>();
		String string = "";
		
		for(Adiacenza adiacenza : adiacenze) {
			if(adiacenza.getPeso() > soglia) {
				adiacenzeMagg.add(adiacenza);
				mag++;
			}				
			else {
				min++;
			}
		}
		
		string += "Soglia: " + soglia + " --> Maggiori " + mag + ", minori " + min;
		return string;
	}
	
	public String trovaSequenza(int soglia) {
		pesoMaxCammino = 0.0;
		String string = "";
		best = new ArrayList<>();
		List<Integer> parziale = new ArrayList<>();
		
		for(Integer vP : grafo.vertexSet()) {
			parziale.clear();
			parziale.add(vP);
			cerca(parziale, soglia, 0.0);
		}
		
		for(Integer v : best) {
			string += v + "\n";
		}
		
		return string;  
	}

	private void cerca(List<Integer> parziale, int soglia, Double peso) {
		
		if(pesoMaxCammino < peso) {
			pesoMaxCammino = peso;
			best = new ArrayList<>(parziale);
			//return;
		}
				
		for(Integer v : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			DefaultWeightedEdge edge = grafo.getEdge(parziale.get(parziale.size()-1), v);
			if(edge != null) {
				if(grafo.getEdgeWeight(edge) > soglia && !parziale.contains(v)) {
					parziale.add(v);
					peso += grafo.getEdgeWeight(edge);
					cerca(parziale, soglia, peso);
					peso -= grafo.getEdgeWeight(edge);
					parziale.remove(v);
				}
			}
			
		}
	}
}