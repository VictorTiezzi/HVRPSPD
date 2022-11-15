package data;

import java.util.Scanner;

public class Node implements Comparable< Node >{
	
	public int id;
	public double delivery;
	public double pickup;
	public double x;
	public double y;
	public boolean infeasible;

	public Double distanceNode;

	public Double getDistanceNode(){
		return distanceNode;
	}

	public Node(Scanner scanner) {
		id = scanner.nextInt();
		delivery = scanner.nextDouble();
		pickup = scanner.nextDouble();
		x = scanner.nextDouble();
		y = scanner.nextDouble();
	}

	@Override
	public int compareTo(Node o) {
		return this.getDistanceNode().compareTo(o.getDistanceNode());
	}
}

