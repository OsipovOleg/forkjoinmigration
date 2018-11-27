import java.util.List;

public interface MigrationPolicy {
    /**
     * Check migration conditions
     *
     * @param currentState      state of queues (n_0, n_1, ..., n_(countOfNodes)
     * @param currentMigrations running migrations
     * @return list of new migrations
     */
    public List<MigrationPair> checkMigration(int[] currentState, List<MigrationPair> currentMigrations);
}
