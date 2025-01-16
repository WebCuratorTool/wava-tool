package org.webcurator.visualization.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.util.PatchUtil;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import java.io.File;
import java.util.*;


public class WavaDirectoryManagement extends VisualizationDirectoryManager {
    private static final Logger log = LoggerFactory.getLogger(WavaDirectoryManagement.class);
    private final Map<Long, WavaTreeNode> mapWarcFolders = new HashMap<>();
    private WavaTreeNode baseWarcFolderNode;
    private long tiId = 1;

    public WavaDirectoryManagement(String baseDir, String baseLogDir, String baseReportDir) {
        super(baseDir, baseLogDir, baseReportDir);
        this.baseWarcFolderNode = treeHarvestResults(new File(this.getBaseDir()));
        if (this.baseWarcFolderNode == null) {
            this.baseWarcFolderNode = new WavaTreeNode();
            this.baseWarcFolderNode.setChildren(new ArrayList<>());
        }
        this.baseWarcFolderNode.getData().setLabel(this.getBaseDir());
    }

    public String getSubHarvestResultFolder(long tiOid, int hrNum) {
        WavaTreeNode node = mapWarcFolders.get(tiOid);
        if (node == null) {
            log.error("node is null: {} {}", tiOid, hrNum);
            return tiOid + File.separator + hrNum;
        }
        String fullPath = node.getData().getAbsolutePath();
        log.info("fullPath: {}, baseDir: {}", fullPath, this.getBaseDir());
        if (fullPath.length() > this.getBaseDir().length()) {
            log.info("Returning substring: {}", fullPath.substring(this.getBaseDir().length()));
            return fullPath.substring(this.getBaseDir().length());
        } else {
            log.error("Returning empty substring");
            return "";
        }
    }

    public String getAbsoluteSubHarvestResultFolder(long job, int harvestResultNumber) {
        WavaTreeNode node = mapWarcFolders.get(job);
        if (node == null) {
            System.out.println("node is null");
            return job + File.separator + harvestResultNumber;
        }
        return node.getData().getAbsolutePath();
    }

    public String getDbName(long job, int harvestResultNumber) {
        WavaTreeNode node = mapWarcFolders.get(job);
        if (node == null) {
            return job + "-" + harvestResultNumber;
        }
        return node.getData().getAbsolutePath();
    }

    public WavaTreeNode treeHarvestResults() {
        return this.baseWarcFolderNode;
    }

    public WavaTreeNode treeHarvestResults(File rootPath) {
        if (rootPath.isFile() && !this.isWarcFile(rootPath.getName())) {
            return null;
        }

        this.tiId++;

        WavaTreeNode node = new WavaTreeNode();
        node.setKey(rootPath.getAbsolutePath());

        WavaTreeDataNode data = new WavaTreeDataNode();
        node.setData(data);

        data.setTiId(this.tiId);
        data.setLabel(rootPath.getName());
        data.setAbsolutePath(rootPath.getAbsolutePath());
        data.setHrNum(1);
        mapWarcFolders.put(this.tiId, node);

        if (rootPath.isFile()) {
            node.setChildren(null);
            data.setFolder(false);
            data.setSize(rootPath.length());
            return node;
        } else if (rootPath.isDirectory()) {
            if (rootPath.getName().equalsIgnoreCase("_resource") || rootPath.getName().equalsIgnoreCase("logs") || rootPath.getName().equalsIgnoreCase("reports")) {
                return null;
            }
            List<WavaTreeNode> childrenNodes = new ArrayList<>();
            File[] children = rootPath.listFiles();
            if (children == null) {
                return node;
            }

            for (File f : children) {
                WavaTreeNode childNode = this.treeHarvestResults(f);
                if (childNode != null) {
                    childrenNodes.add(childNode);
                }
            }

            if (!childrenNodes.isEmpty()) {
                node.setChildren(childrenNodes);
            }

            List<File> warcFiles = PatchUtil.listWarcFiles(rootPath);
            if (!warcFiles.isEmpty()) {
                node.setType("url");
            }

            return node;
        }
        return null;
    }

    private boolean isWarcFile(String name) {
        return name.toLowerCase().endsWith(".warc") || name.toLowerCase().endsWith(".warc.gz");
    }
}