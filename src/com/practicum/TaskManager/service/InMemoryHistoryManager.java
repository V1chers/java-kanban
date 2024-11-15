package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Task;
import com.practicum.TaskManager.model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    final private Map<Integer, Node> history;
    private Node first;
    private Node last;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        first = null;
        last = null;
    }

    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }

        linkLast(task);
        history.put(task.getId(), last);
    }

    public List<Task> getHistory() {
        boolean haveNextNode = true;
        Node currentNode = last;
        ArrayList<Task> tasks = new ArrayList<>();

        if (currentNode == null) {
            return null;
        }

        while (haveNextNode) {
            tasks.add(currentNode.getTask());
            if (currentNode.getPrev() == null) {
                haveNextNode = false;
            } else {
                currentNode = currentNode.getPrev();
            }
        }

        return Collections.unmodifiableList(tasks);
    }

    public void remove(int id) {
        if (history.get(id) == null) {
            return;
        }

        removeNode(history.get(id));
        history.remove(id);
    }

    private void linkLast(Task task) {
        if (first == null) {
            first = new Node(null, task, null);
            last = first;
        } else {
            Node previous = last;
            last = new Node(previous, task, null);
            previous.setNext(last);
        }
    }

    private void removeNode(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev == null && next == null) {
            first = null;
            last = null;
            return;
        } else if (prev == null) {
            next.setPrev(null);
            first = next;
            return;
        } else if (next == null) {
            prev.setNext(null);
            last = prev;
            return;
        }

        prev.setNext(next);
        next.setPrev(prev);
    }
}
