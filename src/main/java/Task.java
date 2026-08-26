public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        String status = (this.isDone) ? "X" : " ";
        return "[" + status + "] " + this.description;
    }

    /**
     * Converts this task to one line in Charlie's save-file format.
     *
     * @return the task data to write to the save file
     */
    public abstract String saveFileFormat();
}
