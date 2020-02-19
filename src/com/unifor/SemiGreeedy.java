package com.unifor;

import com.unifor.data.*;

import java.io.PrintStream;
import java.util.*;

public class SemiGreeedy {

    Solution solution;

    public SemiGreeedy(Data data, int timeLimit, PrintStream printer) {
        double startTime = System.currentTimeMillis();
        Random randomGenerator = new Random();
        int iteration = 0;
        double currentTime = 0.0;
        while (iteration <= 10000) {

            List<Route> routes = new ArrayList<>();
            List<Node> freeNodes = new ArrayList<>(Arrays.asList(data.nodes));
            freeNodes.remove(0);
            Collections.shuffle(freeNodes);

            double totalCost = 0;
            while (!freeNodes.isEmpty()) {// teste de parada da iteração

                // dados do caminhão
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

                //int suddenlyStops = 3;
                int numberOfClients = 3;
                //fim dados do caminhão

                for (Node next : freeNodes) {
                    next.infeasible = false;
                }

                // rota do caminhão
                while (feasibleNodes) {
                    //int ss = 1;
                    Node trial = null;
                    //int trialIndex = -1;

                    // escolhendo um cliente
                    if (!first) {
                        List<Node> list = new ArrayList<>();

                        for (Node next : freeNodes) {
                            if (!next.infeasible) {
                                Link link = new Link(end, next, false);
                                next.distanceNode = link.distance;
                                list.add(next);
                            }
                        }

                        //Arrays.sort(new List[]{list});
                        Collections.sort(list);

                        List<Node> escolhidos = new ArrayList<>();
                        for(int i = 0; i < numberOfClients; i++){
                            if(i+1 <= list.size()){
                                escolhidos.add(list.get(i));
                            }
                        }

                        trial = escolhidos.get(randomGenerator.nextInt(escolhidos.size()));

                    } else {
                        for (int n = 0; n < freeNodes.size(); n++) {
                            Node next = freeNodes.get(n);
                            if (!next.infeasible) {
                                trial = next;
                                first = false;
                                break;
                            }
                        }

                    }
                    // fim escolhendo um cliente

                    // teste de viabilidade
                    assert trial != null;
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
                                // cliente viavel
                                Link link = new Link(end, trial, false);
                                totalCost += link.distance * type.variableCost;
                                freeNodes.remove(trial);
                                nodes.add(trial);
                                end = trial;
                                initialLoad += trial.delivery;
                            }
                        }

                    }
                    //fim teste de viabilidade

//                    if (trial.infeasible) {
//                        ss++;
//                        if (suddenlyStops > ss) {
//                            break;
//                        }
//                    }

                    // teste de parada
                    feasibleNodes = false;
                    for (Node next : freeNodes) {
                        if (!next.infeasible) {
                            feasibleNodes = true;
                            break;
                        }
                    }
                    //fim teste de parada

                }
                Link link = new Link(nodes.get(nodes.size() - 1), nodes.get(0), false);
                totalCost += link.distance * type.variableCost;
                routes.add(new Route(type, nodes));
                // fim rota do caminhão
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
