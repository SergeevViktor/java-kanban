package manager;

import tasks.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> mapItemsContainer = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        Node<Task> node = new Node<>(null, task, null);
        if (mapItemsContainer.containsKey(task.getId())) {
            removeNode(mapItemsContainer.get(task.getId()));
        }
        linkLast(node);
        mapItemsContainer.put(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (mapItemsContainer.containsKey(id)) {
            removeNode(mapItemsContainer.get(id));
            mapItemsContainer.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Node<Task> node) {
        final Node<Task> oldTail = tail;
        node.prev = oldTail;
        tail = node;
        if (oldTail == null)
            head = node;
        else
            oldTail.next = node;
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
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        }
        if (prevNode == null && nextNode != null) {
            head = nextNode;
            nextNode.prev = null;
        }
        if (prevNode != null && nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        }
        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }
}
