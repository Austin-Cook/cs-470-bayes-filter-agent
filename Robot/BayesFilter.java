/**
 * Create an instance of this class each time you recompute probs
 * NOTES: 
 * - Probs are stored as (col, row) AKA (x, y)
 * - x moves right, y moves down
 */
public class BayesFilter {
    private final World world;
    private final double[][] oldProbs;
    private double[][] newProbs;
    private final double moveProb;
    private final double sensorAccuracy;
    private final int action;
    private final String sonars;
    private final int width;
    private final int height;
    
    public BayesFilter(final World world, final double[][] oldProbs, final double moveProb, final double sensorAccuracy, final int action, final String sonars) {
        this.world = world;
        this.oldProbs = oldProbs;
        this.moveProb = moveProb;
        this.sensorAccuracy = sensorAccuracy;
        this.action = action;
        this.sonars = sonars;

        width = oldProbs.length;
        height = oldProbs[0].length;
    }
    
    public double[][] run() {
        newProbs = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // System.out.println("col: " + col + ", row: " + row + ": " + oldProbs[col][row]);
                newProbs[x][y] = transitionModel(x, y);
                newProbs[x][y] = sensorModel(x, y);
            }
        }

        normalize(newProbs);
        return newProbs;
    }

    /**
     * Runs the transition model for transitioning INTO a given square
     */
    private double transitionModel(final int toX, final int toY) {
        double prob = 0.0;
        for (int i = 0; i < Util.xOffsets.length; i++) { // visit up, down, right, left, stay
            int fromX = toX + Util.xOffsets[i];
            int fromY = toY + Util.yOffsets[i];
            double probFromAdj = 0;

            if (isWall(fromX, fromY) || isStair(fromX, fromY) || isWall(toX, toY)) {
                // from wall/stair OR to wall
                continue;
            }

            if (actionLeadsFromSquareToToSquare(fromX, fromY, toX, toY)) {
                // action leads to the space being evaluated
                if (action == theRobot.STAY) {
                    // staying: P(moves correctly) + P(moves incorrectly and bumps off wall back to it)
                    probFromAdj = moveProb + (numAdjacentWalls(fromX, fromY) * ((1 - moveProb) / 4));
                } else {
                    // moving: P(moves corractly) + P(accidently moves into it)
                    probFromAdj = moveProb + ((1 - moveProb) / 4);
                }
            } else {
                // action doesn't lead the space being evaluated
                if (action != theRobot.STAY && isSameSquare(fromX, fromY, toX, toY)) {
                    // going but need to stay: P(moves incorrectly and bumps off wall back to it) + (if moves into wall, it will stay)
                    probFromAdj = numAdjacentWalls(fromX, fromY) * ((1 - moveProb) / 4);
                    if (isDestAWall(fromX, fromY)) {
                        probFromAdj += moveProb;
                    }
                } else {
                    // going to wrong space or staying but need to go: P(accidently moves into it)
                    probFromAdj = ((1 - moveProb) / 4);
                }
            }

            // multiply result of transition model by Bel(x_(t-1)) from the curr adj square
            probFromAdj *= oldProbs[fromX][fromY];
            prob += probFromAdj;
        }

        return prob;
    }

    private double sensorModel(final int toX, final int toY) {
        double numMatching = 0;
        for (int i = 0; i < 4; i++) {
            int newX = toX + Util.xOffsets[i];
            int newY = toY + Util.yOffsets[i];
            boolean isWallSensed = sonars.charAt(i) == '1';
            boolean isWallSeenInMap = isWall(newX, newY);
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

    private static void normalize(double[][] arr) {
        double total = 0.0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                total += arr[i][j];
            }
        }
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                arr[i][j] /= total;
            }
        }
    }

    private boolean isOutOfBounds(int x, int y) {
        return (x < 0 || y < 0 || x >= world.width || y >= world.height);
    }

    private boolean isWall(int x, int y) {
        // NOTE: We treat an out of bounds space as a wall
        return isOutOfBounds(x, y) || world.grid[x][y] == World.WALL;
    }

    private boolean isStair(int x, int y) {
        // NOTE: We treat an out of bounds space as a stair
        return isOutOfBounds(x, y) || world.grid[x][y] == World.STAIRWELL;
    }

    private boolean isDestAWall(int fromX, int fromY) {
        int reachedX = fromX + Util.xOffsets[action];
        int reachedY = fromY + Util.yOffsets[action];

        return isWall(reachedX, reachedY);
    }

    private boolean actionLeadsFromSquareToToSquare(int fromX, int fromY, int toX, int toY) {
        int reachedX = fromX + Util.xOffsets[action];
        int reachedY = fromY + Util.yOffsets[action];

        return isSameSquare(reachedX, reachedY, toX, toY);
    }

    private boolean isSameSquare(int xA, int yA, int xB, int yB) {
        return xA == xB && yA == yB;
    }

    private int numAdjacentWalls(int x, int y) {
        int numBlocks = 0;
        for (int i = 0; i < 4; i++) {
            int newX = x + Util.xOffsets[i];
            int newY = y + Util.yOffsets[i];

            if (isWall(newX, newY)) {
                numBlocks += 1;
            }
        }

        return numBlocks;
    }
}
