package searchengine.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.AppParameters;
import searchengine.dto.indexing.IndexingResult;
import searchengine.dto.indexing.RelevancePage;
import searchengine.model.SiteStatus;
import searchengine.model.entity.Index;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.entity.WebSite;
import searchengine.utils.CollectionUtils;
import searchengine.utils.PageSaverTask;
import searchengine.utils.TextHandler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
public class ApiService {

    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private  final IndexService indexService;
    private final AppParameters parameters;

    private static final String INDEXING_RUNNING_MESSAGE = "Индексация уже запущена!";
    private static final String INDEXING_NOT_RUN_MESSAGE = "Индексация еще не запущена!";
    private static final String INDEXING_STOPPED_MESSAGE = "Индексация остановлена пользователем!";
    private static final String INDEXING_STOPPED_MESSAGE_PARAM = "Индексация сайта {} остановлена пользователем.";
    private static final String INDEXING_SUCCESSFUL_MESSAGE = "Индексация сайтов успешно завершена!";
    private static final String FAIL_SITE_RIDDING_MESSAGE = "Ошибка чтения сайта!";

    private boolean indexingIsStopped = false;
    private boolean indexingIsRunning = false;

    //todo метод сохраняет в БД все сайты из конфигурационного файла
    @PostConstruct
    private void getSitesFromConfig() {
        for (Site site : parameters.getSites()) {
            siteService.deleteByUrl(site.getUrl());
            WebSite webSite = new WebSite(site.getUrl(), site.getName());
            if (!webSite.isReadable()) {
                webSite.setStatus(SiteStatus.FAILED, FAIL_SITE_RIDDING_MESSAGE);
                log.warn(FAIL_SITE_RIDDING_MESSAGE + webSite.getUrl());
            }
            siteService.save(webSite);
        }
    }

    //todo метод индексации сайтов
    public IndexingResult startIndexing() {    //boolean
        if (indexingIsRunning) {
            log.warn(INDEXING_RUNNING_MESSAGE);
            return new IndexingResult(false, INDEXING_RUNNING_MESSAGE);     //false;
        }

        indexingIsRunning = true;
        indexingIsStopped = false;
        List<WebSite> webSites = siteService.findAll();

        int amountSites = webSites.size();
        AtomicInteger processedSitesCount = new AtomicInteger();

        for (WebSite webSite : webSites) {
            if (webSite.getStatus() != null && webSite.getStatus().equals(SiteStatus.FAILED)) {
                processedSitesCount.getAndIncrement();
                continue;
            }
            siteService.save(webSite);

            new Thread(() -> {
                Page rootPage = new Page(webSite);
                new ForkJoinPool().invoke(new PageSaverTask(this, rootPage));
                if (indexingIsStopped) return;
                log.info("Индексация сайта " + webSite.getUrl() + " завершена.");
                webSite.setStatus(SiteStatus.INDEXED);
                siteService.save(webSite);
                processedSitesCount.getAndIncrement();
            }).start();
        }

        while (processedSitesCount.get() < amountSites) {
            if (indexingIsStopped) {
                log.warn(INDEXING_STOPPED_MESSAGE);
                return new IndexingResult(false, INDEXING_RUNNING_MESSAGE);     //false;
            }
        }

        log.info(INDEXING_SUCCESSFUL_MESSAGE);
        indexingIsRunning = false;
        return new IndexingResult(true); //true;
    }

    //todo метод останавливает индексацию
    public IndexingResult stopIndexing() {      //boolean;
        if (!indexingIsRunning) {
            log.warn(INDEXING_NOT_RUN_MESSAGE);
            return new IndexingResult(false, INDEXING_NOT_RUN_MESSAGE); //false;
        }

        indexingIsRunning = false;
        indexingIsStopped = true;

        setFailedStatus();
        return new IndexingResult(true);    //true;
    }

    private void setFailedStatus() {
        for(WebSite site : siteService.findAll()) {
            if (site.getStatus().equals(SiteStatus.INDEXING)) {
                site.setStatus(SiteStatus.FAILED, INDEXING_STOPPED_MESSAGE);
                log.warn(INDEXING_STOPPED_MESSAGE_PARAM, site.getUrl());
                siteService.save(site);
            }
        }
    }

    public boolean saveAndIndexingPage(Page page) {
        return indexPage(page); //todo применять при полной индексации
    }

