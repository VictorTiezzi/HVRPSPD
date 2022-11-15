package data;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Data {
	
	public String filename;
	public int nTypes;
	public int nNodes;
	public Type[] types;
	public Node[] nodes;

	public Data(String filename) throws IOException{
		
		this.filename = filename;

		Scanner scanner = new Scanner(new FileReader("./data/" + filename + ".dat"));
		
		nTypes = scanner.nextInt();
		types = new Type[nTypes];
		for (int t = 0; t < nTypes; t++) {
			types[t] = new Type(scanner);
		}

		nNodes = scanner.nextInt(); 
		
		nodes = new Node[nNodes];
		for (int n = 0; n < nNodes; n++) {
			nodes[n] = new Node(scanner);
		}
		
		scanner.close();
			
	}
	
}
