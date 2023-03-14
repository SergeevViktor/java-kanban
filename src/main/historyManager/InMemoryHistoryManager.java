package main.historyManager;

import main.tasks.Task;
import main.util.Node;

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
        node.setPrev(oldTail);
        tail = node;
        if (oldTail == null)
            head = node;
        else
            oldTail.setNext(node);
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            list.add(node.getData());
            node = node.getNext();
        }
        return list;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.getPrev();
        Node<Task> nextNode = node.getNext();
        if (prevNode != null) {
            prevNode.setNext(nextNode);
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        } else {
            tail = prevNode;
        }
    }
}