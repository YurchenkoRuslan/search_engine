package searchengine.services.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.model.entity.WebSite;
import searchengine.repository.SiteRepository;
import searchengine.services.SiteService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;

    @Override
    public WebSite findByUrl(String url){
        return siteRepository.findByUrl(url).orElse(null);
    }

    @Override
    public void deleteByUrl(String url) {
        siteRepository.deleteByUrl(url);
    }

    @Override
    public boolean save(WebSite webSite) {     //void
        try { //может возникнуть ошибка SQLIntegrityConstraintViolationException из-за дублирования сайта
            siteRepository.save(webSite);
            return false;
        }
        catch (Exception e) {
            log.warn(e.getMessage());    //"Попытка добавить существующий сайт в БД"
            return true;
        }
    }

    @Override
    public List<WebSite> findAll() {
        return siteRepository.findAll();
    }

    @Override
    public WebSite findById(Integer Id) {
        return siteRepository.findById(Id).orElse(null);
    }

    @Override
    public WebSite update(WebSite webSite) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {
        siteRepository.deleteById(Id);
    }

}
