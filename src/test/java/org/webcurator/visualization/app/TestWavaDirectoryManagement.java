package org.webcurator.visualization.app;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class TestWavaDirectoryManagement {

    protected String baseLogDir = "logs";
    protected String baseReportDir = "reports";

    @Test
    public void testTreeHarvestResults() {
        String baseDir = "/usr/local/wct/store";
        WavaDirectoryManagement testInstance = new WavaDirectoryManagement(baseDir, baseLogDir, baseReportDir);
        WavaTreeNode folders = testInstance.treeHarvestResults();
        System.out.println(folders.getData().getLabel());
        WavaTreeNode leaf = getOneLeafNode(folders);
       assert leaf == null;
    }


    @Test
    public void testGetHarvestResultPath() throws IOException {
        createSubFolder("/tmp", "SciBlogs");
        createSubFolder("/tmp", "NewsNZ");
        createSubFolder("/tmp/harvest_results", "SciBlogs");
        createSubFolder("/tmp/harvest_results", "NewsNZ");

        String[] baseDirs = {"/tmp", "/tmp/", "/tmp/NewsNZ", "/tmp/NewsNZ/", "/tmp/harvest_results/SciBlogs", "/tmp/harvest_results/SciBlogs/"};
        for (String baseDir : baseDirs) {
            testGetHarvestResultPath(baseDir);
        }
    }

    private void testGetHarvestResultPath(String baseDir) {
        WavaDirectoryManagement testInstance = new WavaDirectoryManagement(baseDir, baseLogDir, baseReportDir);

        WavaTreeNode folders = testInstance.treeHarvestResults();
        System.out.println(folders.getData().getLabel());

        walkNodes(testInstance, folders);
    }

    private void createSubFolder(String baseDir, String subFolderName) throws IOException {
        File subFolder = new File(baseDir, subFolderName);
        if (subFolder.exists()) {
            FileUtils.deleteDirectory(subFolder);
        }
        assertTrue(subFolder.mkdirs());
        File warcFile = new File(subFolder, "a.warc");
        Files.createFile(warcFile.toPath());
    }

    private WavaTreeNode getOneLeafNode(WavaTreeNode node) {
        if (!node.getData().isFolder()) {
            return node;
        }

        for (WavaTreeNode children : node.getChildren()) {
            return getOneLeafNode(children);
        }
        return null;
    }

    private void walkNodes(WavaDirectoryManagement testInstance, WavaTreeNode node) {
        if (node == null || node.getChildren() == null) {
            return;
        }

        if (!node.getData().isFolder()) {
            File hrPath = new File(testInstance.getAbsoluteSubHarvestResultFolder(node.getData().getTiId(), node.getData().getHrNum()));
            assert hrPath != null;
            assert hrPath.getAbsolutePath().equalsIgnoreCase(node.getData().getAbsolutePath());
            System.out.println("BaseDir:" + testInstance.getBaseDir() + ", HarvestResult Path:" + hrPath.getAbsolutePath());
            return;
        }

        for (WavaTreeNode children : node.getChildren()) {
            walkNodes(testInstance, children);
        }
    }
}
