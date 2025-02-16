package searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TextHandlerTest {

    @Test
    public void clearHtmlTagsTest() {
        System.out.println(TextHandler.clearHtmlTags(getContent()));
    }

    @Test
    public void isIndependentWordText() {
//        System.out.println(TextHandler.isIndependentWord("жопа"));
    }

    @Test
    public void getLemmasTest() {
        String content = getContent();
        for (Map.Entry<String, Integer> entry : TextHandler.getLemmas(content).entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    @Test
    public void getLemmaTest() {
        System.out.println(TextHandler.getLemma("DDSgd"));
    }

    @Test
    public void getSnippetsTest() {
        String content = getContent();

//        System.out.println(content);

        String searchRequest = " замыкания на простейшем примере";//"Каждый программист создает десятки классов";
        List<String> lemmas = TextHandler.getLemmas(searchRequest).keySet().stream().toList();
        String snippet = TextHandler.getSnippet(content, lemmas);
        System.out.println(snippet);

    }

    private String getContent() {
        String path = "for_testing/Kotlin_Замыкания.html";
        StringBuilder content = new StringBuilder();
        try {
            //метод читает все строки указанного файла и возвращает их в List<String>:
            List<String> lines = Files.readAllLines(Paths.get(path));
            lines.forEach(line -> content.append(line + "\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();

//        String path = "https://metanit.com/kotlin/tutorial/3.10.php ";
//
////        Document document = null;
//
//        String content = null;
//        try {
//            content = Jsoup.connect(path).get().outerHtml();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        return content;
    }

}
