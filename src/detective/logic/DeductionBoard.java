package detective.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Small graph used by the investigation board.
 * Each clue/idea is a vertex and a deduction is an undirected edge.
 * BFS is provided to demonstrate graph traversal in the project.
 */
public class DeductionBoard {
    private final Map<String, Set<String>> graph = new LinkedHashMap<>();

    public void clear() {
        graph.clear();
    }

    public void addNode(String node) {
        graph.computeIfAbsent(node, key -> new LinkedHashSet<>());
    }

    public void connect(String first, String second) {
        addNode(first);
        addNode(second);
        graph.get(first).add(second);
        graph.get(second).add(first);
    }

    public boolean isConnected(String first, String second) {
        return graph.containsKey(first) && graph.get(first).contains(second);
    }

    /** Breadth-first search for the shortest deduction chain. */
    public List<String> shortestPath(String start, String target) {
        if (!graph.containsKey(start) || !graph.containsKey(target)) return Collections.emptyList();
        if (start.equals(target)) return List.of(start);

        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> parent = new LinkedHashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            for (String next : graph.getOrDefault(current, Collections.emptySet())) {
                if (!visited.add(next)) continue;
                parent.put(next, current);
                if (next.equals(target)) return buildPath(parent, start, target);
                queue.add(next);
            }
        }
        return Collections.emptyList();
    }

    private List<String> buildPath(Map<String, String> parent, String start, String target) {
        List<String> path = new ArrayList<>();
        String current = target;
        while (current != null) {
            path.add(current);
            if (current.equals(start)) break;
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    public int edgeCount() {
        int total = 0;
        for (Set<String> neighbours : graph.values()) total += neighbours.size();
        return total / 2;
    }

    public int nodeCount() {
        return graph.size();
    }
}
