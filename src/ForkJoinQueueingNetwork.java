import java.util.*;


public class ForkJoinQueueingNetwork {
    //Parameters of the model
    private int countOfNodes;
    private int queuesCapacity;


    private Random random;
    private RandomVariable migrationTimeRV;
    private RandomVariable interarrivalTimeRV;
    private RandomVariable serviceTimeRV;
    private RandomVariable taskCountRV;

    private MigrationPolicy migrationPolicy;
    private DistributingPolicy distributingPolicy;


    /**
     * Global queue
     */
    private ArrayList<Task> globalQueue;
    /**
     * Array of local queues
     */
    private ArrayList<Task>[] localQueues;

    /**
     * Available for migration nodes
     */
    private Set<Integer> availableForMigrationNodes;


    //Times of events
    private double nextArrivalTime;
    private double[] startServiceTimes;
    private double[] endServiceTimes;
    private double currentTime;
    //Migrations and completion times
    private ArrayList<MigrationPair> migrations;
    //Task only in service process without migrating tasks
    private int[] state;

    //Demand counter
    private int demandCounter;


    private int[] getCurrentState() {
        state[0] = globalQueue.size();
        for (int i = 1; i < localQueues.length; i++) {
            state[i] = localQueues[i - 1].size();
        }

        return state;

    }


    /**
     * Run simulation
     *
     * @param time
     */
    public void start(double time) {

        currentTime = 0;
        nextArrivalTime = 0;
        Arrays.fill(startServiceTimes, Double.POSITIVE_INFINITY);
        Arrays.fill(endServiceTimes, Double.POSITIVE_INFINITY);


        while (currentTime <= time) {
            //Time of a next event
            int startServiceIndex = 0;
            int endServiceIndex = 0;

            //Search minimum elements in arrays
            for (int i = 1; i < countOfNodes; i++) {
                if (startServiceTimes[startServiceIndex] > startServiceTimes[i]) {
                    startServiceIndex = i;
                }
                if (endServiceTimes[endServiceIndex] > endServiceTimes[i]) {
                    endServiceIndex = i;
                }
            }


            int endMigrationIndex = 0;
            for (int i = 1; i < migrations.size(); i++) {
                if (migrations.get(endMigrationIndex).getCompletionTime() > migrations.get(i).getCompletionTime()) {
                    endMigrationIndex = i;
                }
            }


            //Get event time
            double nextEventTime = Math.min(
                    Math.min(nextArrivalTime, migrations.get(endMigrationIndex).getCompletionTime()),
                    Math.min(startServiceTimes[startServiceIndex], endServiceTimes[endServiceIndex]));

            //Update current time
            currentTime = nextEventTime;


            //Run a handler for the current event
            if (nextEventTime == nextArrivalTime) {
                arrivalEvent();
            } else if (nextEventTime == startServiceTimes[startServiceIndex]) {
                startServiceEvent(startServiceIndex);

            } else if (nextEventTime == endServiceTimes[endServiceIndex]) {
                endServiceEvent(endServiceIndex);

            } else if (nextEventTime == migrations.get(endMigrationIndex).getCompletionTime()) {

            }


            //Check need for migrations and run it
            List<MigrationPair> newMigrations = migrationPolicy.checkMigration(getCurrentState(), migrations);
            for (MigrationPair migration :
                    newMigrations) {
                runMigration(migration.getSourceIndex(), migration.getDestinationIndex());
            }
        }

    }


    /**
     * Handler of arriving
     */
    private void arrivalEvent() {
        int countOfSiblings = (int) taskCountRV.nextValue();

        //create tasks and try to distribute them
        for (int i = 0; i < countOfSiblings; i++) {
            Task task = new Task(demandCounter, currentTime, countOfSiblings);

            Integer nodeIndex = distributingPolicy.nodeIndex(getCurrentState(), migrations);
            if (nodeIndex != null) {
                localQueues[nodeIndex].add(task);
                startServiceTimes[nodeIndex] = currentTime;
            } else {
                globalQueue.add(task);
            }
        }


    }

    /**
     * Handler of start service event
     *
     * @param index of node
     */
    private void startServiceEvent(int index) {
        //check tasks in the queue
        if (localQueues[index].size() > 0) {
            //according to a PS discipline
            if (endServiceTimes[index] == Double.POSITIVE_INFINITY) {
                endServiceTimes[index] += serviceTimeRV.nextValue();
            }
        }
        startServiceTimes[index] = Double.POSITIVE_INFINITY;
    }

    /**
     * Handler of end service event
     *
     * @param index of node
     */
    private void endServiceEvent(int index) {
        //choose a random task
        int n = localQueues[index].size();
        int i = random.nextInt(n);
        Task task = localQueues[index].remove(i);

        startServiceTimes[index] = currentTime;
        endServiceTimes[index] = Double.POSITIVE_INFINITY;
    }


    /**
     * Start a migration process for a pair of nodes
     *
     * @param sourceIndex
     * @param destinationIndex
     */
    private void startMigration(int sourceIndex, int destinationIndex) {

    }


    private void endMigration(MigrationPair migration) {

    }


}
