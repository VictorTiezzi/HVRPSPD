package com.unifor;

import com.unifor.data.*;

import java.io.PrintStream;
import java.util.*;

public class Greedy2 {

    Solution solution;

    class Truck {
        Type type;
        double capacity;
        double fixedCost;
        double initialLoad;
        List<Node> nodes;
        Node end;
        boolean first;
        boolean feasibleNodes;
        Set<Node> infeasibleNodes;

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
            infeasibleNodes = new HashSet<>();
        }
    }

    public Greedy2(int NumOfTruks, Data data, double nearestNeighborProbability, int timeLimit, PrintStream printer) {

        double startTime = System.currentTimeMillis();
        Random randomGenerator = new Random();
        int iteration = 0;
        double currentTime = 0.0;

        while (currentTime <= timeLimit) {

            List<Route> routes = new ArrayList<>();
            List<Node> freeNodes = new ArrayList<>(Arrays.asList(data.nodes));
            freeNodes.remove(0);
            Collections.shuffle(freeNodes);

            double totalCost = 0;

            List<Truck> trucks = new ArrayList<>();
            for (int i = 0; i < NumOfTruks; i++) {
                Truck truck = new Truck(data);
                totalCost += truck.fixedCost;
                trucks.add(truck);
            }

            while (!freeNodes.isEmpty()) {
                for (Truck truck : trucks) {

                    if (!truck.feasibleNodes) {
                        continue;
                    }

                    Node trial = null;
                    if (!truck.first && randomGenerator.nextDouble() < nearestNeighborProbability) {
                        double minDistance = Double.MAX_VALUE;
                        for (Node next : freeNodes) {
                            if (!truck.infeasibleNodes.contains(next)) {
                                Link link = new Link(truck.end, next, false);
                                if (link.distance < minDistance) {
                                    minDistance = link.distance;
                                    trial = next;
                                }
                            }
                        }
                    } else {
                        for (Node next : freeNodes) {
                            if (!truck.infeasibleNodes.contains(next)) {
                                trial = next;
                                truck.first = false;
                                break;
                            }
                        }
                    }

                    if(trial == null){
                        truck.feasibleNodes = false;
                        continue;
                    }

                    double loadTrial = truck.initialLoad + trial.delivery;
                    if (loadTrial > truck.capacity) {
                        truck.infeasibleNodes.add(trial);
                    } else {
                        boolean feasibleRoute = true;
                        for (int m = 1; m < truck.nodes.size(); m++) {
                            Node next = truck.nodes.get(m);
                            loadTrial += next.pickup - next.delivery;
                            if (loadTrial > truck.capacity) {
                                truck.infeasibleNodes.add(trial);
                                feasibleRoute = false;
                                break;
                            }
                        }

                        if (feasibleRoute) {
                            loadTrial += trial.pickup - trial.delivery;
                            if (loadTrial > truck.capacity) {
                                truck.infeasibleNodes.add(trial);
                            } else {
                                Link link = new Link(truck.end, trial, false);
                                totalCost += link.distance * truck.type.variableCost;
                                freeNodes.remove(trial);
                                truck.nodes.add(trial);
                                truck.end = trial;
                                truck.initialLoad += trial.delivery;
                            }
                        }
                    }

                    truck.feasibleNodes = false;
                    for (Node node : freeNodes) {
                        if (!truck.infeasibleNodes.contains(node)) {
                            truck.feasibleNodes = true;
                            break;
                        }
                    }

                }

                boolean noneTrucks = true;
                for (Truck truck : trucks) {
                    if (truck.feasibleNodes) {
                        noneTrucks = false;
                        break;
                    }
                }
                if (noneTrucks) {
                    Truck truck = new Truck(data);
                    totalCost += truck.fixedCost;
                    trucks.add(truck);
                }
            }

            for (Truck truck : trucks) {
                Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
                totalCost += link.distance * truck.type.variableCost;
                routes.add(new Route(truck.type, truck.nodes));
            }

            iteration++;
            if ((solution == null) || totalCost < solution.totalCost - 0.01) {
                currentTime = (System.currentTimeMillis() - startTime) / 1000;
                solution = new Solution(routes, currentTime, iteration);
                printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
            }

            currentTime = (System.currentTimeMillis() - startTime) / 1000;
        }
        if (solution != null)
            printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
    }

}
