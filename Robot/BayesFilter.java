public class BayesFilter {
    public static double[][] filter(final double[][] oldProbs, final int action, final String sonars) {
        assert(oldProbs.length > 0 && oldProbs[0].length > 0);
        
        int numRows = oldProbs.length;
        int numCols = oldProbs[0].length;
        double[][] newProbs = new double[numRows][numCols];

        // DELETEME
        double evenDistribution = 1 / (numRows * numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                newProbs[i][j] = 0;
            }
        }
        
        
        return newProbs;
    }
    
    private static double transitionModel() {
        return 1.0;
    }

    private static double sensorModel() {
        return 1.0;
    }

    private static double[][] normalize() {
        return new double[1][1];
    }
}
