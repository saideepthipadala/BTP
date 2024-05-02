public class TrustScore {
    private int taskcompletion;
    private int timelinessSubtaskCompletion;
    private int correctionOfSubtaskResults;
    private int correctionOfTaskResults;
    private int correctnessOfVerificationSubtask;
    private int correctnessOfVerificationTask;

    public TrustScore() {

    }

    public int getTaskcompletion() {
        return taskcompletion;
    }

    public void setTaskcompletion(int taskcompletion) {
        this.taskcompletion = taskcompletion;
    }

    public int getTimelinessSubtaskCompletion() {
        return timelinessSubtaskCompletion;
    }

    public void setTimelinessSubtaskCompletion(int timelinessSubtaskCompletion) {
        this.timelinessSubtaskCompletion = timelinessSubtaskCompletion;
    }

    public int getCorrectionOfSubtaskResults() {
        return correctionOfSubtaskResults;
    }

    public void setCorrectionOfSubtaskResults(int correctionOfSubtaskResults) {
        this.correctionOfSubtaskResults = correctionOfSubtaskResults;
    }

    public int getCorrectionOfTaskResults() {
        return correctionOfTaskResults;
    }

    public void setCorrectionOfTaskResults(int correctionOfTaskResults) {
        this.correctionOfTaskResults = correctionOfTaskResults;
    }

    public int getCorrectnessOfVerificationSubtask() {
        return correctnessOfVerificationSubtask;
    }

    public void setCorrectnessOfVerificationSubtask(int correctnessOfVerificationSubtask) {
        this.correctnessOfVerificationSubtask = correctnessOfVerificationSubtask;
    }

    public int getCorrectnessOfVerificationTask() {
        return correctnessOfVerificationTask;
    }

    public void setCorrectnessOfVerificationTask(int correctnessOfVerificationTask) {
        this.correctnessOfVerificationTask = correctnessOfVerificationTask;
    }

    public TrustScore(int taskcompletion, int timelinessSubtaskCompletion, int correctionOfSubtaskResults,
            int correctionOfTaskResults, int correctnessOfVerificationSubtask, int correctnessOfVerificationTask) {
        this.taskcompletion = taskcompletion;
        this.timelinessSubtaskCompletion = timelinessSubtaskCompletion;
        this.correctionOfSubtaskResults = correctionOfSubtaskResults;
        this.correctionOfTaskResults = correctionOfTaskResults;
        this.correctnessOfVerificationSubtask = correctnessOfVerificationSubtask;
        this.correctnessOfVerificationTask = correctnessOfVerificationTask;
    }

    public float calculateTrustScore() {
        float trust = (float) (0.2 * this.taskcompletion + 0.1 * this.correctionOfSubtaskResults
                + 0.3 * this.correctionOfTaskResults + 0.1 * this.correctnessOfVerificationSubtask
                + 0.1 * timelinessSubtaskCompletion + 0.1 * correctnessOfVerificationTask);
        return trust;
    }

}
