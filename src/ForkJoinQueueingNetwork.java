import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ForkJoinQueueingNetwork {

    static  public  class PerfomanceMeasures{
        public  double RT;
        public double B;
    }


    Logger logger = Logger.getLogger(ForkJoinQueueingNetwork.class.getName());

    private void printState() {
        if (false) {
            System.out.print(globalQueue.size() + ": ");
            Arrays.stream(localQueues).forEach(item -> System.out.print(" " + item.size()));

            migrations.forEach(migration -> System.out.print("(" + migration.getSourceIndex() + ", " + migration.getDestinationIndex() + ")"));
            System.out.println();
        }
    }


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

    private int leavedTaskCounter;
    private int leavedDemandCounter;
    private int migrationCounter;


    //Performance measures
    private double averageServiceTimeOfTask;
    private double averageResponseTimeOfTask;
    private double averageResponseTimeOfDemand;
    private double averageWaitingTimeInGlobalQueue;

    private int maxState = 10;
    private double[] agregateProbs;


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


    private HashMap<Integer, Task> synchronizingQueue;


    //Times of events
    private double nextArrivalTime;
    private double[] startServiceTimes;
    private double[] endServiceTimes;
    private double currentTime;
    //Migrations and completion times
    private ArrayList<Migration> migrations;


    //Demand counter
    private int arrivedDemandCounter;


    /**
     * Return current capacity of nodes (including running migrations)
     *
     * @return
     */
    private int[] getCurrentNodesStateIncludingMigrations() {
        int[] nodesState = new int[localQueues.length];
        for (int i = 0; i < localQueues.length; i++) {
            nodesState[i] = localQueues[i].size();
        }
        for (Migration migration :
                migrations) {
            nodesState[migration.getDestinationIndex()]++;
            nodesState[migration.getSourceIndex()]++;
        }
        return nodesState;
    }

    private int[] getCurrentNodesStateWithoutMigrations() {
        int[] nodesState = new int[localQueues.length];
        for (int i = 0; i < localQueues.length; i++) {
            nodesState[i] = localQueues[i].size();
        }
        return nodesState;
    }


    public ForkJoinQueueingNetwork(int countOfNodes, int queuesCapacity, Random random,
                                   RandomVariable migrationTimeRV,
                                   RandomVariable interarrivalTimeRV,
                                   RandomVariable serviceTimeRV,
                                   RandomVariable taskCountRV,
                                   MigrationPolicy migrationPolicy,
                                   DistributingPolicy distributingPolicy) {
        this.countOfNodes = countOfNodes;
        this.queuesCapacity = queuesCapacity;
        this.random = random;
        this.migrationTimeRV = migrationTimeRV;
        this.interarrivalTimeRV = interarrivalTimeRV;
        this.serviceTimeRV = serviceTimeRV;
        this.taskCountRV = taskCountRV;
        this.migrationPolicy = migrationPolicy;
        this.distributingPolicy = distributingPolicy;

        this.agregateProbs = new double[maxState];

        logger.setLevel(Level.OFF);
    }

    /**
     * Run simulation
     *
     * @param time
     */
    public PerfomanceMeasures start(double time) {

        currentTime = 0;
        arrivedDemandCounter = 0;

        nextArrivalTime = 0;
        startServiceTimes = new double[countOfNodes];
        endServiceTimes = new double[countOfNodes];

        double nextMigrationTime = Double.POSITIVE_INFINITY;

        Arrays.fill(startServiceTimes, Double.POSITIVE_INFINITY);
        Arrays.fill(endServiceTimes, Double.POSITIVE_INFINITY);


        globalQueue = new ArrayList<>();
        localQueues = new ArrayList[countOfNodes];
        for (int i = 0; i < localQueues.length; i++) {
            localQueues[i] = new ArrayList<>();
        }

        migrations = new ArrayList<>();
        availableForMigrationNodes = new HashSet<>();
        for (int i = 0; i < countOfNodes; i++) {
            availableForMigrationNodes.add(i);
        }

        synchronizingQueue = new HashMap<>();


        int percent = 0;
        for (int i = 0; i < 100; i++) {
            System.out.print("#");
        }
        System.out.println();
        System.out.println("Current progress");


        while (currentTime <= time) {

            logger.info("Count of tasks in the global queue: " + globalQueue.size());
            if ((int) (currentTime * 100 / time) > percent) {
                percent = (int) (currentTime * 100 / time);
                System.out.print("#");
            }


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


            //момент времени окончания миграции ближайшей
            int endMigrationIndex = 0;
            double migrationCompletionTime;
            if (migrations.size() == 0) {
                migrationCompletionTime = Double.POSITIVE_INFINITY;
            } else {
                for (int i = 1; i < migrations.size(); i++) {
                    if (migrations.get(endMigrationIndex).getCompletionTime() > migrations.get(i).getCompletionTime()) {
                        endMigrationIndex = i;
                    }
                }
                migrationCompletionTime = migrations.get(endMigrationIndex).getCompletionTime();
            }


            //Get event time
            double nextEventTime = Math.min(
                    Math.min(Math.min(nextArrivalTime, migrationCompletionTime),
                            Math.min(startServiceTimes[startServiceIndex], endServiceTimes[endServiceIndex])), nextMigrationTime);

            int countOfTasksInGlobalQueue = globalQueue.size();
            if (countOfTasksInGlobalQueue < maxState) {
                agregateProbs[countOfTasksInGlobalQueue] += nextEventTime - currentTime;
            }

            //Update current time
            currentTime = nextEventTime;
            logger.info("currentTime: " + currentTime);
            logger.info("migrations count: " + migrations.size());
            logger.info("global queues count: " + globalQueue.size());


            //Run a handler for the current event
            if (nextEventTime == nextArrivalTime) {
                arrivalEvent();
            } else if (nextEventTime == startServiceTimes[startServiceIndex]) {
                startServiceEvent(startServiceIndex);
                continue;
            } else if (nextEventTime == endServiceTimes[endServiceIndex]) {
                endServiceEvent(endServiceIndex);
                nextMigrationTime = currentTime;
                continue;
            } else if (migrations.size() != 0 && nextEventTime == migrations.get(endMigrationIndex).getCompletionTime()) {
                endMigration(migrations.get(endMigrationIndex));
                nextMigrationTime = currentTime;
                continue;
            } else if (nextEventTime == nextMigrationTime) {
                tryMigrate();
                nextMigrationTime = Double.POSITIVE_INFINITY;
                continue;
            }

        }


        System.out.println();
        averageServiceTimeOfTask /= leavedTaskCounter;
        averageResponseTimeOfTask /= leavedTaskCounter;
        averageResponseTimeOfDemand /= leavedDemandCounter;
        averageWaitingTimeInGlobalQueue /= leavedTaskCounter;
        double averageCountOfMigrations = (double) migrationCounter / leavedTaskCounter;


        System.out.println("averageSTT = " + averageServiceTimeOfTask);
        System.out.println("averageRTT = " + averageResponseTimeOfTask);
        System.out.println("averageRTD = " + averageResponseTimeOfDemand);
        System.out.println("averageWaitingInGloabalQueue = " + averageWaitingTimeInGlobalQueue);
        System.out.println("averageMigrationsCount = " + averageCountOfMigrations);

        double averageNumberOfFragmentsInGlobalQueue = 0;
        for (int i = 0; i < agregateProbs.length; i++) {
            averageNumberOfFragmentsInGlobalQueue += i * agregateProbs[i] / time;
        }
        System.out.println("averageNumberOfFragmentsInGlobalQueue = " + averageNumberOfFragmentsInGlobalQueue);


        System.out.println("CHECK by Little law " + ((DiscreteRV)taskCountRV).ExpectValue()*averageWaitingTimeInGlobalQueue);




        for (int i = 0; i < agregateProbs.length; i++) {
            System.out.println("agrgP" + i + " = " + agregateProbs[i] / time);
        }


        PerfomanceMeasures pm = new PerfomanceMeasures();
        pm.B = ((DiscreteRV)taskCountRV).ExpectValue()*averageWaitingTimeInGlobalQueue;
        pm.RT = averageResponseTimeOfDemand;
        return pm;

    }


    private void tryMigrate() {
        List<Migration> newMigration = migrationPolicy.checkMigration(getCurrentNodesStateIncludingMigrations(),
                migrations, availableForMigrationNodes);
        if (newMigration != null) {
            for (Migration migration :
                    newMigration) {
                startMigration(migration.getSourceIndex(), migration.getDestinationIndex());
            }
        }
    }

    /**
     * Handler of arriving
     */
    private void arrivalEvent() {

        //System.out.println("Поступление");
        printState();
        int countOfSiblings = (int) taskCountRV.nextValue();
        //System.out.println("Сейчас поступит " + countOfSiblings + " фрагентов,  перешли в состояние:");

        logger.info("Arrivals " + countOfSiblings + " tasks at time:" + currentTime);


        //create tasks and try to distribute them
        for (int i = 0; i < countOfSiblings; i++) {
            Task task = new Task(arrivedDemandCounter, currentTime, countOfSiblings);

            //get a node to send a task
            Integer nodeIndex = distributingPolicy.nodeIndex(getCurrentNodesStateIncludingMigrations());
            if (nodeIndex != null) {
                localQueues[nodeIndex].add(task);
                task.setStartServiceTime(currentTime);
                startServiceTimes[nodeIndex] = currentTime;
            } else {
                globalQueue.add(task);
            }
        }
        nextArrivalTime += interarrivalTimeRV.nextValue();
        arrivedDemandCounter++;

        printState();
    }

    /**
     * Handler of start service event
     *
     * @param index of node
     */
    private void startServiceEvent(int index) {
        logger.info("Try to start service at node " + index + "  at time: " + currentTime);

        //check utilization including migrations
        if ((globalQueue.size() > 0) && (getCurrentNodesStateIncludingMigrations()[index] <= queuesCapacity)) {
            Task task = globalQueue.remove(0);
            task.setStartServiceTime(currentTime);
            localQueues[index].add(task);

        }
        //check tasks in the queue
        if (localQueues[index].size() > 0) {
            //according to a PS discipline
            if (endServiceTimes[index] == Double.POSITIVE_INFINITY) {
                endServiceTimes[index] = currentTime + serviceTimeRV.nextValue();
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

        logger.info("End service at  node " + index + "  at time: " + currentTime);

        //choose a random task
        int n = localQueues[index].size();
        int i = random.nextInt(n);
        Task task = localQueues[index].remove(i);
        task.setEndServiceTime(currentTime);

        //get some statistics
        leavedTaskCounter++;
        averageResponseTimeOfTask += task.getEndServiceTime() - task.getArrivalTime();
        averageServiceTimeOfTask += task.getEndServiceTime() - task.getStartServiceTime();
        averageWaitingTimeInGlobalQueue += task.getStartServiceTime() - task.getArrivalTime();

        //Search siblings in the synch queue
        if (task.getCountOfSiblings() == 1) {
            synchronizingQueue.put(task.getParentId(), task);
        }
        if (synchronizingQueue.containsKey(task.getParentId())) {
            Task synchTask = synchronizingQueue.get(task.getParentId());
            synchTask.setCountOfSiblings(synchTask.getCountOfSiblings() - 1);
            if (synchTask.getCountOfSiblings() <= 1) {
                averageResponseTimeOfDemand += currentTime - synchTask.getArrivalTime();
                leavedDemandCounter++;
                logger.info("Tasks have been joined");
                synchronizingQueue.remove(synchTask.getParentId());
            }
        } else {
            synchronizingQueue.put(task.getParentId(), task);
        }


        startServiceTimes[index] = currentTime;
        endServiceTimes[index] = Double.POSITIVE_INFINITY;
        //startServiceEvent(index);
    }


    /**
     * Start a migration process for a pair of nodes
     *
     * @param sourceIndex
     * @param destinationIndex
     */
    private void startMigration(int sourceIndex, int destinationIndex) {
        logger.warning("start migration from " + sourceIndex + " to " + destinationIndex + " for state " + Arrays.toString(getCurrentNodesStateIncludingMigrations()));
        availableForMigrationNodes.remove(sourceIndex);
        availableForMigrationNodes.remove(destinationIndex);
        Migration migration = new Migration(sourceIndex, destinationIndex, currentTime + migrationTimeRV.nextValue());
        //get a task for migration
        int taskIndex = random.nextInt(localQueues[sourceIndex].size());
        Task migratingTask = localQueues[sourceIndex].remove(taskIndex);
        migration.setTask(migratingTask);
        migrations.add(migration);

        //if the local queue is empty now then update endServiceTime as infinity
        if (localQueues[sourceIndex].size() == 0) {
            endServiceTimes[sourceIndex] = Double.POSITIVE_INFINITY;
        }

        migrationCounter++;
    }


    private void endMigration(Migration migration) {
        //return nodes after the  migration
        availableForMigrationNodes.add(migration.getDestinationIndex());
        availableForMigrationNodes.add(migration.getSourceIndex());

        //Add the migrated task to a  destination node
        localQueues[migration.getDestinationIndex()].add(migration.getTask());

        //delete current migrations
        migrations.remove(migration);

        startServiceTimes[migration.getSourceIndex()] = currentTime;
        startServiceTimes[migration.getDestinationIndex()] = currentTime;

    }


}
