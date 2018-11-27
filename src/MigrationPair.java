/**
 * A migration pair (from, to)
 */
public class MigrationPair {
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