/**
 * Create an instance of this class each time you recompute probs
 */
public class BayesFilter {
    private final World world;
    private final double[][] oldProbs;
    private final double moveProb;
    private final double sensorAccuracy;

    private final int action;
    private final String sonars;
    // [north, south, east, west, stay] DO NOT MODIFY
    private final int[] rowOffsets = { -1, 1, 0, 0, 0 };
    private final int[] colOffsets = { 0, 0, 1, -1, 0 };
    
    public BayesFilter(final World world, final double[][] oldProbs, final double moveProb, final double sensorAccuracy, final int action, final String sonars) {
        this.world = world;
        this.oldProbs = oldProbs;
        this.moveProb = moveProb;
        this.sensorAccuracy = sensorAccuracy;
        this.action = action;
        this.sonars = sonars;
    }
    
    public double[][] filter() {
        assert(oldProbs.length > 0 && oldProbs[0].length > 0);
        
        int numRows = oldProbs.length;
        int numCols = oldProbs[0].length;
        double[][] newProbs = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // System.out.println("i: " + i + ", j: " + j + ": " + oldProbs[i][j]);
                newProbs[i][j] = transitionModel(i, j);
            }
        }

        normalize(newProbs);
        
        return newProbs;
    }

    /**
     * Runs the transition model for transitioning INTO a given square
     */
    private double transitionModel(final int toRow, final int toCol) {
        double prob = 0.0;
        for (int i = 0; i < rowOffsets.length; i++) { // visit up, down, right, left, stay
            int fromRow = toRow + rowOffsets[i];
            int fromCol = toCol + colOffsets[i];
            double probFromAdj = 0;

            if (isWall(fromRow, fromCol) || isStair(fromRow, fromCol) || isWall(toRow, toCol)) {
                // from wall/stair OR to wall
                continue;
            }

            if (actionLeadsFromSquareToToSquare(fromRow, fromCol, toRow, toCol)) {
                // action leads to the space being evaluated
                if (action == theRobot.STAY) {
                    // staying: P(moves correctly) + P(moves incorrectly and bumps off wall back to it)
                    probFromAdj = moveProb + (numAdjacentWalls(fromRow, fromCol) * ((1 - moveProb) / 4));
                } else {
                    // moving: P(moves corractly) + P(accidently moves into it)
                    probFromAdj = moveProb + ((1 - moveProb) / 4);
                }
            } else {
                // action doesn't lead the space being evaluated
                if (action != theRobot.STAY && isSameSquare(fromRow, fromCol, toRow, toCol)) {
                    // going but need to stay: P(moves incorrectly and bumps off wall back to it)
                    probFromAdj = numAdjacentWalls(fromRow, fromCol) * ((1 - moveProb) / 4);
                } else {
                    // going to wrong space or staying but need to go OR TODO OTHER CASES???
                    // P(accidently moves into it)
                    probFromAdj = ((1 - moveProb) / 4);
                }
            }

            // multiply result of transition model by Bel(x_(t-1)) from the curr adj square
            probFromAdj *= oldProbs[fromRow][fromCol];
            prob += probFromAdj;
        }

        return prob;
    }

    private static double sensorModel() {
        // TODO
        return 1.0;
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

    // TODO change to private
    public boolean isOutOfBounds(int row, int col) {
        return (row < 0 || col < 0 || row >= world.height || col >= world.height);
    }

    public boolean isWall(int row, int col) {
        // NOTE: We treat an out of bounds space as a wall
        return isOutOfBounds(row, col) || world.grid[row][col] == World.WALL;
    }

    private boolean isStair(int row, int col) {
        // NOTE: We treat an out of bounds space as a stair
        return isOutOfBounds(row, col) || world.grid[row][col] == World.STAIRWELL;
    }

    public boolean actionLeadsFromSquareToToSquare(int fromRow, int fromCol, int toRow, int toCol) {
        int reachedRow = fromRow + rowOffsets[action];
        int reachedCol = fromCol + colOffsets[action];

        return isSameSquare(reachedRow, reachedCol, toRow, toCol);
    }

    public boolean isSameSquare(int rowA, int colA, int rowB, int colB) {
        return rowA == rowB && colA == colB;
    }

    public int numAdjacentWalls(int row, int col) {
        int numBlocks = 0;
        for (int i = 0; i < 4; i++) {
            int newRow = row + rowOffsets[i];
            int newCol = col + colOffsets[i];

            if (isWall(newRow, newCol)) {
                numBlocks += 1;
            }
        }

        return numBlocks;
    }
}
