package searchengine.others;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ParsePageTest {

    private String path = "https://svetlovka.ru";
    Connection.Response response = null;

    @Test
    public void test () {
        int statusCode = 0;
        String statusMessage = "";
        try {
            response = Jsoup.connect(path)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(1000) //максимальное время, в течение которого клиент будет ждать ответа от сервера
                    .execute();
            statusCode = response.statusCode();
            System.out.println(response.body());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            HttpStatusException ex = (HttpStatusException) e;
            statusCode = ex.getStatusCode();
        }

//        statusCode = response.statusCode();
        System.out.println("Статус код страницы: " + statusCode);


    }

//    public int getSitemapStatus() {
//        int statusCode = response.statusCode();
//        return statusCode;
//    }
    public ArrayList<String> getUrls() throws IOException {
        ArrayList<String> urls = new ArrayList<String>();
        Document doc = response.parse();
        // do whatever you want, for example retrieving the <url> from the sitemap
        for (Element url : doc.select("url")) {
            urls.add(url.select("loc").text());
        }
        return urls;
    }

    @Test
    public void test2() {
        System.out.println((double) 1/1);
    }

}
