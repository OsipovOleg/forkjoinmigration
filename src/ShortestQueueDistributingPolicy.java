import java.util.List;

public class ShortestQueueDistributingPolicy implements DistributingPolicy {


    private int queuesCapacity;

    /**
     * @param queuesCapacity capacity of each queues
     */
    public ShortestQueueDistributingPolicy(int queuesCapacity) {
        this.queuesCapacity = queuesCapacity;
    }

    /**
     * Return a index of node for a new task for current state of nodes and migrations
     *
     * @param state      state of nodes
     * @param migrations current migration state
     * @return index of node or null
     */
    @Override
    public Integer nodeIndex(int[] state, List<MigrationPair> migrations) {
        int index = 0;
        int[] capacities = state.clone();

        for (MigrationPair migration :
                migrations) {
            capacities[migration.getDestinationIndex()]++;
            capacities[migration.getSourceIndex()]++;
        }
        for (int i = 1; i < state.length; i++) {
            if (capacities[index] > capacities[i]) {
                index = i;
            }
        }

        if (capacities[index] <= queuesCapacity)
            return index;
        return null;
    }
}