    //todo метод запускает индексацию страницы по url
    public boolean indexPage(String path) {
        WebSite site = Page.getSiteFromSiteList(path, siteService.findAll());
        if (site == null) {
            log.error(MessageFormat.format("Страница {0} с постороннего сайта!",path));
            return false;
        }
        if (!Page.isReadable(path)) {
            log.error(MessageFormat.format("Ошибка чтения страницы {0}",path));
            return false;
        }

        Page page = pageService.findByPath(path);
        if (page == null) {
            log.info("Страница {} не найдена в БД", path);
            page = new Page(site, path);
        } else {
            log.info("Страница {} найдена в БД", path);
        }

        indexingIsStopped = false;
        return indexPage(page);
    }

    //todo метод запускает индексацию страницы по объекту Page
    public boolean indexPage(Page page) {
        log.info("Индексация страницы {} запущена.", page.getPath());

        if (!pageService.save(page)) {
            log.warn("Страница {} уже есть в БД!", page.getPath());
            return false;
        }

        WebSite site = page.getSite();
        if (indexingIsRunning) {
            site.setStatus(SiteStatus.INDEXING);
            siteService.save(site);
        }

        String pageText = TextHandler.clearHtmlTags(page.getContent());
        Map<String, Integer> lemmaList = TextHandler.getLemmas(pageText);

        for(Map.Entry<String, Integer> entry : lemmaList.entrySet()) {
            if (indexingIsStopped) {
                return false;
            }
            saveLemmaAndIndex(site, page, entry.getKey(), entry.getValue());
        }
        log.info("Индексация страницы {} завершена.", page.getPath());
        return true;
    }

    //todo метод сораняет лемму и индекс в ВБ
    @Transactional
    private void saveLemmaAndIndex(WebSite site, Page page, String word, Integer rating) {
        Lemma lemma = lemmaService.findLemmaBySiteIdAndWord(site.getId(), word);
        if (lemma == null) {
            lemma = new Lemma();
            lemma.setLemma(word);    //lemma     word
            lemma.setSite(site);
            lemma.setFrequency(1);
        } else {
            lemma.setFrequency(lemma.getFrequency() + 1);
        }
        lemmaService.save(lemma);

        Index index = indexService.findIndexByPageIdAndLemmaId(page.getId(), lemma.getId());
        if (index == null) {
            index = new Index();
            index.setPage(page);
            index.setLemma(lemma);
            index.setRating(rating);
            indexService.save(index);
        }

        if (indexingIsStopped) {
            setFailedStatus();
        }
    }

