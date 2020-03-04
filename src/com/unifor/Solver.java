package com.unifor;
import com.unifor.data.*;

import java.io.*;
import java.util.Locale;

/**
 * Article: A Fast Randomized Algorithm for the Heterogeneous Vehicle Routing Problem with Simultaneous Pickup and Delivery
 * Available online at https://doi.org/10.3390/a12080158
 * @author Nepomuceno
 */
public class Solver {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        String[] filenames = {"instance-550"};
        /*String[] filenames = {
                "instance101", "instance102", "instance103", "instance104", "instance105", "instance106", "instance107",
                "instance108", "instance109", "instance110", "instance111", "instance112", "instance113", "instance114",
                "instance201", "instance202", "instance203", "instance204", "instance205", "instance206", "instance207",
                "instance208", "instance209", "instance210", "instance211", "instance212", "instance213", "instance214"};*/
        int numberOfExecutions = 1;
        double[] rates = {0.99};
        int timeLimit = 5;

        try {
            for (String filename : filenames) {
                String fileDirectory = "./solution/" + filename;
                new File(fileDirectory).mkdirs();
                for (int exec = 1; exec <= numberOfExecutions; exec++) {
                    for (double rate : rates) {
                        Data data = new Data(filename);
                        PrintStream printerEvolution;
                        int greedyTestNum = 1;
/*
                        printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
                        Greedy2 greedy2 = new Greedy2(greedyTestNum, data, rate, timeLimit, printerEvolution);
                        greedy2.solution.exportSolution(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");



                        printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
                        Greedy greedy = new Greedy(data, rate, timeLimit, printerEvolution);
                        greedy.solution.exportSolution(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");

                        greedyTestNum++;
*/

                        printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
                        SemiGreeedy semiGreeedy = new SemiGreeedy(data, timeLimit, printerEvolution);
                        semiGreeedy.solution.exportSolution(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");

/*
                        greedyTestNum++;


                        printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
                        SuddenlyStops suddenlyStops = new SuddenlyStops(data, greedyTestNum, rate, timeLimit, printerEvolution);
                        suddenlyStops.solution.exportSolution(fileDirectory + "/" + filename + "--" + greedyTestNum + "-" + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
*/


                        printerEvolution.close();
                    }
                }
            }
        } catch (IOException exc1) {
            exc1.printStackTrace();
        }
    }
}
