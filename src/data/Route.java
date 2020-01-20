package data;

import java.util.List;

public class Route {
	
	public Type vehicleType;
	public List<Node> nodes;
	public double totalCost;
	public double variableCost;
	public int fixedCost;
		
	public Route(Type vehicleType, List<Node> nodes) {
		this.vehicleType = vehicleType;
		this.nodes = nodes;
		this.fixedCost = vehicleType.fixedCost;
		this.variableCost = variableCost();
		this.totalCost = this.fixedCost + this.variableCost;
	}
	
	private double variableCost() {
		Link link;
		double cost = 0;
		for (int i = 0; i < nodes.size() - 1; i++) {
			link = new Link (nodes.get(i), nodes.get(i+1), false);
			cost += link.distance * vehicleType.variableCost;
		}
		link = new Link (nodes.get(nodes.size()-1), nodes.get(0), false);
		cost += link.distance * vehicleType.variableCost;
		return cost; 
	}
	
}

