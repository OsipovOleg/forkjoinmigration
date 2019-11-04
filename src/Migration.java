/**
 * A migration
 */
public class Migration {
    private int sourceIndex;
    private int destinationIndex;
    private double completionTime;
    private Task task;


    /**
     * Create a new instance for migration from node_sourceIndex to node_destinationIndex
     *
     * @param sourceIndex      index of source node
     * @param destinationIndex index of destination node
     */
    public Migration(int sourceIndex, int destinationIndex) {
        this.sourceIndex = sourceIndex;
        this.destinationIndex = destinationIndex;
    }

    /**
     * Create a new instance for migration from node_sourceIndex to node_destinationIndex
     *
     * @param sourceIndex      index of source node
     * @param destinationIndex index of destination node
     * @param completionTime   completion time of the migration
     */
    public Migration(int sourceIndex, int destinationIndex, double completionTime) {
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


    public Task getTask() {
        return task;
    }


    public void setTask(Task task) {
        this.task = task;
    }
}