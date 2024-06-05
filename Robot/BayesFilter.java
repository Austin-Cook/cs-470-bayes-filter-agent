/**
 * Create an instance of this class each time you recompute probs
 * NOTES: 
 * - Probs are stored as (col, row) AKA (x, y)
 * - x moves right, y moves down
 */
public class BayesFilter {
    private final double[][] oldProbs;
    private double[][] newProbs;
    private final double sensorAccuracy;
    private final int action;
    private final String sonars;
    private final int width;
    private final int height;
    private final Util util;
    
    public BayesFilter(final World world, final double[][] oldProbs, final double moveProb, final double sensorAccuracy, final int action, final String sonars) {
        this.oldProbs = oldProbs;
        this.sensorAccuracy = sensorAccuracy;
        this.action = action;
        this.sonars = sonars;
        this.util = new Util(world, moveProb);
        width = oldProbs.length;
        height = oldProbs[0].length;
    }
    
    public double[][] compute() {
        newProbs = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newProbs[x][y] = transitionModel(x, y);
                newProbs[x][y] = sensorModel(x, y);
            }
        }

        Util.normalize(newProbs);
        return newProbs;
    }

    /**
     * Runs the transition model for transitioning INTO a given square
     */
    private double transitionModel(final int toX, final int toY) {
        double prob = 0.0;
        for (int i = 0; i < Util.X_OFFSETS.length; i++) { // visit up, down, right, left, stay
            int fromX = toX + Util.X_OFFSETS[i];
            int fromY = toY + Util.Y_OFFSETS[i];

            if (util.isWall(fromX, fromY)) {
                continue;
            }

            double probFromAdj = util.probTrans_givenFromAndAction(fromX, fromY, toX, toY, action);

            // multiply result of transition model by Bel(x_(t-1)) from the curr adj square
            probFromAdj *= oldProbs[fromX][fromY];
            prob += probFromAdj;
        }

        return prob;
    }

    private double sensorModel(final int toX, final int toY) {
        double numMatching = 0;
        for (int i = 0; i < 4; i++) {
            int newX = toX + Util.X_OFFSETS[i];
            int newY = toY + Util.Y_OFFSETS[i];
            boolean isWallSensed = sonars.charAt(i) == '1';
            boolean isWallSeenInMap = util.isWall(newX, newY);
            if (isWallSensed == isWallSeenInMap) {
                numMatching += 1;
            }
        }

        // NOTE - this formula isn't perfect
        // I came up with a table and derived equations to closely match the table
        double evidenceProb;
        if (sensorAccuracy >= 0.9 && numMatching < 4) {
            evidenceProb =  (1 - sensorAccuracy) * (1.25 + (2 * numMatching));
        } else {
            evidenceProb = (1 - sensorAccuracy) + numMatching * (sensorAccuracy / 4);
        }
        return evidenceProb * newProbs[toX][toY];
    }
}
