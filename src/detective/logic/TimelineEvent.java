package detective.logic;

public class TimelineEvent {
    private final String time;
    private final String event;

    public TimelineEvent(String time, String event) {
        this.time = time;
        this.event = event;
    }

    public String getTime() { return time; }
    public String getEvent() { return event; }
}
