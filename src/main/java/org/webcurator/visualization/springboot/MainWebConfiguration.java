package org.webcurator.visualization.springboot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MainWebConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/home.html");
        registry.addViewController("/index.html").setViewName("forward:/home.html");
        registry.addViewController("/index.htm").setViewName("forward:/home.html");
        registry.addViewController("/index.jsp").setViewName("forward:/home.html");
    }
}
