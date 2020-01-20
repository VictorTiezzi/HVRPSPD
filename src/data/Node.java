package data;

import java.util.Scanner;

public class Node {
	
	public int id;
	public double delivery;
	public double pickup;
	public double x;
	public double y;
	public boolean infeasible;
		
	public Node(Scanner scanner) {
		id = scanner.nextInt();
		delivery = scanner.nextDouble();
		pickup = scanner.nextDouble();
		x = scanner.nextDouble();
		y = scanner.nextDouble();
	}
}

