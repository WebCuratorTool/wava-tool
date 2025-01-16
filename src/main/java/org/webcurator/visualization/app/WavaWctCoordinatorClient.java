package org.webcurator.visualization.app;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.webcurator.core.coordinator.WctCoordinatorClient;

public class WavaWctCoordinatorClient extends WctCoordinatorClient {

    public WavaWctCoordinatorClient(String baseUrl, RestTemplateBuilder restTemplateBuilder) {
        super(baseUrl, restTemplateBuilder);
    }

    public void notifyModificationComplete(long targetInstanceId, int harvestResultNumber) {
        //        Do nothing
    }

    public void finaliseIndex(long targetInstanceId, int harvestNumber) {
        //        Do nothing
    }
}