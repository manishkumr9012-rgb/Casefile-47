package detective.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Suspect {
    private final String name;
    private final String role;
    private final String initials;
    private final String motive;
    private final List<String> dialogue = new ArrayList<>();
    private boolean interviewed;

    public Suspect(String name, String role, String initials, String motive) {
        this.name = name;
        this.role = role;
        this.initials = initials;
        this.motive = motive;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public String getInitials() { return initials; }
    public String getMotive() { return motive; }
    public List<String> getDialogue() { return Collections.unmodifiableList(dialogue); }
    public boolean isInterviewed() { return interviewed; }
    public void addDialogue(String line) { dialogue.add(line); }
    public void setInterviewed(boolean interviewed) { this.interviewed = interviewed; }
}
