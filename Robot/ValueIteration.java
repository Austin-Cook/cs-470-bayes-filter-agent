public class ValueIteration {
    private final double CONVERGENCE_FACTOR = 0.001;
    private final double GAMMA = 0.99;
    private final double GOAL_REWARD = 100;
    private final double STAIRWLL_REWARD = -100;
    private final double DEFAULT_REWARD = -1;
    
    private final World world;
    private final double moveProb;
    private final double sensorAccuracy;
    private final int width;
    private final int height;
    private double[][] oldVs;
    private double[][] newVs;

    public ValueIteration(final World world, final double moveProb, final double sensorAccuracy) {
        this.world = world;
        this.moveProb = moveProb;
        this.sensorAccuracy = sensorAccuracy;

        width = world.grid.length;
        height = world.grid[0].length;
    }

    public double[][] run() {
        newVs = initVs();
        
        double maxChange = Double.MAX_VALUE;
        while (maxChange > CONVERGENCE_FACTOR) {
            maxChange = 0.0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    computeVs(x, y);
                }
            }
        }

        return newVs;
    }

    private void computeVs(final int x, final int y) {
        if (world.grid[x][y] == World.WALL)
            return;

        oldVs = newVs.clone();
        
    }

    private double[][] initVs() {
        double[][] vs = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newVs[x][y] = world.grid[x][y] == World.GOAL ? GOAL_REWARD
                        : world.grid[x][y] == World.STAIRWELL ? STAIRWLL_REWARD
                        : DEFAULT_REWARD;
            }
        }

        return vs;
    }

    private double[][] copyArray(double[][] arr) {
        double[][] copy = new double[arr.length][arr[0].length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // TODO
            }
        }
        
        return copy;
    }
}
