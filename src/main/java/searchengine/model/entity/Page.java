package searchengine.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.config.AppParameters;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "page",
        indexes = {@jakarta.persistence.Index(name = "index_path", columnList = "path")}) //, unique = true
@Data
@NoArgsConstructor
@Slf4j
//@RequiredArgsConstructor
public class Page {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/128.0.0.0 YaBrowser/24.10.0.0 Safari/537.36";
    public static final String REFERER = "http://www.google.com";
    public static final String CHARSET = "UTF-8";
    public static final String FINAL_LINK_REGEX = ".*[.][a-zA-Z]+";
    public static final int TIME_DELAY = 500;
    public static final int TIME_OUT = 2_000;
//    public static final int HTTP_STATUS_OK = HttpStatus.OK.value();
//    public static final int HTTP_STATUS_NOT_FOUND = HttpStatus.NOT_FOUND.value();

//    @Transient
//    @Autowired
//    private ConfigParameters cfg;
//
//    public Page(ConfigParameters cfg) {
//        this.cfg = cfg;
//    }

//    private final AppParameters parameters;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") //, nullable = false
    private Integer Id;

    @Column(name = "path", nullable = false, unique = true) //unique = true //columnDefinition = "TEXT" - при таком типе возникает ошибка и индекс не создается "BLOB/TEXT column 'path' used in key specification without a key length"
    private String path;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Column(name = "code", nullable = false)
    private Integer responseCode;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private WebSite site;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Index> indexes = new ArrayList<>();

    @Transient
    private int absRelevance;

    @Transient
    private double relRelevance;

    public Page(WebSite site) { //конструктор для корневой страницы
//        this.parameters = parameters;
        this.site = site;
        this.path = site.getUrl();
        setParam();
    }

    public Page(WebSite site, String path) {
//        this.parameters = parameters;
        this.site = site;
        this.path = path;
        setParam();
    }

    private void setParam() {
//        Document document = null;
        try {
            Thread.sleep(TIME_DELAY);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }

//        Connection connection = null;
        Connection.Response response = null;

        try {
            response = Jsoup.connect(path)
                    .userAgent(USER_AGENT)
                    .timeout(TIME_OUT)
                    .referrer(REFERER)
                    .execute()
                    .charset(CHARSET);

            this.content = response.body();     //connection.get().outerHtml();
            this.responseCode = response.statusCode();     //HTTP_STATUS_OK; //connection.method(Connection.Method.GET).execute().statusCode();      HTTP_STATUS_OK
        } catch (IOException e) {
            this.content = "";
            this.responseCode = 444;//((HttpStatusException) e).getStatusCode();        //HTTP_STATUS_NOT_FOUND;
            log.warn("Ошибка при чтении страницы: " + path);
        }
//        finally {     //убрать, т.к. статус сайта обновляется в PageServiceImpl при сохранении страницы
//            this.site.setStatus(SiteStatus.INDEXING);
//        }
    }

    public List<Page> getChildrenPages() {
        if (path.matches(FINAL_LINK_REGEX)) {
            return new ArrayList<>();
        }
        return getChildrenLinks().stream()
                .map(link -> new Page(this.site, link))
                .toList();
    }

    public Set<String> getChildrenLinks() {    //Set<String>        //private
        Document document = Jsoup.parse(content, path);   //Jsoup.parse(content);
//        try {       //избавиться от данного блока?
//            document = Jsoup.connect(path)
//                    .userAgent(USER_AGENT)
//                    .referrer(REFERER)
//                    .get();
//        } catch (IOException e) {
//            return new HashSet<>();
//        }

//        if ( path.matches("(.*).php")) {
//            return new HashSet<>();
//        }
        return document.select("a").stream()   //List<String> links =
                .map(link -> link.absUrl("href"))//attr //преобразуем все ссылки в абсолютный вид
//                .filter(this::validLink1) // убираем невалидные ссылки
//                .map(this::changeLink) //преобразуем оставшиеся ссылки к полному виду
                .filter(this::isValidLink) //оставляем только дочерние ссылки
                .collect(Collectors.toSet());
    }

    private boolean isValidLink(String link) {
//        if (link.equals(path + "/")) {
//            return false;
//        }
        if (link.contains("#")) { //ссылка на раздер страницы
            return false;
        }
        return link.matches(path + "(.+)");     //link.startsWith(path)
    }

    public String getTitle(){
        return Jsoup.parse(content).title();
    }

    public static boolean isReadable(String path){
        try {
            Jsoup.connect(path)
                    .userAgent(USER_AGENT)
                    .referrer(REFERER)
                    .get();
        } catch (IOException e) {
//            throw new RuntimeException(e);
            return false;
        }
        return true;
    }

    public static boolean pageFromSiteList(String path, List<WebSite> sites) {
        for (WebSite site : sites) {
            if (path.contains(site.getUrl()))
                return true;
        }
        return false;
    }

    public static WebSite getSiteFromSiteList(String path, List<WebSite> sites) {
        for (WebSite site : sites) {
            if (path.contains(site.getUrl()))
                return site;
        }
        return null;
    }
//    private boolean validLink(String link) {
//        if ((link.matches("http[s]?://(.*)") && !link.contains(site.getUrl()))) { //ссылка на другой сайт / Main.URL
//            return false;
//        }
//
//        if (link.equals(path) || link.equals(path + "/") || link.equals("/")) { //автоссылка
//            return false;
//        }
//
//        if (link.matches("[.]+/(.*)")) {//обратные ссылки
//            return false;
//        }
//
//        if (link.contains("#")) { //ссылка на раздер страницы
//            return false;
//        }
//
//        return true;
//    }
//
//    private String changeLink(String link) {
//        if (link.contains(path)) {
//            return link;
//        }
//
//        if (link.matches("//(.*)")) {
//            return site.getProtocol() + link;    //Main.protocol
//        }
//
//        if (link.matches("[^/](.*)")) {
//            if (path.charAt(path.length() - 1) != '/') {
//                return path + "/" + link;
//            }
//            return path + link;
//        }
//
//        if (link.matches("/[^/](.+)")) {
//            if (path.charAt(path.length() - 1) == '/') {
//                return path.substring(0, path.length() - 1)  + link;
//            }
//            return path + link;
//        }
//
//        if (link.matches("/")) {
//            return path;
//        }
//
//        return link;
//    }
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Page page = (Page) object;
        return Objects.equals(Id, page.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }
}
