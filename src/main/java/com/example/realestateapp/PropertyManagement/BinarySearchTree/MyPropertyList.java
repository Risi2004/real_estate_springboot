package com.example.realestateapp.PropertyManagement.BinarySearchTree;

import com.example.realestateapp.PropertyManagement.model.Property;

public class MyPropertyList {
    private Property[] data;
    private int size;

    public MyPropertyList() {
        data = new Property[10];
        size = 0;
    }

    public void add(Property p) {
        if (size == data.length) resize();
        data[size++] = p;
    }

    private void resize() {
        Property[] newData = new Property[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    public Property get(int index) {
        if (index >= 0 && index < size) return data[index];
        return null;
    }

    public void set(int index, Property p) {
        if (index >= 0 && index < size) {
            data[index] = p;
        }
    }

    public int size() {
        return size;
    }

    public Property[] toArray() {
        Property[] output = new Property[size];
        for (int i = 0; i < size; i++) {
            output[i] = data[i];
        }
        return output;
    }

    // New: Create a copy of this list
    public MyPropertyList copy() {
        MyPropertyList copy = new MyPropertyList();
        for (int i = 0; i < this.size(); i++) {
            copy.add(this.get(i));
        }
        return copy;
    }
}
