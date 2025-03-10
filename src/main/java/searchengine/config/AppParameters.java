package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")  //indexing-settings
public class AppParameters {
    private List<Site> sites;

    private String userAgent;
    private String referer;
    private int timeDelay;
}
