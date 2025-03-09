package searchengine.services.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Index;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.SiteStatus;
import searchengine.model.entity.WebSite;
import searchengine.repository.PageRepository;
import searchengine.services.PageService;
import searchengine.utils.TextHandler;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service
//@Getter
@Slf4j
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    private final PageRepository pageRepository;

    @Override
    public boolean save(Page page) {   //void
        try {
            pageRepository.save(page);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Page findByPath(String path) {
        return pageRepository.findByPath(path).orElse(null);
    }

    @Override
    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    @Override
    public Page findById(Integer Id) {
        return pageRepository.findById(Id).orElse(null);
    }

    @Override
    public Page update(Page page) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {
        if (Id != null)
            pageRepository.deleteById(Id);
    }

}
