import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> taskNodes = new HashMap<>();

    @Override
    public void add(Task task) {
        if (taskNodes.containsKey(task.getTaskId())) {
            Node existingNode = taskNodes.get(task.getTaskId());
            removeNode(existingNode);
        }
        Node newNode = new Node(task);
        taskNodes.put(task.getTaskId(), newNode);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
    }


    @Override
    public void remove(int id) {
        if (taskNodes.containsKey(id)) {
            removeNode(taskNodes.get(id));
            taskNodes.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }


    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        if (tail == node) {
            tail = node.prev;
        }
        if (head == node) {
            head = node.next;
        }
    }

}
