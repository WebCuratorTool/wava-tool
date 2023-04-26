package org.webcurator.visualization.app;

import org.webcurator.core.util.PatchUtil;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class WavaDirectoryManagement extends VisualizationDirectoryManager {
    private Map<Long, FolderNode> mapWarcFolders = new HashMap<>();
    private FolderNode baseWarcFolderNode;
    private long tiId = 1;

    public WavaDirectoryManagement(String baseDir, String baseLogDir, String baseReportDir) {
        super(baseDir, baseLogDir, baseReportDir);
        this.baseWarcFolderNode = treeHarvestResults(new File(baseDir));
        if (this.baseWarcFolderNode == null) {
            this.baseWarcFolderNode = new FolderNode();
            this.baseWarcFolderNode.setChildren(new ArrayList<>());
        }
        this.baseWarcFolderNode.setName(baseDir);
    }

    public FolderNode treeHarvestResults(File rootPath) {
        if (rootPath == null || !rootPath.isDirectory()) {
            return null;
        }
        List<File> warcFiles = PatchUtil.listWarcFiles(rootPath);
        if (warcFiles.size() > 0) {
            FolderNode node = new FolderNode();
            node.setName(rootPath.getName());
            node.setLeafNode(true);
            node.setAbsolutePath(rootPath.getAbsolutePath());
            long tiId = this.tiId++;
            node.setTiId(tiId);
            node.setHrNum(1);
            mapWarcFolders.put(tiId, node);
            return node;
        }

        File[] files = rootPath.listFiles();
        if (files == null) {
            return null;
        }
        List<FolderNode> children = new ArrayList<>();
        for (File f : files) {
            if (!f.isDirectory()) {
                continue;
            }
            FolderNode node = treeHarvestResults(f);
            if (node != null) {
                children.add(node);
            }
        }
        if (!children.isEmpty()) {
            FolderNode node = new FolderNode();
            node.setName(rootPath.getName());
            node.setLeafNode(false);
            node.setChildren(children);
            return node;
        }
        return null;
    }

}

class FolderNode {
    private String name;
    private String absolutePath;
    private long tiId;
    private int hrNum;
    private boolean isLeafNode;
    private List<FolderNode> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isLeafNode() {
        return isLeafNode;
    }

    public void setLeafNode(boolean leafNode) {
        isLeafNode = leafNode;
    }

    public List<FolderNode> getChildren() {
        return children;
    }

    public void setChildren(List<FolderNode> children) {
        this.children = children;
    }
}