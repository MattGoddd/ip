public class Event extends Task {
    protected String from;
    protected String to;
    public Event(String description, boolean isDone, String from, String to) {
        super(description, isDone);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }

    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "E" + " | " + status + " | " + this.description + " | "
                + this.from + " | " + this.to;
    }
}
