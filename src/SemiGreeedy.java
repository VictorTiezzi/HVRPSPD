import data.*;

import java.io.PrintStream;
import java.util.*;

public class SemiGreeedy {

    Solution solution;

    Data data;

    static class Truck {
        Type type;
        double capacity;
        double fixedCost;
        double variableCost;
        double initialLoad;
        List<Node> nodes;
        Node end;
        boolean first;
        boolean feasibleNodes;

        public Truck(Data data) {
            this.type = data.types[new Random().nextInt(data.nTypes)];
            capacity = type.capacity;
            fixedCost = type.fixedCost;
            variableCost = type.variableCost;
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

    public SemiGreeedy(Data data, int timeLimit, PrintStream printer) {
        double startTime = System.currentTimeMillis();

        this.data = data;

        int iteration = 0;
        double currentTime = 0.0;

        int numberOfClients = 3;

        while (currentTime <= timeLimit) {
            
            Returned returned = semiGreeedyAlgorithim(numberOfClients);

            iteration++;
            if ((solution == null) || returned.totalCost < solution.totalCost - 0.01) {
                currentTime = (System.currentTimeMillis() - startTime) / 1000;
                solution = new Solution(returned.routes, currentTime, iteration);
                printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
            }

            currentTime = (System.currentTimeMillis() - startTime) / 1000;
        }
        if (solution != null)
            printer.printf("%8.2f%6.1f%15d\n", solution.totalCost, currentTime, iteration);
    }

    public Returned semiGreeedyAlgorithim(int numberOfClients){

        List<Route> routes = new ArrayList<>();
        List<Node> freeNodes = new ArrayList<>(Arrays.asList(data.nodes));

        freeNodes.remove(0);
        Collections.shuffle(freeNodes);

        double totalCost = 0;

        while (!freeNodes.isEmpty()) {
            // teste de parada da iteração

            // dados do caminhão
            Truck truck = new Truck(data);
            totalCost += truck.fixedCost;
            //fim dados do caminhão

            for (Node next : freeNodes) {
                next.infeasible = false;
            }

            // rota do caminhão
            while (truck.feasibleNodes) {

                // escolhendo um cliente
                Node trial = null;
                List<Node> escolhidos = new ArrayList<>();

                if (!truck.first) {
                    List<Node> list = new ArrayList<>();

                    for (Node next : freeNodes) {
                        if (!next.infeasible) {
                            Link link = new Link(truck.end, next, false);
                            next.distanceNode = link.distance;
                            list.add(next);
                        }
                    }
                    Collections.sort(list);
                    for (int i = 0; i < numberOfClients; i++) {
                        if (i < list.size()) {
                            escolhidos.add(list.get(i));
                        }
                    }

                } else {
                    for (Node next : freeNodes) {
                        if (!next.infeasible) {
                            trial = next;
                            truck.first = false;
                            break;
                        }
                    }

                }
                // fim escolhendo um cliente

                // teste de viabilidade
                if (!escolhidos.isEmpty()) {

                    //escolha em ordem apartir de uma escolha aleatoria primaria entre os selecionados
                    int chosenNumber = new Random().nextInt(escolhidos.size());
                    int index = chosenNumber;
                    do {
                        trial = escolhidos.get(index);
                        assert trial != null;
                        if (viabilidade(trial, truck)) {
                            Link link = new Link(truck.end, trial, false);
                            totalCost += link.distance * truck.variableCost;
                            freeNodes.remove(trial);
                            truck.nodes.add(trial);
                            truck.end = trial;
                            truck.initialLoad += trial.delivery;
                            break;
                        } else {
                            trial.infeasible = true;
                            index--;
                            if (index < 0) {
                                index = escolhidos.size() - 1;
                            }
                        }
                    } while (chosenNumber != index);
                } else {
                    assert trial != null;
                    if (viabilidade(trial, truck)) {
                        Link link = new Link(truck.end, trial, false);
                        totalCost += link.distance * truck.variableCost;
                        freeNodes.remove(trial);
                        truck.nodes.add(trial);
                        truck.end = trial;
                        truck.initialLoad += trial.delivery;
                    } else {
                        trial.infeasible = true;
                    }
                }
                //fim teste de viabilidade

                // teste de parada
                truck.feasibleNodes = false;
                for (Node next : freeNodes) {
                    if (!next.infeasible) {
                        truck.feasibleNodes = true;
                        break;
                    }
                }
                //fim teste de parada

            }
            Link link = new Link(truck.nodes.get(truck.nodes.size() - 1), truck.nodes.get(0), false);
            totalCost += link.distance * truck.variableCost;
            routes.add(new Route(truck.type, truck.nodes));
            // fim rota do caminhão
        }

        return new Returned(totalCost, routes);
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

}