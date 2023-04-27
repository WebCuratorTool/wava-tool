package org.webcurator.visualization.app;

import org.junit.Test;

import java.io.File;

public class TestWavaDirectoryManagement {
    protected String baseDir = "/usr/local/wct/store";
    protected String baseLogDir = "logs";
    protected String baseReportDir = "reports";

    @Test
    public void testTreeHarvestResults() {
        WavaDirectoryManagement testInstance = new WavaDirectoryManagement(baseDir, baseLogDir, baseReportDir);
        WavaDirectoryManagement.FolderNode folders = testInstance.treeHarvestResults(new File(baseDir));
        System.out.println(folders.getTitle());
    }
}
