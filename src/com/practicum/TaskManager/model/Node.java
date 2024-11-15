package com.practicum.TaskManager.model;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node(Node prev, Task task, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    public Task getTask() {
        return task;
    }
}
