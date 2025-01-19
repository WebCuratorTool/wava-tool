package org.webcurator.visualization.springboot;

// import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
public class MainWebConfiguration implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/index.html").setViewName("forward:/index.html");
        registry.addViewController("/index.htm").setViewName("forward:/index.html");
        registry.addViewController("/index.jsp").setViewName("forward:/index.html");
    }
}
