package org.webcurator.visualization.springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.webcurator.core.harvester.coordinator.HarvestAgentListenerService;
import org.webcurator.core.coordinator.WctCoordinator;

import java.util.Arrays;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"org.webcurator.visualization.app",
        "org.webcurator.visualization.springboot",
        "org.webcurator.core.visualization",
        "org.webcurator.ui.tools.controller"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {HarvestAgentListenerService.class, WctCoordinator.class})
)
public class MainApplication {
    @Value("${arcDigitalAssetStoreService.baseDir}")
    private String arcDigitalAssetStoreServiceBaseDir;

    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        try {
            SpringApplication.run(MainApplication.class, args);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // Note that this is just here for debugging purposes. It can be deleted at any time.
            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

            System.out.println("==========================WAVA Tool Launched==================================");
            System.out.println("The web harvest folder: " + arcDigitalAssetStoreServiceBaseDir);
            System.out.println("The server port: " + serverPort);
            System.out.println("==============================================================================");
            System.out.println();
        };
    }
}
