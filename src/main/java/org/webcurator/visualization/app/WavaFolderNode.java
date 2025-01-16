package org.webcurator.visualization.app;

import java.util.List;

public class WavaFolderNode {
    private String key;
    private String title;
    private String absolutePath;
    private long tiId;
    private int hrNum;
    private boolean folder = true;
    private boolean selectable = true;
    private long size = 0;
    private List<WavaFolderNode> children;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public long getTiId() {
        return tiId;
    }

    public void setTiId(long tiId) {
        this.tiId = tiId;
    }

    public int getHrNum() {
        return hrNum;
    }

    public void setHrNum(int hrNum) {
        this.hrNum = hrNum;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<WavaFolderNode> getChildren() {
        return children;
    }

    public void setChildren(List<WavaFolderNode> children) {
        this.children = children;
    }
}
