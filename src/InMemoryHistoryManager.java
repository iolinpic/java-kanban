import models.Task;
import models.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;
    private int size;

    private void linkLast(Task task) {
        Node node = new Node(task, null, null);
        if (tail != null) {
            node.setPrev(tail);
            tail.setNext(node);
        } else {
            head = node;
        }
        tail = node;
        size += 1;
    }

    private ArrayList<Task> getTasks() {
        Node current = head;
        ArrayList<Task> tasks = new ArrayList<>(size);
        while (current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
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
        size -= 1;
    }


    InMemoryHistoryManager() {
        size = 0;
        head = null;
        tail = null;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        this.remove(task.getId());
        linkLast(task);
        nodes.put(task.getId(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(nodes.get(id));
        nodes.remove(id);
    }
}
