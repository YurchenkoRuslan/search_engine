package searchengine.services;

import searchengine.model.entity.WebSite;

import java.util.Optional;

public interface SiteService extends EntityService<WebSite, Integer> {

//    void updateStatus(WebSite webSite);
    void deleteByUrl(String url);

    public WebSite findByUrl(String url);

}
