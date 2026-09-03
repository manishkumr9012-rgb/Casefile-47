package detective.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Witness {
    private final String name;
    private final String role;
    private final List<String> statements = new ArrayList<>();
    private boolean interviewed;

    public Witness(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public List<String> getStatements() { return Collections.unmodifiableList(statements); }
    public boolean isInterviewed() { return interviewed; }
    public void addStatement(String statement) { statements.add(statement); }
    public void setInterviewed(boolean interviewed) { this.interviewed = interviewed; }
}
