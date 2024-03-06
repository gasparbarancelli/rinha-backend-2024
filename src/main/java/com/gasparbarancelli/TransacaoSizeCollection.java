package com.gasparbarancelli;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class TransacaoSizeCollection {
    private static final int MAX_SIZE = 11;
    private LinkedList<String[]> list = new LinkedList<>();

    public void addItem(String[] item) {
        if (list.size() >= MAX_SIZE) {
            list.remove(1);
        }
        list.add(item);
    }

    public void addAll(List<String[]> data) {
        list.addAll(data);
    }

    public LinkedList<String[]> getList() {
        return list;
    }

    public String[] getFirst() {
        return list.get(1);
    }

    public Stream<String[]> stream() {
        return list.stream();
    }

    public void changeFirst(String[] item) {
        try {
            list.set(1, item);
        } catch (IndexOutOfBoundsException e) {
            list.add(item);
        }
    }

}