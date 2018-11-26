public class Task {

    private long parentId;
    private double arrivalTime;
    private double startServiceTime;
    private double endServiceTime;
    private double leaveTime;


    public Task(long parentId, double arrivalTime) {
        this.parentId = parentId;
        this.arrivalTime = arrivalTime;
    }


    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(double leaveTime) {
        this.leaveTime = leaveTime;
    }

    public double getStartServiceTime() {
        return startServiceTime;
    }

    public void setStartServiceTime(double startServiceTime) {
        this.startServiceTime = startServiceTime;
    }

    public double getEndServiceTime() {
        return endServiceTime;
    }

    public void setEndServiceTime(double endServiceTime) {
        this.endServiceTime = endServiceTime;
    }
}
