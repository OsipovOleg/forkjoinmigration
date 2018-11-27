import java.util.List;

public interface DistributingPolicy {
    /**
     * Return a index of node for a new task for current state
     *
     * @param state      state of nodes
     * @param migrations current migration state
     * @return index of node or null
     */
    Integer nodeIndex(int[] state, List<MigrationPair> migrations);
}
