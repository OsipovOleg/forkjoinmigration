import java.util.Random;

/**
 * Exponential distributed random variable
 */
public class ExpRV implements RandomVariable {

    public ExpRV(Random random, double rate) {
        this.random = random;
        this.rate = rate;
    }
    private double rate;
    private Random random;


    @Override
    public double nextValue() {
            return -Math.log(random.nextDouble()) / rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
