public class Util {
    private final World world;
    private final double moveProb;
    // [north, south, east, west, stay] DO NOT MODIFY
    public static final int[] X_OFFSETS = { 0, 0, 1, -1, 0 };
    public static final int[] Y_OFFSETS = { -1, 1, 0, 0, 0 };

    public Util(World world, double moveProb) {
        this.world = world;
        this.moveProb = moveProb;
    }

    public double probTrans_givenFromAndAction(int fromX, int fromY, int toX, int toY, int action) {
        double prob = 0;
        
        if (isWall(fromX, fromY) || isStair(fromX, fromY) || isWall(toX, toY)) {
            // from wall/stair OR to wall
            return 0;
        }

        if (actionLeadsFromSquareToToSquare(fromX, fromY, toX, toY, action)) {
            // action leads to the space being evaluated
            if (action == theRobot.STAY) {
                // staying: P(moves correctly) + P(moves incorrectly and bumps off wall back to it)
                prob = moveProb + (numAdjacentWalls(fromX, fromY) * ((1 - moveProb) / 4));
            } else {
                // moving: P(moves corractly) + P(accidently moves into it)
                prob = moveProb + ((1 - moveProb) / 4);
            }
        } else {
            // action doesn't lead the space being evaluated
            if (action != theRobot.STAY && isSameSquare(fromX, fromY, toX, toY)) {
                // going but need to stay: P(moves incorrectly and bumps off wall back to it) + (if moves into wall, it will stay)
                prob = numAdjacentWalls(fromX, fromY) * ((1 - moveProb) / 4);
                if (isDestAWall(fromX, fromY, action)) {
                    prob += moveProb;
                }
            } else {
                // going to wrong space or staying but need to go: P(accidently moves into it)
                prob = ((1 - moveProb) / 4);
            }
        }
        
        return prob;
    }

    public static void normalize(double[][] arr) {
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

    public boolean isWall(int x, int y) {
        // NOTE: We treat an out of bounds space as a wall
        return isOutOfBounds(x, y) || world.grid[x][y] == World.WALL;
    }

    private boolean isStair(int x, int y) {
        // NOTE: We treat an out of bounds space as a stair
        return isOutOfBounds(x, y) || world.grid[x][y] == World.STAIRWELL;
    }

    private boolean isDestAWall(int fromX, int fromY, int action) {
        int reachedX = fromX + Util.X_OFFSETS[action];
        int reachedY = fromY + Util.Y_OFFSETS[action];

        return isWall(reachedX, reachedY);
    }

    private static boolean actionLeadsFromSquareToToSquare(int fromX, int fromY, int toX, int toY, int action) {
        int reachedX = fromX + Util.X_OFFSETS[action];
        int reachedY = fromY + Util.Y_OFFSETS[action];

        return isSameSquare(reachedX, reachedY, toX, toY);
    }

    private static boolean isSameSquare(int xA, int yA, int xB, int yB) {
        return xA == xB && yA == yB;
    }

    private int numAdjacentWalls(int x, int y) {
        int numBlocks = 0;
        for (int i = 0; i < 4; i++) {
            int newX = x + Util.X_OFFSETS[i];
            int newY = y + Util.Y_OFFSETS[i];

            if (isWall(newX, newY)) {
                numBlocks += 1;
            }
        }

        return numBlocks;
    }
}
