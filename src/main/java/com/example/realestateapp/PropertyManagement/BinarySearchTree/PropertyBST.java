package com.example.realestateapp.PropertyManagement.BinarySearchTree;
import com.example.realestateapp.PropertyManagement.model.Property;

public class PropertyBST {
    private BSTNode root;

    public void insert(Property property) {
        root = insertRecursive(root, property);
    }

    private BSTNode insertRecursive(BSTNode node, Property property) {
        if (node == null) return new BSTNode(property);

        if (property.getLocation().compareToIgnoreCase(node.property.getLocation()) < 0) {
            node.left = insertRecursive(node.left, property);
        } else {
            node.right = insertRecursive(node.right, property);
        }

        return node;
    }

    public MyPropertyList inOrderTraversal() {
        MyPropertyList result = new MyPropertyList();
        inOrderRecursive(root, result);
        return result;
    }

    private void inOrderRecursive(BSTNode node, MyPropertyList list) {
        if (node != null) {
            inOrderRecursive(node.left, list);
            list.add(node.property);
            inOrderRecursive(node.right, list);
        }
    }

    public MyPropertyList searchByLocation(String targetLocation) {
        MyPropertyList result = new MyPropertyList();
        searchRecursive(root, targetLocation.toLowerCase(), result);
        return result;
    }

    private void searchRecursive(BSTNode node, String target, MyPropertyList result) {
        if (node == null) return;

        String nodeLocation = node.property.getLocation().toLowerCase();
        if (nodeLocation.equals(target)) {
            result.add(node.property);
        }

        searchRecursive(node.left, target, result);
        searchRecursive(node.right, target, result);
    }
}