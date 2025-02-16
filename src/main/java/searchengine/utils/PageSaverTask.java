package searchengine.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.model.entity.Page;
import searchengine.services.ApiService;
import searchengine.services.PageService;
import searchengine.services.impl.SiteServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

@RequiredArgsConstructor
@Slf4j
public class PageSaverTask extends RecursiveAction {

//    private final SiteServiceImpl siteService;
    private final ApiService apiService;
//    private final PageService pageService;
    private final Page page;

    @Override
    public void compute() {
        if (apiService.isIndexingIsStopped()) //todo для реализации остановки индексации   //pageService.getSiteService().isStop()
            return;

        apiService.saveAndIndexingPage(page); //здесь нужно запускать индексацию ???

        log.info("Обработка страницы {} ...", page.getPath());  //, Thread.currentThread().getName());

        List<PageSaverTask> taskList = new ArrayList<>();
        for(Page childPage : page.getChildrenPages()) {
            PageSaverTask task = new PageSaverTask(apiService, childPage);
            task.fork();
            taskList.add(task);
        }

        for(PageSaverTask task : taskList) {
            task.join();
        }

    }
}
