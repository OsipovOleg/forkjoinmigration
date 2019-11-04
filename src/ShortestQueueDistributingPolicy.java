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
     * Return an index of node for a new task for the current state of nodes and migrations
     *
     * @param localQueuesState      state of nodes (including migrations)
     * @return index of node or null
     */
    @Override
    public Integer nodeIndex(int[] localQueuesState) {
        int minIndex = 0;

        //just find minimum index
        for (int i = 1; i < localQueuesState.length; i++) {
            if (localQueuesState[minIndex] > localQueuesState[i]) {
                minIndex = i;
            }
        }

        if (localQueuesState[minIndex] <= queuesCapacity)
            return minIndex;
        return null;
    }
}
