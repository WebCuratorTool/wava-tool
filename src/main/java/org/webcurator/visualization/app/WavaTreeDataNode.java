package org.webcurator.visualization.app;

public class WavaTreeDataNode {
    private String label;
    private String absolutePath;
    private long tiId;
    private int hrNum;
    private boolean folder = true;
    private long size = 0;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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



    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
