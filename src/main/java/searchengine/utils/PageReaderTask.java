package searchengine.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.model.entity.Page;
import searchengine.services.ApiService;
import searchengine.services.impl.PageServiceImpl;
import searchengine.services.impl.SiteServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

//@Component
@RequiredArgsConstructor
@Slf4j
public class PageReaderTask extends RecursiveAction {

    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;
//    private final ApiService apiService;
    private final Page page;

    @Override
    public void compute() {
////        if (apiService.isIndexingIsStopped())
//        if (siteService.isIndexingIsStopped()) //todo для реализации остановки индексации   //pageService.getSiteService().isStop()
//            return;
//
//        pageService.saveAndIndexingPage(page); //здесь нужно запускать индексацию ???
////        apiService.saveAndIndexingPage(page); //здесь нужно запускать индексацию ???
//
//        log.info("Обработка страницы {}  ...", page.getPath());
//
//        List<PageReaderTask> taskList = new ArrayList<>();
//        for(Page childPage : page.getChildrenPages()) {
//            PageReaderTask task = new PageReaderTask(siteService, pageService, childPage);
////            PageReaderTask task = new PageReaderTask(apiService, childPage);
//            task.fork();
//            taskList.add(task);
//        }
//
//        for(PageReaderTask task : taskList) {
//            task.join();
//        }

    }
}
