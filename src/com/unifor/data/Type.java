package com.unifor.data;

import java.util.Scanner;

public class Type {
	
	public int id;
	public int capacity;
	public double variableCost;
	public int fixedCost;
		
	public Type(Scanner scanner) {
		id = scanner.nextInt();
		capacity = scanner.nextInt();
		variableCost = scanner.nextDouble();
		fixedCost = scanner.nextInt();
	}
	
}

