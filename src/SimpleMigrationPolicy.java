import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimpleMigrationPolicy implements MigrationPolicy {


    private int queuesCapacity;
    private int delta;

    public SimpleMigrationPolicy(int queuesCapacity, int delta) {
        this.queuesCapacity = queuesCapacity;
        this.delta = delta;
    }

    /**
     * Check migration conditions
     *
     * @param currentState               state of queues (n_1, ..., n_(countOfNodes)
     * @param currentMigrations          running migrations
     * @param availableForMigrationNodes
     * @return list of new migrations
     */
    @Override
    public List<Migration> checkMigration(int[] currentState,
                                          List<Migration> currentMigrations,
                                          Set<Integer> availableForMigrationNodes) {
        if (availableForMigrationNodes.size() == 0) return null;


        int minIndex = availableForMigrationNodes.iterator().next();
        int maxIndex = minIndex;


        for (int i = 0; i < currentState.length; i++) {

            if (!availableForMigrationNodes.contains(i)) continue;

            if (currentState[minIndex] > currentState[i]) {
                minIndex = i;
            }
            if (currentState[maxIndex] < currentState[i]) {
                maxIndex = i;
            }
        }

        if (minIndex == maxIndex) return null;
        if (currentState[maxIndex] - currentState[minIndex] <= delta) return null;
        //TODO: check the condition
        if (currentState[minIndex] > queuesCapacity+1) return null;

        List<Migration> newMigrations = new ArrayList<>();
        newMigrations.add(new Migration(maxIndex, minIndex));

        return newMigrations;

    }
}
