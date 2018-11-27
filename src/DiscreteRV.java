import java.util.Random;

public class DiscreteRV implements RandomVariable {

    private double[] probabilities;
    private Random random;

    public DiscreteRV(double[] probabilities, Random random) {
        this.probabilities = probabilities;
        this.random = random;

    }


    /**
     * Generate a value from 1 to n according to  a distribution
     *
     * @return
     */
    @Override
    public double nextValue() {
        double r = random.nextDouble();
        double sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (r <= sum)
                return i + 1;
        }
        return probabilities.length + 1;
    }

    public double[] getProbabilities() {
        return probabilities;
    }
}
