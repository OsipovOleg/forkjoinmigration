import java.util.*;


public class ForkJoinQueueingNetwork {
    //Parameters of the model
    private int countOfNodes;
    private int queuesCapacity;


    private Random random;
    private RandomVariable migrationTimeRV;
    private RandomVariable interarrivalTimeRV;
    private RandomVariable serviceTimeRV;


    /**
     * A migration pair (from, to)
     */
    private class MigrationPair {
        private int sourceIndex;
        private int destinationIndex;
        private double completionTime;


        /**
         * Create a new instance for a migration from node_sourceIndex to node_
         *
         * @param sourceIndex      index of source node
         * @param destinationIndex index of destination node
         * @param completionTime   completion time of the migration
         */
        public MigrationPair(int sourceIndex, int destinationIndex, double completionTime) {
            this.sourceIndex = sourceIndex;
            this.destinationIndex = destinationIndex;
            this.completionTime = completionTime;
        }

        public int getSourceIndex() {
            return sourceIndex;
        }

        public int getDestinationIndex() {
            return destinationIndex;
        }

        public double getCompletionTime() {
            return completionTime;
        }

    }

    private enum EventType {
        ARRIVAL,
        START_SERVICE,
        END_SERVICE
    }


    /**
     * Global queue
     */
    private Queue<Task> globalQueue;
    /**
     * Array of local queues
     */
    private Queue<Task>[] localQueues;

    /**
     * Available for migration nodes
     */
    private Set<Integer> availableForMigrationNodes;


    //Times of events
    private double nextArrivalTime;
    private double[] startServiceTimes;
    private double[] endServiceTimes;
    //Migrations and completion times
    private ArrayList<MigrationPair> migrations;


    /**
     * Run simulation
     *
     * @param time
     */
    public void start(double time) {

        double currentTime = 0;
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

                tryMigration();
                continue;
            }
            if (nextEventTime == startServiceTimes[startServiceIndex]) {
                tryMigration();
                continue;
            }
            if (nextEventTime == endServiceTimes[endServiceIndex]) {
                tryMigration();
                continue;
            }
            if (nextEventTime == migrations.get(endMigrationIndex).getCompletionTime()) {
                tryMigration();
                continue;
            }
        }

    }


    /**
     * Handler of arriving
     */
    private void arrivalEvent() {
    }

    /**
     * Handler of start service event
     *
     * @param index of node
     */
    private void startServiceEvent(int index) {
    }

    /**
     * Handler of end service event
     *
     * @param index of node
     */
    private void endServiceEvent(int index) {

    }


    /**
     * Run migration process for a pair of nodes
     *
     * @param sourceIndex
     * @param destinationIndex
     */
    private void runMigration(int sourceIndex, int destinationIndex) {

    }

    /**
     * Make decision about starting migrations
     */
    private void tryMigration() {

    }


}
