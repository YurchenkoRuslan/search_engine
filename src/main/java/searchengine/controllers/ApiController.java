package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexing.IndexingResult;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.ApiService;
import searchengine.services.StatisticsService;
import searchengine.services.impl.PageServiceImpl;
import searchengine.services.impl.SiteServiceImpl;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final StatisticsService statisticsService;
    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;

    private final ApiService apiService;

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    public IndexingResult startIndexing() {
        return apiService.startIndexing();
    }

    @GetMapping("/stopIndexing")
    public IndexingResult stopIndexing() {
        return apiService.stopIndexing();
    }

    @PostMapping("/indexPage")
    public void indexPage(String url){
        apiService.indexPage(url);
    }

    @GetMapping("/search")
    public void search(@RequestParam String query, @RequestParam Integer offset,
                                 @RequestParam Integer limit, @RequestParam (required = false) String site){
        apiService.search(query, site, offset, limit);
    }
}