    //todo метод возвращает список релевантных страниц по поисковому запросу
    public  List<RelevancePage> search (String query, String siteUrl, Integer offset, Integer limit) {
        log.info("Запуск поиска со следующими параметрами:");
        log.info("Query: \"{}\", siteUrl: {}, offset: {}, limit: {}", query, siteUrl, offset, limit);

        offset = offset == null ? 0 : offset;
        limit = limit == null ? 20 : limit;
        List<WebSite> sites = siteUrl == null ? siteService.findAll() : List.of(siteService.findByUrl(siteUrl));
        List<String> queryLemmas = TextHandler.getLemmas(query).keySet().stream().toList(); //todo 1 список слов из запроса

        List<Lemma> lemmaListByQuery = new ArrayList<>();//todo список лемм с частотамиих появления на всех страницах всех сайтов
        List<Index> indexListFromDB = new ArrayList<>(); //todo список индексов из БД в которых есть эти леммы
        List<Lemma> lemmaListFromDB = new ArrayList<>(); //todo список лемм из БД, которые встречаются в запросе
        List<Page> pageListFromDB = new ArrayList<>(); //todo список страниц, на которых присутствуют все леммы

        log.info("Итоговые параметры для поиска: offset: {}, limit: {}", offset, limit);
        log.info("Поиск по сайтам:");
        sites.forEach(s -> log.info(String.valueOf(s)));
        log.info("Леммы поискового запроса:");
        queryLemmas.forEach(log::info);

        //todo 2
        int amountPages = siteUrl == null ? pageService.findAll().size() : sites.get(0).getPages().size();
        log.info("Общее количество страниц для поиска: {}", amountPages);
        double maxPercentagePage = getMaxPercentagePage(amountPages);
        log.info("maxPercentagePage: {}", maxPercentagePage);
        for (String word : queryLemmas){
            int totalFrequencyOfLemma = getTotalFrequencyOfLemma(word, sites);
            if ((double) totalFrequencyOfLemma / amountPages <= maxPercentagePage) { //если данная лемма встречается на менее чем 60% страниц
                lemmaListByQuery.add(new Lemma(word, totalFrequencyOfLemma)); //добавлять ее в итоговый список лемм
            }
        }

        //todo 3
        Collections.sort(lemmaListByQuery); //сортируем список лемм по возрастанию частоты
        log.info("Итоговый список лемм для поиска:");
        lemmaListByQuery.forEach(l -> log.info(l.getLemma()));  //getLemma  getWord

        //todo 4
        List<List<Page>> pageLists = new ArrayList<>();
        for (Lemma currentLemma : lemmaListByQuery) {
            String word = currentLemma.getLemma();      //getLemma  getWord

            //список лемм из БД по указанному слову
            //все леммы, либо с указанного сайта (т.е. 1 лемма), либо со всех сайтов
            List<Lemma> lemmaListByWord = siteUrl == null ?
                    lemmaService.findByWord(word) : //либо все леммы по казананному слову
                    List.of(lemmaService.findLemmaBySiteIdAndWord(sites.get(0).getId(), word)); //либо 1 лемма, если сайт 1
            lemmaListFromDB.addAll(lemmaListByWord);
            for (Lemma lemma : lemmaListByWord) { //заполняем список индексов
                indexListFromDB.addAll(indexService.findByLemmaId(lemma.getId()));
            }
            List<Page> pages = indexListFromDB.stream()
                    .map(index -> pageService.findById(index.getPage().getId()))
                    .toList();  //список страниц из таблицы индексов с такой леммой
            pageLists.add(pages);
        }
        pageListFromDB = CollectionUtils.getCommonElementsList(pageLists);
        if (pageListFromDB.isEmpty()) { //todo 5
            log.warn("Страниц по указанному запросу не найдено!");
            return new ArrayList<>();
        }
        log.info("Список страниц для поиска:");
        pageListFromDB.forEach(p -> log.info(p.getPath()));

        //todo 6
        for (Page page : pageListFromDB) { //перебираем список страниц
            int absRelevance = 0;
            for (Lemma lemma : lemmaListFromDB) { //перебираем список всех лемм
                Index index = indexService.findIndexByPageIdAndLemmaId(page.getId(), lemma.getId());
                absRelevance += (index == null ? 0 : index.getRating());
            }
            page.setAbsRelevance(absRelevance);
        }
        int maxRelevance =  pageListFromDB.stream()
                .mapToInt(Page::getAbsRelevance)
                .max()
                .getAsInt();
        for (Page page : pageListFromDB) {
            page.setRelRelevance((double) page.getAbsRelevance() / maxRelevance);
        }
        pageListFromDB.sort((page1, page2) -> page2.getAbsRelevance() - page1.getAbsRelevance());

        //todo 7 преобразуем объекты Page в RelevancePage
        List<String> lemmasOfQuery = lemmaListByQuery.stream()
                .map(Lemma::getLemma)       //getLemma   getWord
                .toList();
        List<RelevancePage> relevancePages = pageListFromDB.stream()
                .map(p -> new RelevancePage(p, lemmasOfQuery))
                .toList();

        log.info("Результат поиска:");
        relevancePages.forEach(rp -> log.info(String.valueOf(rp)));

        return relevancePages;
    }

    private int getTotalFrequencyOfLemma(String word, List<WebSite> sites) {
        log.info("Запуск метода getTotalFrequencyOfLemma по лемме '{}'", word);
        int totalFrequencyOfLemma = 0;
        for (WebSite site : sites) {
            Lemma lemma = lemmaService.findLemmaBySiteIdAndWord(site.getId(), word);
            totalFrequencyOfLemma += (lemma == null ? 0 : lemma.getFrequency());
        }
        log.info("Количество слов '{}' на сайтах: {}", word, totalFrequencyOfLemma);

        return totalFrequencyOfLemma;
    }

    private double getMaxPercentagePage(int amountPage) {
        if (amountPage >= 1 && amountPage <= 50) {
            return 1.;
        }
        return 0.6;
    }

}
