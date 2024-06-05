public class ValueIteration {
    private final double CONVERGENCE_FACTOR = 0.01;
    private final double GAMMA = 0.99;
    private final double GOAL_REWARD = 100;
    private final double STAIRWLL_REWARD = 0.0;
    private final double DEFAULT_REWARD = 0;
    
    private final World world;
    private final int width;
    private final int height;
    private final Util util;
    private double[][] oldVs;
    private double[][] newVs;
    private double[][] rewards;
    private double iter;

    public ValueIteration(final World world, final double moveProb) {
        this.world = world;
        this.util = new Util(world, moveProb);
        width = world.grid.length;
        height = world.grid[0].length;
    }

    public double[][] compute() {
        rewards = initRewards();
        newVs = initRewards();
        
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
        }

        return newVs;
    }

    /**
     * Updates newVs[x][y] with the cooresponding value from the Bellman equation
     * 
     * Bellman Equation:
     * R(s) + γ^iter * max_a∈A(s)∑_s′(P(s'|s,a)*U*(S'))
     * - R(S): the value previously stored in newVs
     * - γ: the gamma value
     * - max_a∈A(s): the best value considering all possible actions
     * - ∑_s′(P(s'|s,a)*U*(S')): for each possible child state given the state and action, 
     *                           the probability of the child state times the understood 
     *                           utility of the child state
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
        
        newVs[fromX][fromY] = rewards[fromX][fromY] + Math.pow(GAMMA, iter) * maxActionVal;
    }

    private double[][] initRewards() {
        double[][] arr = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                arr[x][y] = world.grid[x][y] == World.GOAL ? GOAL_REWARD
                        : world.grid[x][y] == World.STAIRWELL ? STAIRWLL_REWARD
                        : DEFAULT_REWARD;
            }
        }

        return arr;
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
