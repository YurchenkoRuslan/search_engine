package searchengine.dto.indexing;


import org.jsoup.Jsoup;
import searchengine.model.entity.Page;
import searchengine.utils.TextHandler;

import java.util.List;

public class RelevancePage {

    private String uri;
    private String title;
    private String snippet;
    private double relevance;

    public RelevancePage(Page page, List<String> lemmas) {
        this.uri = page.getPath();
        this.title = page.getTitle();
        this.relevance = page.getRelRelevance();
        this.snippet = TextHandler.getSnippet( getBody(page.getContent()), lemmas); //считывать только тело страницы
    }

    @Override
    public String toString() {
        return "RelevancePage{" +
                "uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", relevance=" + relevance +
                ", \nsnippet='" + snippet + '\'' +
                '}';
    }

    private static String getBody(String htmlPage) {
        return Jsoup.parse(htmlPage).body().text();
    }
}
