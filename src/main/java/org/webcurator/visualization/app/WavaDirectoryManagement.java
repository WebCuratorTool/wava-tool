package org.webcurator.visualization.app;

import org.webcurator.core.util.PatchUtil;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import java.io.File;
import java.util.*;


public class WavaDirectoryManagement extends VisualizationDirectoryManager {
    private Map<Long, FolderNode> mapWarcFolders = new HashMap<>();
    private FolderNode baseWarcFolderNode;
    private long tiId = 1;

    public WavaDirectoryManagement(String baseDir, String baseLogDir, String baseReportDir) {
        super(baseDir, baseLogDir, baseReportDir);
        this.baseWarcFolderNode = treeHarvestResults(new File(this.getBaseDir()));
        if (this.baseWarcFolderNode == null) {
            this.baseWarcFolderNode = new FolderNode();
            this.baseWarcFolderNode.setChildren(new ArrayList<>());
        }
        this.baseWarcFolderNode.setTitle(this.getBaseDir());
    }

    @Override
    public String getSubHarvestResultFolder(long job, int harvestResultNumber) {
        FolderNode node = mapWarcFolders.get(job);
        if (node == null) {
            return job + File.separator + harvestResultNumber;
        }
        String fullPath = node.getAbsolutePath();
        return fullPath.substring(this.getBaseDir().length());
    }

    @Override
    public String getDbName(long job, int harvestResultNumber) {
        FolderNode node = mapWarcFolders.get(job);
        if (node == null) {
            return job + "-" + harvestResultNumber;
        }
        return node.getAbsolutePath();
    }

    public FolderNode treeHarvestResults() {
        return this.baseWarcFolderNode;
    }

    public FolderNode treeHarvestResults(File rootPath) {
        if (rootPath == null || !rootPath.isDirectory()) {
            return null;
        }
        List<File> warcFiles = PatchUtil.listWarcFiles(rootPath);
        if (warcFiles.size() > 0) {
            FolderNode node = new FolderNode();
            node.setTitle("Harvest Result: " + rootPath.getName());
            node.setFolder(false);
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
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f0, File f1) {
                return f0.getName().compareTo(f1.getName());
            }
        });
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
            node.setTitle(rootPath.getName());
            node.setFolder(true);
            node.setChildren(children);
            return node;
        }
        return null;
    }


    static class FolderNode {
        private String title;
        private String absolutePath;
        private long tiId;
        private int hrNum;
        private boolean folder = true;
        private List<FolderNode> children;

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


        public List<FolderNode> getChildren() {
            return children;
        }

        public void setChildren(List<FolderNode> children) {
            this.children = children;
        }
    }
}