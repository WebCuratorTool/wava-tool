package org.webcurator.visualization.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class WavaDirectoryManagement extends VisualizationDirectoryManager {
    private static final Logger log = LoggerFactory.getLogger(WavaDirectoryManagement.class);
    private final Map<Long, WavaTreeNode> mapWarcFolders = new HashMap<>();
    private final IdGenerator tiIdGenerator;
    private WavaTreeNode baseWarcFolderNode;


    public WavaDirectoryManagement(String baseDir, String baseLogDir, String baseReportDir) {
        super(baseDir, baseLogDir, baseReportDir);
        this.tiIdGenerator = IdGenerator.getInstance(baseDir);
        this.baseWarcFolderNode = treeHarvestResults();
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

    synchronized public WavaTreeNode treeHarvestResults() {
        this.baseWarcFolderNode = treeHarvestResults(new File(this.getBaseDir()));
        if (this.baseWarcFolderNode == null) {
            this.baseWarcFolderNode = new WavaTreeNode();
            this.baseWarcFolderNode.setChildren(new ArrayList<>());
        }
        this.baseWarcFolderNode.getData().setLabel(this.getBaseDir());

        return this.baseWarcFolderNode;
    }

    public WavaTreeNode treeHarvestResults(File rootPath) {
        if (rootPath.isFile() && !this.isWarcFile(rootPath.getName())) {
            return null;
        }

        long tiId = this.tiIdGenerator.nextId(rootPath.getAbsolutePath());
        if (tiId <= 0) {
            log.error("Failed to generate tiId: {}", rootPath);
            return null;
        }

        WavaTreeNode node = new WavaTreeNode();
        node.setKey(rootPath.getAbsolutePath());

        WavaTreeDataNode data = new WavaTreeDataNode();
        node.setData(data);

        data.setTiId(tiId);
        data.setLabel(rootPath.getName());
        data.setAbsolutePath(rootPath.getAbsolutePath());
        data.setHrNum(1);
        mapWarcFolders.put(tiId, node);

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

            boolean hasWarcFiles = false;
            for (File f : children) {
                if (this.isWarcFile(f.getName())) {
                    hasWarcFiles = true;
                }
                WavaTreeNode childNode = this.treeHarvestResults(f);
                if (childNode != null) {
                    childrenNodes.add(childNode);
                }
            }

            if (childrenNodes.isEmpty()) {
                return null;
            }

            node.setChildren(childrenNodes);
//            List<File> warcFiles = PatchUtil.listWarcFiles(rootPath);
            if (hasWarcFiles) {
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

class IdGenerator {
    private static final Logger log = LoggerFactory.getLogger(IdGenerator.class);

    @JsonIgnore
    private File idGeneratorFile;
    private long curId = 0;
    private Map<String, Long> mapPath2ID = new HashMap<>();

    @JsonIgnore
    public static IdGenerator getInstance(String baseDir) {
        File idGeneratorFile = new File(baseDir, "id-generator.json");
        IdGenerator idGenerator;
        if (!idGeneratorFile.exists()) {
            idGenerator = new IdGenerator();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                idGenerator = mapper.readValue(idGeneratorFile, IdGenerator.class);
                log.info("Succeed to load json file: {}, curId={}", idGeneratorFile, idGenerator.curId);
            } catch (IOException e) {
                log.error("Failed to load json file: {}", baseDir, e);
                return null;
            }
        }
        idGenerator.idGeneratorFile = idGeneratorFile;
        return idGenerator;
    }

    @JsonIgnore
    synchronized public long nextId(String filePath) {
        if (mapPath2ID.containsKey(filePath)) {
            return mapPath2ID.get(filePath);
        } else {
            this.curId++;
            mapPath2ID.put(filePath, this.curId);

            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(this.idGeneratorFile, this);
            } catch (IOException e) {
                log.error("Failed to write json to file: {}", this.idGeneratorFile);
                return -1;
            }

            return this.curId;
        }
    }

    public long getCurId() {
        return curId;
    }

    public void setCurId(long curId) {
        this.curId = curId;
    }

    public Map<String, Long> getMapPath2ID() {
        return mapPath2ID;
    }

    public void setMapPath2ID(Map<String, Long> mapPath2ID) {
        this.mapPath2ID = mapPath2ID;
    }
}