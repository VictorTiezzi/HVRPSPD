# Fast Heuristics for Heterogeneous Vehicle Routing Problem with Simultaneous Pickup and Delivery

This is the source code and the final work for the Computer Science course at the University of Fortaleza at 2020 in Fortaleza, Ceará, Brazil.

To run this code, execute "Solver.java" with 4 heuristics.

My paper is HVRPSPD_Victor_Tiezzi_Henriques.pdf file, it's in portuguese language.

## ABSTRACT

The Heterogeneous Vehicle Routing Problem with Simultaneous Pickup and Delivery (HVRPSPD),
aims to generate a route for a heterogeneous vehicle fleet minimizing costs and meeting all
customer demand. This problem is commonly applied to companies that want to reduce their
logistics costs. The aim of this work is to create greedy heuristics that generate viable solutions with low costs for HVRPSPD, for this reason, 3 heuristics were developed based on the
Nearest-Neighbor-Based Randomized Algorithm (NNRA) with competitive, Sudden Stop and
Semi-Greedy strategies. The performance of the developed heuristics are compared to the NNRA
its evaluated performance. The results found showed that the heuristic of the sudden stop stood
out for presenting significant improvement in the quality of the solutions.

Keywords: Greedy Algorithm. Vehicle Routing Problem. Nearest-Neighbor-Based Randomized
Algorithm. Sudden Stop. Semi-Greedy. Competitive

## Solution file details

Detailed results for each execution are prensented in 2 different files. The
file "instance.sol" presents, for a given execution, the total cost of the best
solution found, the solving time and the iteration at which this solution was
reached, along with the routes of this solution, e.g.:

Total cost:             620.23  
Solving time:             0.88  
Iteration:              327872  

Vehicle type:  2  
Route:             0    5    4    6    2    9    0  

Vehicle type:  1  
Route:             0    1    7   10    0  

Vehicle type:  2  
Route:             0    8    3    0  

The file "instance.evo" presents, for a given execution, the evolution of the
best solution found. Whenever a new best solution is found, its cost is
presented along with the solving time and the iteration of the execution,
e.g.:

  802.98   0.0              1  
  787.39   0.0              2  
  753.28   0.0              4  
  734.51   0.0              5  
  727.18   0.0              6  
  685.40   0.0             14  
  684.15   0.0             24  
  682.07   0.0             74  
  672.66   0.0            155  
  668.29   0.0            250  
  664.96   0.0            253  
  641.55   0.0            387  
  636.28   0.1           5930  
  634.35   0.3          58134  
  628.00   0.5          99743  
  620.23   0.9         327872  
  620.23  60.0       35743147  
