import java.util.List;
import java.util.Set;

/**
 *  Describe a migration policy
 */
public interface MigrationPolicy {
    /**
     * Checks migration conditions and returns a list with all proper migrations
     *
     * @param currentState      state of queues (n_1, ..., n_(countOfNodes)
     * @param currentMigrations running migrations
     * @return list of new migrations
     */
    List<Migration> checkMigration(int[] currentState,
                                   List<Migration> currentMigrations,
                                   Set<Integer> availableForMigrationNodes);
}
