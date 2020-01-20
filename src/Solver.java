/**
 * Article: A Fast Randomized Algorithm for the Heterogeneous Vehicle Routing Problem with Simultaneous Pickup and Delivery  
 * Available online at https://doi.org/10.3390/a12080158
 */

import data.*;
import java.io.*;
import java.util.Locale;

public class Solver {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);

		String[] filenames = {"instance-50", "instance-100", "instance-350", "instance-550"};
/*		String[] filenames = {
			"instance101", "instance102", "instance103", "instance104", "instance105", "instance106", "instance107",
			"instance108", "instance109", "instance110", "instance111", "instance112", "instance113", "instance114",
			"instance201", "instance202", "instance203", "instance204", "instance205", "instance206", "instance207",
			"instance208", "instance209", "instance210", "instance211", "instance212", "instance213", "instance214"}; */
		int numberOfExecutions = 10;
		double[] rates = {0.00, 0.25, 0.50, 0.75, 0.90, 0.95, 0.99, 1.00};
		int timeLimit = 60;

		try {
			for (String filename: filenames) {
				String fileDirectory = "./solution/" + filename;
				new File(fileDirectory).mkdirs();			
				for (int exec = 1; exec <= numberOfExecutions; exec++) {
					for (double rate : rates) {
						Data data = new Data(filename);
						PrintStream printerEvolution = new PrintStream(fileDirectory + "/" + filename + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
						Greedy greedy = new Greedy(data, rate, timeLimit, printerEvolution);
						greedy.solution.exportSolution(fileDirectory + "/" + filename + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
						printerEvolution.close();
					}
				}
			}
		}
		catch (IOException exc1) {
			System.err.println(exc1);
		}
	}
}

