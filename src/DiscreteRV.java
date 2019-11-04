import java.util.Arrays;
import java.util.Random;

public class DiscreteRV implements RandomVariable {

    private double[] probabilities;
    private Random random;

    public DiscreteRV(double[] probabilities, Random random) {
        this.probabilities = probabilities;
        this.random = random;

    }


    public static void main(String[] args) {
        double[] probs = {0.1, 0.3, 0.6};
        DiscreteRV rv = new DiscreteRV(probs, new Random());
        double[] expProbs = new double[probs.length];
        int N = 1000000;
        double sum = 0;
        for (int i = 0; i < N; i++) {
            int r = (int) rv.nextValue();
            expProbs[r - 1]++;
            sum += r;
        }
        System.out.println("Average = " + sum / N);
        double mean = 0;
        for (int i = 0; i < probs.length; i++) {
            mean += (i + 1) * probs[i];
        }
        System.out.println("Mean = " + mean);
        System.out.println(Arrays.toString((Arrays.stream(expProbs).map(operand -> operand / N)).toArray()));
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
        return probabilities.length;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public double ExpectValue() {
        double mean = 0;
        for (int i = 0; i < probabilities.length; i++) {
            mean += probabilities[i] * (i + 1);
        }
        return  mean;
    }
}
