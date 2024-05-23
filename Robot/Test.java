public class Test {
    public static void main(String[] args) {
        // World world = new World("testworld.txt");
        World world = new World("mundo_maze.txt");

        double[][] probs = {
            {0,0,0,0,0},
            {0,1,1,1,0},
            {0,1,1,1,0},
            {0,1,1,1,0},
            {0,0,0,0,0}
        };

        BayesFilter bayesFilter = new BayesFilter(world, probs, 0.9, 1, theRobot.EAST, "1111");
        printWorld(world.grid);
        
        // probs = bayesFilter.filter();
        // printProbs(probs);
        // probs = new BayesFilter(world, probs, 0.9, 1, theRobot.EAST, "1111").filter();
        // System.out.println();
        // printProbs(probs);

        // if (bayesFilter.actionLeadsFromSquareToToSquare(0, 0, 0, -1)) {
        //     System.out.println("moves to");
        // } else {
        //     System.out.println("doesn't move to");
        // }
        // for (int i = 0; i < probs.length; i++) {
        //     for (int j = 0; j < probs[0].length; j++) {
        //         if (bayesFilter.isOutOfBounds(i, j)) {
        //             System.out.println("BAD");
        //         } else {
        //             System.out.println("GOOD");
        //         }
        //     }
        // }
        // int[] fromRows = {0,0,4,4};
        // int[] fromCols = {0,4,0,4};
        // int[] rowsB = {-1,1};
        // int[] colsB = {0,2};
        // System.out.println(world.height);
        // System.out.println(world.width);
        // for (int i = 0; i < rowsA.length; i++) {
        //     System.out.println(bayesFilter.numAdjacentWalls(rowsA[i], colsA[i]));
        //     // if (bayesFilter.isSameSquare(rowsA[i], colsA[i], rowsB[i], colsB[i])) {
        //     //     System.out.println("same");
        //     // } else {
        //     //     System.out.println("not same");
        //     // }
        // }


    }

    private static void printProbs(double[][] probs) {
        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < probs[0].length; j++) {
                System.out.print(probs[i][j] + ",");
            }
            System.out.println();
        }
    }

    private static void printWorld(int[][] probs) {
        for (int i = 0; i < probs.length; i++) {
            for (int j = 0; j < probs[0].length; j++) {
                System.out.print(probs[j][i] + ",");
            }
            System.out.println();
        }
    }
}
