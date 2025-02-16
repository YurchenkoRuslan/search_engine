package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.utils.TextHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Element::baseUri !!!


public class ApplicationTest {

    final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (HTML, like Gecko) " +
            "Chrome/128.0.0.0 YaBrowser/24.10.0.0 Safari/537.36";
    final String referer = "http://www.google.com";

    String path = "https://svetlovka.ru/projects/kids/";       //https://sendel.ru/

    private static final LuceneMorphology russianMorphology;
    public static final String WORD_SEPARATOR_REGEX = "[^а-яА-Я]";

    static {
        try {
            russianMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void morphologyTest() throws IOException {

//        String regex = ".*[.][a-zA-Z]+";
//        String text = "https://metanit.com/kotlin/tutorial/1.12.html";
//        System.out.println(text.matches(regex));
        int x = 7;
        int y = 10;
        System.out.println((double) 7/10);

    }

    @Test
    public void testTest(){

    }


    @Test
    public void test() throws IOException {

        Document doc = Jsoup.connect(path)
                .userAgent(userAgent)
                .referrer(referer)
                .get();

        path = doc.baseUri();

        System.out.println(path);

        List<String> stringList =
        doc.select("a").stream()
                .map(l -> l.absUrl("href")) //преобразует относительный адрес в абсолютный
//                .filter(this::validLink)
//                .map(this::changeLink)
                .filter(this::isSubLink)
                .toList();

        stringList.forEach(System.out::println);
        System.out.println();
        System.out.println(stringList.size());
    }

    private boolean isSubLink(String link) {
//        if (link.equals(path + "/")) {
//            return false;
//        }
        return link.matches(path + "(.+)");
    }

    private boolean validLink(String link) {
        if ((link.matches("https?://(.*)") && !link.contains(path))) { //ссылка на другой сайт / site.getUrl() / + //проработать вариант с www
            return false;
        }

        if (link.equals(path) || link.equals(path + "/") || link.equals("/") || link.isEmpty()) { //автоссылка или пустая ссылка
            return false;
        }

        if (link.matches("[.]+/(.*)")) {//обратные ссылки ("./text")
            return false;
        }

        if (link.contains("#") || link.contains("tel:") || link.contains("mailto:") || link.contains("tg:")) {
            //ссылка на раздел страницы или телефоны или емейлы
            //заменить на link.contains(":")
            return false;
        }

        return true;
    }

    private String changeLink(String link) { //приводит ссылку к виду http(s)://url.ru(com)/...
        if (link.contains(path)) {
            return link;
        }

        if (link.matches("//(.*)")) { // ссылки типа '//text...'
            return getProtocol() + link;
        }

        if (link.matches("[^/](.*)")) {  //ссылка типа 'text...'
            if (path.charAt(path.length() - 1) != '/') { //если в url последний символ не '/'
                return path + "/" + link; //то добавляем к url '/' и ссылку и возвращаем ее
            }
            return path + link;
        }

        if (link.matches("/[^/](.+)")) { //ссылка типа '/text...'
            if (path.charAt(path.length() - 1) == '/') { //если в url последний символ '/'
                return path.substring(0, path.length() - 1)  + link; //то убираем / и прибавляем ссылку
            }
            return path + link;
        }

        if (link.matches("/")) {
            return path;
        }

        return link;
    }

    public String getProtocol() {
        return path.substring(0, path.indexOf("/"));
    }
}
