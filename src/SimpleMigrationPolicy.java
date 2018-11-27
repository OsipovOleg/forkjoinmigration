import java.util.List;

public class SimpleMigrationPolicy implements MigrationPolicy {
    /**
     * Check migration conditions
     *
     * @param currentState      state of queues (n_0, n_1, ..., n_(countOfNodes)
     * @param currentMigrations running migrations
     * @return list of new migrations
     */
    @Override
    public List<MigrationPair> checkMigration(int[] currentState, List<MigrationPair> currentMigrations) {
        return null;
    }
}
