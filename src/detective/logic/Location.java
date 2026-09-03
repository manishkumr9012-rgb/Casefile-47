package detective.logic;

public class Location {
    private final String name;
    private final String description;
    private boolean searched;

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isSearched() { return searched; }
    public void setSearched(boolean searched) { this.searched = searched; }
}
