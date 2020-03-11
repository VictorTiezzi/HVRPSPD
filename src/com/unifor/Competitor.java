package com.unifor;

import com.unifor.data.*;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;

public class Competitor {

    Solution solution;

    static class Truck {
        Type type;
        double capacity;
        double fixedCost;
        double initialLoad;
        List<Node> nodes;
        Node end;
        boolean first;
        boolean feasibleNodes;
        Set<Node> infeasibleNodes;
        //int ss;

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
            //ss = 0;
        }
    }

    public Competitor(int NumOfSum, Data data, double nearestNeighborProbability, int timeLimit, PrintStream printer) {

        double startTime = System.currentTimeMillis();
        Random randomGenerator = new Random();
        int iteration = 0;
        double currentTime = 0.0;

        int NumOfTruks = 1;
        int NumOfTimes = 1;

        Map<Integer, ArrayList<Double>> map = new HashMap<>();
        for (int i = 1; i<30;i++){
            map.put(i, new ArrayList<>());
        }

        //int suddenlyStops = 6;

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
            NumOfTimes--;

            while (!freeNodes.isEmpty()) {

                if (trucks.isEmpty()) {
                    if (NumOfTimes > 0) {
                        for (int i = 0; i < NumOfTruks; i++) {
                            Truck truck = new Truck(data);
                            totalCost += truck.fixedCost;
                            trucks.add(truck);
                        }
                        NumOfTimes--;
                    } else {
                        Truck truck = new Truck(data);
                        totalCost += truck.fixedCost;
                        trucks.add(truck);
                    }
                }

                for (Truck truck : trucks) {

                    Node trial = null;
                    int trialIndex = -1;
                    if (!truck.first && randomGenerator.nextDouble() < nearestNeighborProbability) {
                        double minDistance = Double.MAX_VALUE;
                        for (int n = 0; n < freeNodes.size(); n++) {
                            Node next = freeNodes.get(n);
                            if (!truck.infeasibleNodes.contains(next)) {
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
                            if (!truck.infeasibleNodes.contains(next)) {
                                trial = next;
                                trialIndex = n;
                                truck.first = false;
                                break;
                            }
                        }
                    }

                    if (trial == null) {
                        truck.feasibleNodes = false;
                        Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
                        totalCost += link.distance * truck.type.variableCost;
                        routes.add(new Route(truck.type, truck.nodes));
                        continue;
                    }

                    if (viabilidade(trial, truck)) {
                        Link link = new Link(truck.end, trial, false);
                        totalCost += link.distance * truck.type.variableCost;
                        truck.nodes.add(freeNodes.remove(trialIndex));
                        truck.end = trial;
                        truck.initialLoad += trial.delivery;
                    } else {
                        truck.infeasibleNodes.add(trial);
                        //truck.ss++;
                    }
//suddenlyStops test
//                    if (suddenlyStops <= truck.ss) {
//                        truck.feasibleNodes = false;
//                        Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
//                        totalCost += link.distance * truck.type.variableCost;
//                        routes.add(new Route(truck.type, truck.nodes));
//                    }

//                    truck.feasibleNodes = false;
//                    for (Node node : freeNodes) {
//                        if (!truck.infeasibleNodes.contains(node)) {
//                            truck.feasibleNodes = true;
//                            break;
//                        }
//                    }
//
//                    if(!truck.feasibleNodes){
//                        Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
//                        totalCost += link.distance * truck.type.variableCost;
//                        routes.add(new Route(truck.type, truck.nodes));
//                    }
                }

                trucks.removeIf(truck -> !truck.feasibleNodes);
            }


            ArrayList<Double> listInMap = map.get(NumOfTruks);
            if (listInMap.size() < NumOfSum) {
                listInMap.add(totalCost);
                if (listInMap.size() - 1 == NumOfSum) Collections.sort(listInMap);
            } else {
                if (listInMap.get(NumOfSum - 1) > totalCost) {
                    listInMap.remove(NumOfSum - 1);
                    listInMap.add(totalCost);
                    Collections.sort(listInMap);
                }
            }
            map.replace(NumOfTruks, listInMap);



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
        System.out.println();
    }

    public Boolean viabilidade(Node trial, Truck truck) {
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

    public double media(int NumOfTruks, ArrayList<Double> listInMap){
        double sum = 0;
        for (double num: listInMap) sum += num;
        return sum / NumOfTruks;
    }

}
