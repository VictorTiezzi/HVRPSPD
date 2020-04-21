package com.unifor;

import com.unifor.data.*;

import java.io.PrintStream;
import java.util.*;

public class SuddenlyStops {

    Solution solution;

    double startTime;

    Data data;
    double nearestNeighborProbability;
    int timeLimit;
    PrintStream printer;
    int iteration;
    double currentTime;

    Map<Integer, ArrayList<Double>> map;

    static class Truck {
        Type type;
        double capacity;
        double fixedCost;
        double initialLoad;
        List<Node> nodes;
        Node end;
        boolean first;
        boolean feasibleNodes;

        public Truck(Data data) {
            this.type = data.types[new Random().nextInt(data.nTypes)];
            capacity = type.capacity;
            fixedCost = type.fixedCost;
            initialLoad = 0;
            nodes = new ArrayList<>();
            nodes.add(data.nodes[0]);
            end = data.nodes[0];
            first = true;
            feasibleNodes = true;
        }
    }

    static class Returned {
        public final double totalCost;
        public final List<Route> routes;

        public Returned(double totalCost, List<Route> routes) {
            this.totalCost = totalCost;
            this.routes = routes;
        }
    }

    public SuddenlyStops(Data data, double nearestNeighborProbability, int timeLimit, PrintStream printer) {
        startTime = System.currentTimeMillis();

        this.data = data;
        this.nearestNeighborProbability = nearestNeighborProbability;
        this.timeLimit = timeLimit;
        this.printer = printer;

        iteration = 0;
        currentTime = 0.0;

        map = new HashMap<>();

        int suddenlyStops = 1;
        boolean calibrationDone = false;

        int uperMaxNum = 6;
        int numOfTestsForCalibration = 1000;

        while (currentTime <= timeLimit) {

            if(!calibrationDone){
                suddenlyStops = calibration(uperMaxNum, numOfTestsForCalibration);
                calibrationDone = true;
                if (currentTime >= timeLimit) {
                    break;
                }
            }

            Returned returned = suddenlyStopsAlgorithm(suddenlyStops);

            iteration++;
            if ((solution == null) || returned.totalCost < solution.totalCost - 0.01) {
                currentTime = (System.currentTimeMillis() - startTime) / 1000;
                solution = new Solution(returned.routes, currentTime, iteration);
                printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
            }
            currentTime = (System.currentTimeMillis() - startTime) / 1000;
        }

        if (!(solution == null))
            printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);

        printer.println("Numero de suddenlyStops: " + map.size());
        printer.println("numero do suddenlyStops usado: " + suddenlyStops);

    }

    public int calibration(int uperMaxNum, int numOfTestForCalibration) {

        int suddenlyStops = 1;
        int uper = 0;
        boolean stopCalibration = false;

        while (!stopCalibration) {
            map.put(suddenlyStops, new ArrayList<>());

            for (int j = 0; j < numOfTestForCalibration; j++) {
                Returned returned = suddenlyStopsAlgorithm(suddenlyStops);
                ArrayList<Double> list = map.get(suddenlyStops);
                list.add(returned.totalCost);
                map.put(suddenlyStops, list);

                iteration++;
                if ((solution == null) || returned.totalCost < solution.totalCost - 0.01) {
                    currentTime = (System.currentTimeMillis() - startTime) / 1000;
                    solution = new Solution(returned.routes, currentTime, iteration);
                    printer.printf("%8.2f%6.1f%15d\n", returned.totalCost, currentTime, iteration);
                }
                currentTime = (System.currentTimeMillis() - startTime) / 1000;

                if (currentTime >= timeLimit) {
                    break;
                }
            }
            if (currentTime >= timeLimit) {
                break;
            }

            if (suddenlyStops != 1) {
                if (average(map.get(suddenlyStops)) - average(map.get(suddenlyStops - 1)) >= 0) {
                    uper++;
                } else {
                    uper--;
                    if (uper < 0) {
                        uper = 0;
                    }
                }
            }

            if (uper >= uperMaxNum) {
                stopCalibration = true;
                double menor = Double.MAX_VALUE;
                int num = -1;

                for (int i = 1; i <= suddenlyStops; i++) {
                    double aux = Collections.min(map.get(i));
                    if (aux < menor) {
                        menor = aux;
                        num = i;
                    }
                }

                suddenlyStops = num;
            } else {
                suddenlyStops++;
            }

        }

        return suddenlyStops;
    }

    public Returned suddenlyStopsAlgorithm(int suddenlyStops) {

        List<Route> routes = new ArrayList<>();
        List<Node> freeNodes = new ArrayList<>(Arrays.asList(data.nodes));
        freeNodes.remove(0);
        Collections.shuffle(freeNodes);

        double totalCost = 0;
        while (!freeNodes.isEmpty()) {

            Truck truck = new Truck(data);
            totalCost += truck.fixedCost;

            int ss = 0;

            for (Node next : freeNodes) {
                next.infeasible = false;
            }

            while (truck.feasibleNodes) {
                Node trial = null;
                int trialIndex = -1;

                if (!truck.first && new Random().nextDouble() < nearestNeighborProbability) {
                    double minDistance = Double.MAX_VALUE;
                    for (int n = 0; n < freeNodes.size(); n++) {
                        Node next = freeNodes.get(n);
                        if (!next.infeasible) {
                            Link link = new Link(truck.end, next, false);
                            if (link.distance < minDistance) {
                                minDistance = link.distance;
                                trial = next;
                                trialIndex = n;
                            }
                        }
                    }
                } else {
                    for (int n = 0; n < freeNodes.size(); n++) {
                        Node next = freeNodes.get(n);
                        if (!next.infeasible) {
                            trial = next;
                            trialIndex = n;
                            truck.first = false;
                            break;
                        }
                    }
                }

                if (viability(trial, truck)) {
                    Link link = new Link(truck.end, trial, false);
                    totalCost += link.distance * truck.type.variableCost;
                    truck.nodes.add(freeNodes.remove(trialIndex));
                    truck.end = trial;
                    truck.initialLoad += trial.delivery;
                } else {
                    trial.infeasible = true;
                }

                //new code
                if (trial.infeasible) {
                    ss++;
                    if (suddenlyStops <= ss) {
                        break;
                    }
                }
                //end new code

                truck.feasibleNodes = false;
                for (Node next : freeNodes) {
                    if (!next.infeasible) {
                        truck.feasibleNodes = true;
                        break;
                    }
                }
            }
            Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
            totalCost += link.distance * truck.type.variableCost;
            routes.add(new Route(truck.type, truck.nodes));
        }
        return new Returned(totalCost, routes);
    }

    public Boolean viability(Node trial, Truck truck) {
        double loadTrial = truck.initialLoad + trial.delivery;
        if (loadTrial > truck.capacity) {
            return false;
        } else {
            for (int m = 1; m < truck.nodes.size(); m++) {
                Node next = truck.nodes.get(m);
                loadTrial += next.pickup - next.delivery;
                if (loadTrial > truck.capacity) {
                    return false;
                }
            }
            loadTrial += trial.pickup - trial.delivery;
            //cliente viavel se true
            return !(loadTrial > truck.capacity);
        }
    }

    public double average(ArrayList<Double> listInMap) {
        double sum = 0;
        for (double num : listInMap) sum += num;
        return sum / listInMap.size();
    }

}
