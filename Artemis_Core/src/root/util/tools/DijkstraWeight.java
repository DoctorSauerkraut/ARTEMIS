package root.util.tools;

import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * @author olivier
 * Used for message path building
 */
public class DijkstraWeight {
	public int weight;
	public NetworkAddress address;
	public ArrayList<NetworkAddress> parents;
	
	public DijkstraWeight(int weight, NetworkAddress addr) {
		address = addr;
		this.weight = weight;
		parents = new ArrayList<NetworkAddress>();
	}
}
