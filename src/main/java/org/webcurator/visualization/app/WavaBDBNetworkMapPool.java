package org.webcurator.visualization.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webcurator.core.util.WctUtils;
import org.webcurator.core.visualization.networkmap.bdb.BDBNetworkMapPool;
import org.webcurator.core.visualization.networkmap.bdb.BDBRepoHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class WavaBDBNetworkMapPool extends BDBNetworkMapPool {
    private final static int MAX_SIZE = 10;
    private final static Logger log = LoggerFactory.getLogger(WavaBDBNetworkMapPool.class);
    private final List<BDBRepoHolder> queue = new ArrayList<>();
    private final Map<String, BDBRepoHolder> map = new Hashtable<>();
    private final WavaDirectoryManagement dirMgmt;


    public WavaBDBNetworkMapPool(String dbRootPath, String dbVersion, WavaDirectoryManagement dirMgmt) {
        super(dbRootPath, dbVersion);
        this.dirMgmt = dirMgmt;
    }

    //Create and open a DB
    synchronized public BDBRepoHolder createInstance(long job, int harvestResultNumber) {
        String dbLabel = this.getDbLabel(job, harvestResultNumber);
        if (map.containsKey(dbLabel)) {
            BDBRepoHolder oldDb = map.get(dbLabel);
            oldDb.shutdownDB();
            queue.remove(oldDb);
            map.remove(dbLabel);
        }

        if (queue.size() >= MAX_SIZE) {
            BDBRepoHolder oldDb = queue.get(0);
            queue.remove(0);
            map.remove(oldDb.getDbName());
            oldDb.shutdownDB();
        }

        //Clear the path
        File dbPath = this.getWavaDbPath(job, harvestResultNumber);
        if (dbPath.exists()) {
            WctUtils.cleanDirectory(dbPath);
        } else {
            log.warn("Recover db files: {}", dbPath);
        }

        BDBRepoHolder db;
        try {
            db = BDBRepoHolder.createInstance(dbPath.getAbsolutePath(), this.getDbName());
            queue.add(db);
            map.put(dbLabel, db);
        } catch (IOException e) {
            log.error("Failed to open db: {}-->{}", dbPath, dbLabel, e);
            return null;
        }

        return db;
    }

    //Open with read mode
    synchronized public BDBRepoHolder getInstance(long job, int harvestResultNumber) {
        File dbPath = this.getWavaDbPath(job, harvestResultNumber);
        if (!dbPath.exists()) {  //
            log.warn("Could not find Index DB: {}", dbPath);
            return null;
        }
        String dbLabel = this.getDbLabel(job, harvestResultNumber);
        if (map.containsKey(dbLabel)) {
            return map.get(dbLabel);
        }

        if (queue.size() >= MAX_SIZE) {
            BDBRepoHolder oldDb = queue.get(0);
            queue.remove(0);
            map.remove(oldDb.getDbName());
            oldDb.shutdownDB();
        }

        BDBRepoHolder db;
        try {
            db = BDBRepoHolder.getInstance(dbPath.getAbsolutePath(), this.getDbName());
            queue.add(db);
            map.put(dbLabel, db);
        } catch (IOException e) {
            log.error("Failed to open db: {}-->{}", dbPath, dbLabel, e);
            return null;
        }

        return db;
    }

    synchronized public void shutdownRepo(BDBRepoHolder db) {
        db.shutdownDB();
        String dbPath = db.getDbPath();
        BDBRepoHolder oldDb = null;
        for (BDBRepoHolder e : queue) {
            if (e.getDbPath().equals(dbPath)) {
                oldDb = e;
                break;
            }
        }
        if (oldDb != null) {
            queue.remove(oldDb);
        }
        map.remove(dbPath);
    }

    public void close(long job, int harvestResultNumber) {
        String dbLabel = getDbLabel(job, harvestResultNumber);
        if (map.containsKey(dbLabel)) {
            BDBRepoHolder oldDb = map.get(dbLabel);
            oldDb.shutdownDB();
            queue.remove(oldDb);
            map.remove(dbLabel);
        }
    }


    public String getDbName() {
        return "wava";
    }

    public File getWavaDbPath(long job, int harvestResultNumber) {
        String warcDirectory = this.dirMgmt.getAbsoluteSubHarvestResultFolder(job, harvestResultNumber);
        return new File(warcDirectory, "_resource");
    }


    public String getDbLabel(long job, int harvestResultNumber) {
        File dbPath = getWavaDbPath(job, harvestResultNumber);
        return dbPath.getAbsolutePath();
    }
}

