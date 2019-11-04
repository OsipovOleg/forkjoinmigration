import javax.xml.transform.sax.SAXSource;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

public class Main {


    static int countOfNodes = 6;
    static int queuesCapacity = 15;
    static double migrationRate = 1;
    static double lambda = 1;
    static double mu = 1.5;

    static int delta = 1;
    static double[] probabilities = {0, 0, 0, 0, 0, 0, 0, 1};


    static double time = 10000;

    public static ForkJoinQueueingNetwork.PerfomanceMeasures run() {


//        agrgDP0 = 0.7501406318418999
//        agrgDP1 = 0.05445197599561629
//        agrgDP2 = 0.043099149898741924
//        agrgDP3 = 0.033641666050628016
//        agrgDP4 = 0.026274365116522057
//        agrgDP5 = 0.020613335130487527
//        agrgDP6 = 0.016117659492665228
//        agrgDP7 = 0.01261288510859345

//        [0.723]
//        [0.076]
//        [0.052]
//        [0.032]
//        [0.028]
//        [0.02]
//        [0.015]
//        [0.012]
//        [0.009]
//        [0.007]

        System.out.println("Probs");
        System.out.println(Arrays.toString(probabilities));

        System.out.println("sum of prob dist = " + Arrays.stream(probabilities).sum());

        Random random = new Random();
        RandomVariable taskCountRV = new DiscreteRV(probabilities, random);
        RandomVariable migrationTimeRV = new ExpRV(random, migrationRate);
        RandomVariable interarrivalTimeRV = new ExpRV(random, lambda);
        RandomVariable serviceTimeRV = new ExpRV(random, mu);


        DistributingPolicy distributingPolicy = new ShortestQueueDistributingPolicy(queuesCapacity);
        MigrationPolicy migrationPolicy = new SimpleMigrationPolicy(queuesCapacity, delta);


        ForkJoinQueueingNetwork network = new ForkJoinQueueingNetwork(countOfNodes, queuesCapacity, random,
                migrationTimeRV, interarrivalTimeRV, serviceTimeRV, taskCountRV, migrationPolicy, distributingPolicy);

        return network.start(time);
    }


    public static void LittleMigrateRate_LevelStudy() {

        int expNumber = 10;

        StringBuilder builder = new StringBuilder();

        time = 10000000;


        builder.append("lambda");
        for (int i = 1; i < 10; i++) {
            builder.append(" RT" + i);
            builder.append(" B" + i);
        }
        builder.append("\n");

        NumberFormat formatter = new DecimalFormat("#0.0000");


        for (int i = 0; i < expNumber; i++) {
            lambda = (i + 1) * 0.1;

            builder.append(formatter.format(lambda));
            for (int deltaLevel = 1; deltaLevel < 10; deltaLevel++) {
                delta = deltaLevel;
                System.out.println();
                System.out.println();
                System.out.println("LAMBDA = " + lambda);
                ForkJoinQueueingNetwork.PerfomanceMeasures pm = run();
                builder.append(" " + formatter.format(pm.RT) + " " + formatter.format(pm.B));

            }
            builder.append("\n");
        }


        System.out.println();
        System.out.println();
        System.out.println(builder);


    }


    public static void BigMigrateRate_LevelStudy() {

        int expNumber = 10;

        StringBuilder builder = new StringBuilder();

        time = 10000000;



        int MigrationsLevels = 18;

        builder.append("lambda");
        for (int i = 1; i < MigrationsLevels; i++) {
            builder.append(" RT" + i);
            //builder.append(" B" + i);
        }
        builder.append("\n");

        NumberFormat formatter = new DecimalFormat("#0.0000");


        for (int i = 0; i < 1; i++) {
            //lambda = (i + 1) * 0.1;
            lambda=0.7;

            builder.append(formatter.format(lambda));
            for (int deltaLevel = 1; deltaLevel < MigrationsLevels; deltaLevel++) {
                delta = deltaLevel;
                System.out.println();
                System.out.println();
                System.out.println("LAMBDA = " + lambda);
                ForkJoinQueueingNetwork.PerfomanceMeasures pm = run();
                builder.append(" " + formatter.format(pm.RT));

            }
            builder.append("\n");
        }


        System.out.println();
        System.out.println();
        System.out.println(builder);


    }


    public static void main(String[] args) {

        BigMigrateRate_LevelStudy();

    }
}
