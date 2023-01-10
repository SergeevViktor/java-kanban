package manager;

import tasks.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Node<Task>> history = new LinkedList<>();
    private final HashMap<Integer, Node<Task>> mapItems = new HashMap<>();

    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        if (mapItems.containsKey(node.data.getId())) {
            mapItems.remove(node.data.getId());
        }
        history.linkLast(node);
        mapItems.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            list.add(node.data);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node<Task> node) {
    }
}
