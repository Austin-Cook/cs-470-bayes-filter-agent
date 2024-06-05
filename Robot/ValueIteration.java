public class ValueIteration {
    private final double CONVERGENCE_FACTOR = 0.001;
    private final double GAMMA = 0.98;
    private final double GOAL_REWARD = 100;
    private final double STAIRWLL_REWARD = -100;
    private final double DEFAULT_REWARD = -1;
    
    private final World world;
    private final int width;
    private final int height;
    private final Util util;
    private double[][] oldVs;
    private double[][] newVs;
    private double iter;

    public ValueIteration(final World world, final double moveProb) {
        this.world = world;
        this.util = new Util(world, moveProb);
        width = world.grid.length;
        height = world.grid[0].length;
    }

    public double[][] compute() {
        newVs = initVs();

        System.out.println("Initial values: ");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String numFormatted = String.format("%.2f", newVs[x][y]);
                System.out.print(numFormatted + ",");
            }
            System.out.println("\n");
        }
        
        iter = 0;
        double maxChange = Double.MAX_VALUE;
        while (maxChange > CONVERGENCE_FACTOR) {
            iter++;
            oldVs = copy(newVs);
            maxChange = 0.0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bellman(x, y);
                    maxChange = Math.max(maxChange, Math.abs(oldVs[x][y] - newVs[x][y]));
                }
            }
            if (iter < 35) { // DELETEME
                System.out.println("After " + iter + " iters: ");
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        String numFormatted = String.format("%.2f", newVs[x][y]);
                        System.out.print(numFormatted + ",");
                    }
                    System.out.println();
                }
                System.out.println("maxChange: " + maxChange + "\n");
            }// END DELETEME
        }

        // System.out.println("Value iteration");
        // for (int y = 0; y < height; y++) {
        //     for (int x = 0; x < width; x++) {
        //         String numFormatted = String.format("%.2f", newVs[x][y]);
        //         System.out.print(numFormatted + ",");
        //     }
        //     System.out.println("\n");
        // }
        // System.out.println("Iter: " + iter);


        return newVs;
    }

    /**
     * Updates newVs[x][y] with the cooresponding value from the Bellman equation
     */
    private void bellman(final int fromX, final int fromY) {
        if (world.grid[fromX][fromY] != World.EMPTY)
            // we will only ever move from an empty space (otherwise game ended)
            return;

        // find the value of added by the most lucrative action
        double maxActionVal = 0.0;
        for (int action = 0; action < Util.X_OFFSETS.length; action++) {
            // sum potential value added from all possible states (taking the action)
            double actionVal = 0.0;
            for (int iOffset = 0; iOffset < Util.X_OFFSETS.length; iOffset++) {
                int toX = fromX + Util.X_OFFSETS[iOffset];
                int toY = fromY + Util.Y_OFFSETS[iOffset];
                double probToGivenFromAndAction = util.probTrans_givenFromAndAction(fromX, fromY, toX, toY, action);
                actionVal += probToGivenFromAndAction * oldVs[toX][toY];
            }

            // keep resulting value added from best action
            maxActionVal = Math.max(maxActionVal, actionVal);
        }
        
        // Bellman Equation:
        // R(s) + γ^iter * max_a∈A(s)∑_s′(P(s'|s,a)*U*(S'))
        // - R(S): the value previously stored in newVs
        // - γ: the gamma value
        // - max_a∈A(s): the best value considering all possible actions
        // - ∑_s′(P(s'|s,a)*U*(S')): for each possible child state given the state and action, 
        //                           the probability of the child state times the understood 
        //                           utility of the child state
        newVs[fromX][fromY] += Math.pow(GAMMA, iter) * maxActionVal;
    }

    /**
     * Goal: great reward
     * Stairwells: great loss
     * Other: slight loss
     */
    private double[][] initVs() {
        double[][] vs = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                vs[x][y] = world.grid[x][y] == World.GOAL ? GOAL_REWARD
                        : world.grid[x][y] == World.STAIRWELL ? STAIRWLL_REWARD
                        : DEFAULT_REWARD;
            }
        }

        return vs;
    }

    private double[][] copy(double[][] arr) {
        double[][] copy = new double[arr.length][arr[0].length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                copy[x][y] = arr[x][y];
            }
        }
        
        return copy;
    }
}
