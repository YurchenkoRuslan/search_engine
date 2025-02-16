package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private static final String INDEXING_RUNNING_MESSAGE = "Индексация уже запущена!";
    private static final String INDEXING_NOT_RUN_MESSAGE = "Индексация еще не запущена!";

    private final StatisticsService statisticsService;
    private final SiteServiceImpl siteService;
    private final PageServiceImpl pageService;

    private final ApiService apiService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResult> startIndexing() {
        if (apiService.startIndexing())
            return ResponseEntity.ok(new IndexingResult(true));

        return ResponseEntity.ok(new IndexingResult(false, INDEXING_RUNNING_MESSAGE));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResult> stopIndexing() {
        if (apiService.stopIndexing())
            return ResponseEntity.ok(new IndexingResult(true));

        return ResponseEntity.ok(new IndexingResult(false, INDEXING_NOT_RUN_MESSAGE));
    }

    @PostMapping("/indexPage")
    public ResponseEntity indexPage(String url){
        apiService.indexPage(url);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam String query, @RequestParam Integer offset,
                                 @RequestParam Integer limit, @RequestParam (required = false) String site){
        apiService.search(query, site, offset, limit);
        return ResponseEntity.ok().build();
    }
}
