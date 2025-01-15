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
        WavaDirectoryManagement.FolderNode folders = testInstance.treeHarvestResults();
        System.out.println(folders.getTitle());
        WavaDirectoryManagement.FolderNode leaf = getOneLeafNode(folders);
        assert leaf != null;
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

        WavaDirectoryManagement.FolderNode folders = testInstance.treeHarvestResults();
        System.out.println(folders.getTitle());

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

    private WavaDirectoryManagement.FolderNode getOneLeafNode(WavaDirectoryManagement.FolderNode node) {
        if (!node.isFolder()) {
            return node;
        }

        for (WavaDirectoryManagement.FolderNode children : node.getChildren()) {
            return getOneLeafNode(children);
        }
        return null;
    }

    private void walkNodes(WavaDirectoryManagement testInstance, WavaDirectoryManagement.FolderNode node) {
        if (!node.isFolder()) {
            File hrPath = new File(testInstance.getAbsoluteSubHarvestResultFolder(node.getTiId(), node.getHrNum()));
            assert hrPath != null;
            assert hrPath.getAbsolutePath().equalsIgnoreCase(node.getAbsolutePath());
            System.out.println("BaseDir:" + testInstance.getBaseDir() + ", HarvestResult Path:" + hrPath.getAbsolutePath());
            return;
        }

        for (WavaDirectoryManagement.FolderNode children : node.getChildren()) {
            walkNodes(testInstance, children);
        }
    }
}
