package io.github.gasparbarancelli;

import java.util.concurrent.LinkedBlockingQueue;

public class CircularQueue<E> extends LinkedBlockingQueue<E> {
    private final int capacity;

    public CircularQueue(int capacity){
        this.capacity = capacity;
    }

    @Override
    public boolean add(E element) {
        if (size() >= capacity) {
            super.remove();
        }
        return super.add(element);
    }

}