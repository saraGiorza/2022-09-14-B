package it.polito.tdp.itunes.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {

	private ItunesDAO dao = new ItunesDAO();
	private Graph<Album, DefaultEdge> grafo;

	public void creaGrafo(double soglia) {
		grafo = new SimpleGraph<>(DefaultEdge.class);

		// vertici
		Graphs.addAllVertices(grafo, dao.getAllAlbumsDurata(soglia));
		
		//archi
		for(Album a1: grafo.vertexSet()) {
			for(Album a2: grafo.vertexSet()) {
				if(!a1.equals(a2)) {
					boolean comune = false;
					List<Integer> playlist1 = dao.playlistAlbum(a1);
					List<Integer> playlist2 = dao.playlistAlbum(a2);
					
					for(Integer i1: playlist1) {
						for(Integer i2: playlist2) {
							if(i1 == i2) {
								comune = true;
								break; //se ne trovo una, mi fermo
							}
						}
					}
					
					if(comune) {
						grafo.addEdge(a1, a2);
					}
					
				}
			}
		}

	}
	
	public List<Album> verticiGrafo(){
		List<Album> vertici = new ArrayList<>(grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}

	public String infoGrafo() {
		// se il grafo non è stato creato ritorna stringa vuota
		try {
			return "Grafo creato con " + grafo.vertexSet().size() + " vertici " + grafo.edgeSet().size() + " archi";
		} catch (NullPointerException npe) {
			return "";
		}
	}
	
	public String componenteConnessa(Album a) {
		String result = "";
		result+= "Componente connessa - " + a.getTitle() + "\n";
		ConnectivityInspector<Album, DefaultEdge> c = new ConnectivityInspector<>(grafo);
		result+= "Dimensione componente = " + c.connectedSetOf(a).size() + "\n";
		
		int aC = 0;
		for(Album album: c.connectedSetOf(a)) {
			aC+= dao.getTracksAlbum(album).size();
		}
		result+= "# Album componente = " + aC + "\n";
		return result;
	}

}
