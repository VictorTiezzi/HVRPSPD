import data.*;

import java.io.*;
import java.util.Locale;

/**
 * Article: A Fast Randomized Algorithm for the Heterogeneous Vehicle Routing
 * Problem with Simultaneous Pickup and Delivery
 * Available online at https://doi.org/10.3390/a12080158
 * 
 * @author Nepomuceno
 */
public class Solver {

    public static void main(String[] args) {

        Locale.setDefault(Locale.US);

        String[] filenames = { "instance-550" };
        /*
         * String[] filenames = {
         * "instance101", "instance102", "instance103", "instance104", "instance105",
         * "instance106", "instance107",
         * "instance108", "instance109", "instance110", "instance111", "instance112",
         * "instance113", "instance114",
         * "instance201", "instance202", "instance203", "instance204", "instance205",
         * "instance206", "instance207",
         * "instance208", "instance209", "instance210", "instance211", "instance212",
         * "instance213", "instance214"};
         */
        int numberOfExecutions = 1;
        double[] rates = { 0.99 };
        int timeLimit = 60;

        try {
            for (String filename : filenames) {
                Data data = new Data(filename);
                String fileDirectory = "./solution/" + filename;
                new File(fileDirectory).mkdirs();
                for (int exec = 1; exec <= numberOfExecutions; exec++) {
                    for (double rate : rates) {

                        competitor(fileDirectory, filename, exec, data, rate, timeLimit);

                        NNRA(fileDirectory, filename, exec, data, rate, timeLimit);

                        SemiGreeedy(fileDirectory, filename, exec, data, rate, timeLimit);

                        SuddenlyStops(fileDirectory, filename, exec, data, rate, timeLimit);

                    }
                }
            }
        } catch (IOException exc1) {
            exc1.printStackTrace();
        }
    }

    public static void competitor(String fileDirectory, String filename, int exec, Data data, Double rate,
            int timeLimit) throws IOException {
        PrintStream printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--Competitor-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
        Competitor competitor = new Competitor(data, rate, timeLimit, printerEvolution);
        competitor.solution.exportSolution(fileDirectory + "/" + filename + "--Competitor-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
        printerEvolution.close();
    }

    public static void NNRA(String fileDirectory, String filename, int exec, Data data, Double rate,
            int timeLimit) throws IOException {
        PrintStream printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--NNRA-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");

        NNRA nnra = new NNRA(data, rate, timeLimit, printerEvolution);
        nnra.solution.exportSolution(fileDirectory + "/" + filename + "--NNRA-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
        printerEvolution.close();
    }

    public static void SemiGreeedy(String fileDirectory, String filename, int exec, Data data, Double rate,
            int timeLimit) throws IOException {
        PrintStream printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--SemiGreeedy-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
        SemiGreeedy semiGreeedy = new SemiGreeedy(data, timeLimit, printerEvolution);
        semiGreeedy.solution.exportSolution(fileDirectory + "/" + filename + "--SemiGreeedy-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
        printerEvolution.close();
    }

    public static void SuddenlyStops(String fileDirectory, String filename, int exec, Data data, Double rate,
            int timeLimit) throws IOException {
        PrintStream printerEvolution = new PrintStream(fileDirectory + "/" + filename + "--SuddenlyStops-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".evo");
        SuddenlyStops suddenlyStops = new SuddenlyStops(data, rate, timeLimit, printerEvolution);
        suddenlyStops.solution.exportSolution(fileDirectory + "/" + filename + "--SuddenlyStops-"
                + String.format("-rate-%4.2f", rate) + String.format("-exec-%02d", exec) + ".sol");
        printerEvolution.close();
    }

}
