import models.Node;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodes;
    private Node head;
    private Node tail;

    private void linkLast(Task task) {
        Node node = new Node(task, null, null);
        if (tail != null) {
            node.setPrev(tail);
            tail.setNext(node);
        } else {
            head = node;
        }
        tail = node;
    }

    private ArrayList<Task> getTasks() {
        Node current = head;
        ArrayList<Task> tasks = new ArrayList<>(nodes.size());
        while (current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        } else {
            head = node.getNext();
        }
        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }
    }


    InMemoryHistoryManager() {
        head = null;
        tail = null;
        nodes = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            this.remove(task.getId());
        }
        linkLast(task);
        nodes.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(nodes.get(id));
        nodes.remove(id);
    }
}
