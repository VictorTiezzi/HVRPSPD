package com.unifor;

import com.unifor.data.*;

import java.io.PrintStream;
import java.util.*;

public class SuddenlyStops {

    Solution solution;

    public SuddenlyStops(Data data, int suddenlyStops, double nearestNeighborProbability, int timeLimit, PrintStream printer) {
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
            while (!freeNodes.isEmpty()) {
                Type type = data.types[randomGenerator.nextInt(data.nTypes)];
                double capacity = type.capacity;
                double fixedCost = type.fixedCost;
                double initialLoad = 0;
                totalCost += fixedCost;
                List<Node> nodes = new ArrayList<>();
                nodes.add(data.nodes[0]);
                Node end = data.nodes[0];
                boolean first = true;
                boolean feasibleNodes = true;

                int ss = 0;

                for (Node next : freeNodes) {
                    next.infeasible = false;
                }
                while (feasibleNodes) {
                    Node trial = null;
                    int trialIndex = -1;

                    if (!first && randomGenerator.nextDouble() < nearestNeighborProbability) {
                        double minDistance = Double.MAX_VALUE;
                        for (int n = 0; n < freeNodes.size(); n++) {
                            Node next = freeNodes.get(n);
                            if (!next.infeasible) {
                                Link link = new Link(end, next, false);
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
                                first = false;
                                break;
                            }
                        }
                    }

                    double loadTrial = initialLoad + trial.delivery;
                    if (loadTrial > capacity) {
                        trial.infeasible = true;
                    } else {
                        boolean feasibleRoute = true;
                        for (int m = 1; m < nodes.size(); m++) {
                            Node next = nodes.get(m);
                            loadTrial += next.pickup - next.delivery;
                            if (loadTrial > capacity) {
                                trial.infeasible = true;
                                feasibleRoute = false;
                                break;
                            }
                        }

                        if (feasibleRoute) {
                            loadTrial += trial.pickup - trial.delivery;
                            if (loadTrial > capacity) {
                                trial.infeasible = true;
                            } else {
                                Link link = new Link(end, trial, false);
                                totalCost += link.distance * type.variableCost;
                                nodes.add(freeNodes.remove(trialIndex));
                                end = trial;
                                initialLoad += trial.delivery;
                            }
                        }

                    }
                    //new code
                    if (trial.infeasible) {
                        ss++;
                        if (suddenlyStops <= ss) {
                            break;
                        }
                    }
                    //end new code

                    feasibleNodes = false;
                    for (Node next : freeNodes) {
                        if (!next.infeasible) {
                            feasibleNodes = true;
                            break;
                        }
                    }
                }
                Link link = new Link(nodes.get(nodes.size() - 1), nodes.get(0), false);
                totalCost += link.distance * type.variableCost;
                routes.add(new Route(type, nodes));
            }

            iteration++;
            if ((solution == null) || totalCost < solution.totalCost - 0.01) {
                currentTime = (System.currentTimeMillis() - startTime) / 1000;
                solution = new Solution(routes, currentTime, iteration);
                printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
            }

            currentTime = (System.currentTimeMillis() - startTime) / 1000;
        }
        if (!(solution == null))
            printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
    }
}
