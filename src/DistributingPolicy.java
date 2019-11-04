import java.util.List;

public interface DistributingPolicy {
    /**
     * Return a index of node for a new task for current state
     *
     * @param localQueuesState      state of nodes
     * @return index of node or null
     */
    Integer nodeIndex(int[] localQueuesState);
}
