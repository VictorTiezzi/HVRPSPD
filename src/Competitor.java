import data.*;

import java.io.PrintStream;
import java.util.*;

public class Competitor {

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

    static class Returned {
        public final double totalCost;
        public final List<Route> routes;

        public Returned(double totalCost, List<Route> routes) {
            this.totalCost = totalCost;
            this.routes = routes;
        }
    }

    public Competitor(Data data, double nearestNeighborProbability, int timeLimit, PrintStream printer) {
        startTime = System.currentTimeMillis();

        this.data = data;
        this.nearestNeighborProbability = nearestNeighborProbability;
        this.timeLimit = timeLimit;
        this.printer = printer;

        iteration = 0;
        currentTime = 0.0;

        map = new HashMap<>();

        int NumOfTruks = 1;
        boolean calibrationDone = false;

        int uperMaxNum = 4;
        int numOfTestsForCalibration = 50;

        while (currentTime <= timeLimit) {

            if(!calibrationDone){
                NumOfTruks = calibration(uperMaxNum, numOfTestsForCalibration);
                calibrationDone = true;
                if (currentTime >= timeLimit) {
                    break;
                }
            }

            Returned returned = competitorAlgorithm(NumOfTruks);

            iteration++;
            if ((solution == null) || returned.totalCost < solution.totalCost - 0.01) {
                currentTime = (System.currentTimeMillis() - startTime) / 1000;
                solution = new Solution(returned.routes, currentTime, iteration);
                printer.printf("%8.2f%6.1f%15d\n", returned.totalCost, currentTime, iteration);
            }
            currentTime = (System.currentTimeMillis() - startTime) / 1000;
        }

        if (solution != null)
            printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);

        printer.println("Quantidade de truks:" + map.size());
        printer.println("numero de truks usado:" +  NumOfTruks);

    }

    public int calibration(int uperMaxNum, int numOfTestForCalibration) {

        int NumOfTruks = 1;
        int uper = 0;
        boolean stop = false;

        while (!stop) {
            map.put(NumOfTruks, new ArrayList<>());

            for (int j = 0; j < numOfTestForCalibration; j++) {
                Returned returned = competitorAlgorithm(NumOfTruks);
                ArrayList<Double> list = map.get(NumOfTruks);
                list.add(returned.totalCost);
                map.put(NumOfTruks, list);

                iteration++;
                if ((solution == null) || returned.totalCost < solution.totalCost - 0.01) {
                    currentTime = (System.currentTimeMillis() - startTime) / 1000;
                    solution = new Solution(returned.routes, currentTime, iteration);
                    printer.printf("%8.2f%6.1f%15d\n", returned.totalCost, currentTime, iteration);
                }
                currentTime = (System.currentTimeMillis() - startTime) / 1000;

                if (currentTime >= timeLimit){
                    break;
                }
            }
            if (currentTime >= timeLimit){
                break;
            }

            if (NumOfTruks != 1) {
                if (average(map.get(NumOfTruks)) - average(map.get(NumOfTruks - 1)) >= 0) {
                    uper++;
                } else {
                    uper--;
                    if (uper < 0) {
                        uper = 0;
                    }
                }
            }

            if (uper >= uperMaxNum) {
                stop = true;
                double menor = Double.MAX_VALUE;
                int num = -1;

                for (int i = 1; i <= NumOfTruks; i++) {
                    double aux = Collections.min(map.get(i));
                    if (aux < menor) {
                        menor = aux;
                        num = i;
                    }
                }

                NumOfTruks = num;
            } else {
                NumOfTruks++;
            }

        }

        return NumOfTruks;
    }

    public Returned competitorAlgorithm(int NumOfTruks) {

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

            if (trucks.isEmpty()) {
                Truck truck = new Truck(data);
                totalCost += truck.fixedCost;
                trucks.add(truck);
            }

            for (Truck truck : trucks) {

                Node trial = null;
                int trialIndex = -1;
                if (!truck.first && new Random().nextDouble() < nearestNeighborProbability) {
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

                if (viability(trial, truck)) {
                    Link link = new Link(truck.end, trial, false);
                    totalCost += link.distance * truck.type.variableCost;
                    truck.nodes.add(freeNodes.remove(trialIndex));
                    truck.end = trial;
                    truck.initialLoad += trial.delivery;
                } else {
                    truck.infeasibleNodes.add(trial);
                }

            }
            trucks.removeIf(truck -> !truck.feasibleNodes);
        }

        for (Truck truck : trucks) {
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
