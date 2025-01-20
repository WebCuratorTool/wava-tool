package org.webcurator.visualization.app;

import java.util.List;

public class WavaTreeNode {
    private String key;
    private WavaTreeDataNode data;
    private String type;
    private List<WavaTreeNode> children;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public WavaTreeDataNode getData() {
        return data;
    }

    public void setData(WavaTreeDataNode data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WavaTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<WavaTreeNode> children) {
        this.children = children;
    }
}