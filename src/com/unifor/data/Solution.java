package com.unifor.data;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Solution {
	
	public double totalCost;
	public double solvingTime;
	public long iteration;
	public List<Route> routes;
			
	public Solution(List<Route> routes, double solvingTime, long iteration) {
		this.routes = routes;
		this.totalCost = totalCost();
		this.solvingTime = solvingTime;
		this.iteration = iteration;
	}
	
	private double totalCost() {
		double cost = 0;
		for (int r = 0; r < routes.size(); r++) {
			Route route = routes.get(r);
			cost += route.totalCost;
		}
		return cost; 
	}
	
	public void exportSolution(String filename) throws IOException {
		PrintStream printer = new PrintStream(filename);
		printer.printf("%-15s%15.2f\n", "Total cost:", totalCost);
		printer.printf("%-15s%15.2f\n", "Solving time:", solvingTime);
		printer.printf("%-15s%15d\n", "Iteration:", iteration);
		printer.printf("%-15s%15d\n", "Types:", routes.size());
		printer.println("------------------------------");
		for (int r = 0; r < routes.size(); r++) {
			Route route = routes.get(r);
			printer.printf("%-15s%d\n", "Vehicle type:", route.vehicleType.id);
			printer.printf("%-15s%15.2f\n", "Route cost:", route.totalCost);
			printer.printf("%-15s", "Route: ");
			for (int n = 0; n < route.nodes.size(); n++) {
				Node node = route.nodes.get(n);
				printer.printf("%5d", node.id);
			}
			printer.printf("%5d", route.nodes.get(0).id);
			printer.println();
			printer.println("------------------------------");
		}
		printer.close();
	}
	
}
