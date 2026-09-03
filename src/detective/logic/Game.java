package detective.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
    private final Map<String, Suspect> suspects = new LinkedHashMap<>();
    private final List<Witness> witnesses = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();
    private final List<TimelineEvent> timeline = new ArrayList<>();
    private final Set<String> evidence = new LinkedHashSet<>();
    private final Set<String> connections = new LinkedHashSet<>();
    private final List<String> journal = new ArrayList<>();
    private final DeductionBoard deductionBoard = new DeductionBoard();
    private String endingTitle = "";
    private String endingText = "";

    public Game() { reset(); }

    public void reset() {
        suspects.clear(); witnesses.clear(); locations.clear(); timeline.clear();
        evidence.clear(); connections.clear(); journal.clear(); deductionBoard.clear();
        endingTitle = ""; endingText = "";
        createSuspects(); createWitnesses(); createLocations(); createTimeline();
        journal("CASE OPENED — 11:47 PM");
        journal("Victim: Nitheesh Reedy, 22, Computer Engineering");
        journal("Investigator: Imran Khan");
        journal("Crime scene: Basement Parking");
    }

    private void createSuspects() {
        Suspect s = new Suspect("Manjunath S", "Close Friend", "MS", "Nitheesh was about to expose something Manjunath knew.");
        s.addDialogue("I was his closest friend. We argued because he would not stop investigating.");
        s.addDialogue("He told me he had proof. I thought he was making a terrible mistake.");
        s.addDialogue("I left him alone after our argument."); suspects.put(s.getName(), s);

        s = new Suspect("Om Prakesh", "Bullying / Revenge", "OP", "Nitheesh repeatedly humiliated him and cost him an opportunity.");
        s.addDialogue("Yes, I hated him. Everyone knew that.");
        s.addDialogue("I sent him angry messages. I regret that.");
        s.addDialogue("But anger does not mean I killed him."); suspects.put(s.getName(), s);

        s = new Suspect("Aaradhya Rao", "Girlfriend", "AR", "Their relationship was collapsing, and she knew his secret.");
        s.addDialogue("We had an argument. He wanted to do something I thought was dangerous.");
        s.addDialogue("He asked me to keep quiet about his research.");
        s.addDialogue("I wanted him to go to the authorities."); suspects.put(s.getName(), s);

        s = new Suspect("Maniappa", "Hostel Warden", "MA", "Nitheesh accused him of hiding a hostel access problem.");
        s.addDialogue("Nitheesh was accusing me of things he did not understand.");
        s.addDialogue("I had administrative access. That does not make me a murderer.");
        s.addDialogue("We had a personal conflict, but I wanted it settled quietly."); suspects.put(s.getName(), s);

        s = new Suspect("Yashwanth", "Roommate", "YA", "He knew Nitheesh's passwords and had access to his computer.");
        s.addDialogue("I knew the password because we were roommates.");
        s.addDialogue("I used his computer earlier that day. I should not have.");
        s.addDialogue("I did not know what he was investigating."); suspects.put(s.getName(), s);
    }

    private void createWitnesses() {
        Witness w = new Witness("Ramesh Patil", "Night Security Guard");
        w.addStatement("I saw someone walking toward the basement around 11:20 PM.");
        w.addStatement("I could not see the person's face.");
        w.addStatement("The person was wearing dark clothes."); witnesses.add(w);

        w = new Witness("Rahul Mehta", "Student Who Found the Body");
        w.addStatement("I came down because I had forgotten my charger.");
        w.addStatement("That is when I saw Nitheesh.");
        w.addStatement("I did not know what had happened."); witnesses.add(w);
    }

    private void createLocations() {
        locations.add(new Location("Basement Parking", "Primary crime scene"));
        locations.add(new Location("Security Office", "CCTV and night security records"));
        locations.add(new Location("Hostel Room", "Nitheesh's room and laptop"));
        locations.add(new Location("Hostel Office", "Hostel access records"));
        locations.add(new Location("Computer Lab", "Research backup systems"));
        locations.add(new Location("Library", "Nitheesh's personal notes"));
        locations.add(new Location("Canteen", "Student conversations"));
        locations.add(new Location("Engineering Block", "Research and project records"));
        locations.add(new Location("Student Club Room", "Aaradhya's notes"));
    }

    private void createTimeline() {
        timeline.add(new TimelineEvent("8:15 PM", "Nitheesh leaves the Engineering Block."));
        timeline.add(new TimelineEvent("9:05 PM", "An argument between Nitheesh and Om is reported."));
        timeline.add(new TimelineEvent("10:40 PM", "Manjunath says he last saw Nitheesh."));
        timeline.add(new TimelineEvent("11:18 PM", "Basement CCTV interruption begins."));
        timeline.add(new TimelineEvent("11:20 PM", "Security guard sees an unidentified person."));
        timeline.add(new TimelineEvent("11:31 PM", "CCTV resumes."));
        timeline.add(new TimelineEvent("11:47 PM", "Nitheesh is discovered."));
    }

    public Collection<Suspect> getSuspects() { return suspects.values(); }
    public List<Witness> getWitnesses() { return witnesses; }
    public List<Location> getLocations() { return locations; }
    public List<TimelineEvent> getTimeline() { return timeline; }
    public Set<String> getEvidence() { return evidence; }
    public Set<String> getConnections() { return connections; }
    public List<String> getJournal() { return journal; }
    public DeductionBoard getDeductionBoard() { return deductionBoard; }

    public void interview(Suspect s) { s.setInterviewed(true); journal("Interviewed suspect: " + s.getName()); }
    public void interview(Witness w) { w.setInterviewed(true); journal("Interviewed witness: " + w.getName()); }

    public String explore(Location location) {
        if (location.isSearched()) return "You have already searched this location thoroughly.\n\nNo new clue is visible.";
        location.setSearched(true);
        String result;
        switch (location.getName()) {
            case "Basement Parking":
                addEvidence("Blood pattern at parking bay"); addEvidence("Partial CCTV timestamp");
                result = "The basement is poorly lit. A black sedan is parked near the far wall.\n\n" +
                        "There is a small blood pattern, but no sign of a prolonged struggle.\n\n" +
                        "The entrance camera has a recording gap.\n\n" +
                        "Nitheesh appears to have arrived willingly."; break;
            case "Security Office":
                addEvidence("Security log discrepancy"); addEvidence("Partial CCTV timestamp");
                result = "The security log shows a camera interruption between 11:18 PM and 11:31 PM.\n\n" +
                        "The guard insists he did not switch it off. Someone may have known exactly when the blind spot would exist."; break;
            case "Hostel Room":
                addEvidence("Password access"); addEvidence("Deleted research file");
                result = "Nitheesh's laptop is still in the room.\n\nSeveral research files are missing, but one deleted-file record remains.\n\n" +
                        "The laptop was accessed using a password known to his roommate."; break;
            case "Hostel Office":
                addEvidence("Hostel access anomaly");
                result = "An unusual late-night entry was recorded using a maintenance override.\n\n" +
                        "The override is connected to administrative credentials."; break;
            case "Computer Lab":
                addEvidence("Research project metadata");
                result = "A backup workstation contains metadata from Nitheesh's research.\n\n" +
                        "The project was designed to trace irregular transactions through a university-linked process."; break;
            case "Library":
                addEvidence("Nitheesh's notebook");
                result = "Nitheesh's notebook contains three words:\n\nMONEY — PROJECT — PROOF\n\n" +
                        "A note tells someone to meet at the basement parking area."; break;
            case "Canteen":
                addEvidence("Threatening message");
                result = "A screenshot shows a message from Om:\n\n\"Stay away from me or you will regret it.\"\n\n" +
                        "The timestamp is earlier than the murder."; break;
            case "Engineering Block":
                addEvidence("Research project metadata");
                result = "Nitheesh's project presentation was edited recently.\n\n" +
                        "The edit history contains a user session that should not have been active that night."; break;
            default:
                addEvidence("Aaradhya knew about the research");
                result = "Aaradhya's notebook references Nitheesh's research:\n\n\"Don't let him do this alone.\"";
        }
        journal("Explored: " + location.getName());
        return result;
    }

    public void addEvidence(String item) {
        if (evidence.add(item)) journal("NEW EVIDENCE: " + item);
        if (evidence.contains("Research project metadata") && evidence.contains("Deleted research file")) {
            if (evidence.add("Final encrypted file")) journal("MAJOR DISCOVERY: final encrypted research file recovered.");
        }
        if (evidence.contains("Blood pattern at parking bay") && evidence.contains("Partial CCTV timestamp")) {
            if (evidence.add("Phone recovered")) journal("MAJOR DISCOVERY: Nitheesh's phone recovered.");
        }
    }

    public boolean hasEvidence(String item) { return evidence.contains(item); }

    public void connect(String connection) {
        if (!connections.add(connection)) return;
        String[] parts = connection.split(" → ", 2);
        if (parts.length == 2) deductionBoard.connect(parts[0], parts[1]);
        journal("DEDUCTION: " + connection);
    }

    public String getTheory() {
        if (connections.isEmpty()) return "No theory yet. Collect clues, then connect them.";
        if (connections.size() < 3) return "The timeline is beginning to form. Keep connecting evidence before accusing anyone.";
        return "The strongest theory connects Nitheesh's research, deleted files, the basement meeting and the people who knew about the research. Motive alone is not proof.";
    }

    public String[] getSuspectNames() { return suspects.keySet().toArray(new String[0]); }

    public void finishAccusation(String suspect) {
        boolean full = hasEvidence("Final encrypted file") && hasEvidence("Phone recovered") && connections.size() >= 3;
        boolean strong = hasEvidence("Partial CCTV timestamp") && hasEvidence("Deleted research file") && connections.size() >= 2;
        if ("Manjunath S".equals(suspect)) {
            if (full) {
                endingTitle = "TRUE ENDING — CASE SOLVED";
                endingText = "MANJUNATH S — THE COVER-UP EXPOSED\n\nNitheesh discovered a financial arrangement using research work to justify money moving through a university-linked project channel.\n\nHis software was the proof.\n\nManjunath knew about it and arranged the basement meeting to stop Nitheesh and destroy the research trail.\n\nOm's threats, Aaradhya's secrecy, Maniappa's conflict and Yashwanth's password access were real distractions around the larger secret.\n\nYou reconstructed the timeline and separated motive from proof.\n\nCASEFILE 47 — CLOSED.";
            } else if (strong) {
                endingTitle = "GOOD ACCUSATION — INCOMPLETE PROOF";
                endingText = "Your accusation of Manjunath is consistent with the strongest evidence.\n\nHowever, you did not recover enough final evidence to prove the entire chain beyond doubt.\n\nCASE PARTIALLY SOLVED.";
            } else {
                endingTitle = "CORRECT SUSPECT — WEAK CASE";
                endingText = "You named the correct suspect, but your evidence is too thin.\n\nSeveral suspects had genuine motives. You needed stronger proof of opportunity and method.";
            }
        } else if ("Om Prakesh".equals(suspect)) {
            endingTitle = "WRONG ENDING — THE REVENGE TRAP";
            endingText = "Om genuinely threatened Nitheesh and had a powerful revenge motive.\n\nBut you stopped at motive instead of proving opportunity and method.\n\nOM IS NOT THE MURDERER.";
        } else if ("Aaradhya Rao".equals(suspect)) {
            endingTitle = "WRONG ENDING — THE SECRET WAS NOT MURDER";
            endingText = "Aaradhya hid Nitheesh's secret because he asked her to protect the research.\n\nYou mistook secrecy and relationship conflict for proof of murder.";
        } else if ("Maniappa".equals(suspect)) {
            endingTitle = "WRONG ENDING — ACCESS IS NOT GUILT";
            endingText = "Maniappa had a personal conflict and administrative access.\n\nThe access anomaly was real, but it did not prove murder.";
        } else {
            endingTitle = "INCOMPLETE CASE — THE QUIET ROOMMATE";
            endingText = "Yashwanth knew Nitheesh's password and accessed his computer earlier that day.\n\nThat made him suspicious, but the evidence does not establish him as the murderer.";
        }
        journal("FINAL ACCUSATION: " + suspect);
    }

    private void journal(String text) { journal.add(text); }

    public String getEndingTitle() { return endingTitle; }
    public String getEndingText() { return endingText; }
}
