package org.webcurator.visualization.app;

import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.webcurator.core.exceptions.DigitalAssetStoreException;
import org.webcurator.core.util.PatchUtil;
import org.webcurator.core.visualization.VisualizationProgressBar;
import org.webcurator.core.visualization.networkmap.bdb.BDBNetworkMapPool;
import org.webcurator.core.visualization.networkmap.metadata.NetworkMapNodeUrlDTO;
import org.webcurator.core.visualization.networkmap.processor.IndexProcessorWarc;
import org.webcurator.core.visualization.networkmap.service.NetworkMapService;
import org.webcurator.domain.model.core.HarvestResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class WavaIndexProcessorWarc extends IndexProcessorWarc {
    private final WavaDirectoryManagement dirMgmt;

    public WavaIndexProcessorWarc(BDBNetworkMapPool pool, long tiOID, int hrNum, WavaDirectoryManagement dirMgmt, NetworkMapService networkMapClient) throws DigitalAssetStoreException, IOException {
        super(pool, tiOID, hrNum);
        this.dirMgmt = dirMgmt;
        this.progressBar = new VisualizationProgressBar(getProcessorStage(), tiOID, hrNum);
        this.baseDir = dirMgmt.getAbsoluteSubHarvestResultFolder(tiOID, hrNum);
        this.fileDir = dirMgmt.getUploadDir(tiOID);
        this.logsDir = dirMgmt.getBaseLogDir(tiOID);
        this.reportsDir = dirMgmt.getBaseReportDir(tiOID);
        this.networkMapClient = networkMapClient;

        File fLogsDir = new File(logsDir);
        if (!fLogsDir.exists() && !fLogsDir.mkdirs()) {
            String err = String.format("Make directory failed: %s", fLogsDir.getAbsolutePath());
            log.error(err);
            throw new IOException(err);
        }
        try {
            this.logWriter = new FileWriter(new File(logsDir, dirMgmt.getPatchLogFileName(getProcessorStage(), hrNum)), false);
        } catch (IOException e) {
            log.error("Failed to create log writer", e);
            throw e;
        }

        File fReportsDir = new File(reportsDir);
        if (!fReportsDir.exists() && !fReportsDir.mkdirs()) {
            String err = String.format("Make directory failed: %s", fReportsDir.getAbsolutePath());
            log.error(err);
            throw new IOException(err);
        }
        this.reportWriter = new FileWriter(new File(reportsDir, dirMgmt.getPatchReportFileName(getProcessorStage(), hrNum)), false);

        this.statisticItems.clear();
    }

    @Override
    protected void postProcess() {
        //Do nothing
    }

    public boolean process() {
        try {
            this.status = HarvestResult.STATUS_RUNNING;
            processInternal();
            this.close();
            return true;
        } catch (Exception e) {
            log.error("Failed to process", e);
            return false;
        } finally {
            this.progressBar.clear();
            if (this.status == HarvestResult.STATUS_RUNNING) {
                this.status = HarvestResult.STATUS_FINISHED;
            }
        }
    }

    public boolean isNotWarcFormat(String name) {
        return !name.toLowerCase().endsWith(org.archive.format.warc.WARCConstants.DOT_WARC_FILE_EXTENSION) &&
                !name.toLowerCase().endsWith(org.archive.format.warc.WARCConstants.DOT_COMPRESSED_WARC_FILE_EXTENSION);
    }

    @Override
    public void processInternal() throws Exception {
        try {
            File directory = new File(this.dirMgmt.getAbsoluteSubHarvestResultFolder(this.targetInstanceId, this.harvestResultNumber));
            List<File> fileList = PatchUtil.listWarcFiles(directory);
            if (fileList.isEmpty()) {
                log.error("Could not find any archive files in directory: {}", directory.getAbsolutePath());
                return;
            }
            fileList.sort(Comparator.comparing(File::getName));

            VisualizationProgressBar.ProgressItem progressItemStat = progressBar.getProgressItem("STAT");
            for (File f : fileList) {
                if (this.status == HarvestResult.STATUS_TERMINATED) {
                    log.info("Terminated when scanning, {}", f);
                    break;
                }

                if (isNotWarcFormat(f.getName())) {
                    continue;
                }
                this.fileWarcNames.add(f.getName());

                VisualizationProgressBar.ProgressItem progressItem = progressBar.getProgressItem(f.getName());
                progressItem.setMaxLength(f.length());
                progressItemStat.setMaxLength(progressItemStat.getMaxLength() + f.length());
            }

            log.debug(progressBar.toString());
            for (File f : fileList) {
                if (this.status == HarvestResult.STATUS_TERMINATED) {
                    log.info("Terminated when indexing: {}", f);
                    break;
                }

                if (isNotWarcFormat(f.getName())) {
                    this.writeLog("Skipped unknown file: " + f.getName());
                    continue;
                }
                try (ArchiveReader reader = ArchiveReaderFactory.get(f)) {
                    indexFile(reader, f.getName());
                } catch (Exception e) {
                    String err = "Failed to extract archive file: " + f.getAbsolutePath() + " with exception: " + e.getMessage();
                    log.error(err, e);
                    this.writeLog(err);
                }

                VisualizationProgressBar.ProgressItem progressItem = progressBar.getProgressItem(f.getName());
                progressItem.setCurLength(progressItem.getMaxLength()); //Set all finished
            }

            this.statAndSave();

            this.writeReport();
            progressItemStat.setCurLength(progressItemStat.getMaxLength());//Set all finished

            this.status = HarvestResult.STATUS_FINISHED;
        } finally {
            this.urls.values().forEach(NetworkMapNodeUrlDTO::clear);
            this.urls.clear();
            this.pool.shutdownRepo(db);
        }
    }
}
