package org.webcurator.visualization.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.util.PatchUtil;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import java.io.File;
import java.util.*;


public class WavaDirectoryManagement extends VisualizationDirectoryManager {
    private static final Logger log = LoggerFactory.getLogger(WavaDirectoryManagement.class);
    private final Map<Long, WavaFolderNode> mapWarcFolders = new HashMap<>();
    private WavaFolderNode baseWarcFolderNode;
    private long tiId = 1;

    public WavaDirectoryManagement(String baseDir, String baseLogDir, String baseReportDir) {
        super(baseDir, baseLogDir, baseReportDir);
        this.baseWarcFolderNode = treeHarvestResults(new File(this.getBaseDir()));
        if (this.baseWarcFolderNode == null) {
            this.baseWarcFolderNode = new WavaFolderNode();
            this.baseWarcFolderNode.setChildren(new ArrayList<>());
        }
        this.baseWarcFolderNode.setTitle(this.getBaseDir());
    }

    public String getSubHarvestResultFolder(long tiOid, int hrNum) {
        WavaFolderNode node = mapWarcFolders.get(tiOid);
        if (node == null) {
            log.error("node is null: {} {}", tiOid, hrNum);
            return tiOid + File.separator + hrNum;
        }
        String fullPath = node.getAbsolutePath();
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
        WavaFolderNode node = mapWarcFolders.get(job);
        if (node == null) {
            System.out.println("node is null");
            return job + File.separator + harvestResultNumber;
        }
        return node.getAbsolutePath();
    }

    public String getDbName(long job, int harvestResultNumber) {
        WavaFolderNode node = mapWarcFolders.get(job);
        if (node == null) {
            return job + "-" + harvestResultNumber;
        }
        return node.getAbsolutePath();
    }

    public WavaFolderNode treeHarvestResults() {
        return this.baseWarcFolderNode;
    }

    public WavaFolderNode treeHarvestResults(File rootPath) {
        if (rootPath.isFile() && !this.isWarcFile(rootPath.getName())) {
            return null;
        }

        this.tiId++;

        WavaFolderNode node = new WavaFolderNode();
        node.setKey(rootPath.getAbsolutePath());
        node.setTiId(this.tiId);
        node.setTitle(rootPath.getName());
        node.setAbsolutePath(rootPath.getAbsolutePath());
        node.setHrNum(1);
        mapWarcFolders.put(this.tiId, node);

        if (rootPath.isFile()) {
            node.setChildren(null);
            node.setSelectable(false);
            node.setFolder(false);
            node.setSize(rootPath.length());
            return node;
        } else if (rootPath.isDirectory()) {
            List<WavaFolderNode> childrenNodes = new ArrayList<>();
            File[] children = rootPath.listFiles();
            if (children == null) {
                return node;
            }

            for (File f : children) {
                WavaFolderNode childNode = this.treeHarvestResults(f);
                if (childNode != null) {
                    childrenNodes.add(childNode);
                }
            }

            if (!childrenNodes.isEmpty()) {
                node.setChildren(childrenNodes);
            }

            List<File> warcFiles = PatchUtil.listWarcFiles(rootPath);
            node.setSelectable(warcFiles.isEmpty());
            return node;
        }
        return null;
    }

    private boolean isWarcFile(String name) {
        return name.toLowerCase().endsWith(".warc") || name.toLowerCase().endsWith(".warc.gz");
    }
}