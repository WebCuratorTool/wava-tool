package org.webcurator.visualization.app;
import org.webcurator.core.visualization.VisualizationDirectoryManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class VisServiceController {
    // The base directory for the arc store
    @Value("${arcDigitalAssetStoreService.baseDir}")
    private String arcDigitalAssetStoreServiceBaseDir;

    @Value("${qualityReviewToolController.archiveUrl}")
    private String openWayBack;

    @Autowired
    private VisualizationDirectoryManager dirMgmt;

    final private AtomicLong hrOid = new AtomicLong(0);

    @RequestMapping(path = "/curator/vis/all_hr_results", method = {RequestMethod.POST, RequestMethod.GET})
    public WavaDirectoryManagement.FolderNode getAllHarvestResults() {
        return ((WavaDirectoryManagement)dirMgmt).treeHarvestResults();
    }
}
