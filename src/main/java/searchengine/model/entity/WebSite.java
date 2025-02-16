package searchengine.model.entity;

import lombok.Data;

import jakarta.persistence.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.SiteStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static searchengine.model.entity.Page.REFERER;
import static searchengine.model.entity.Page.USER_AGENT;

@Entity
@Table(name = "site")
@Data
public class WebSite { //implements Runnable

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

    @Column(name = "url", columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String name;

    @Column(name = "status", columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    @Enumerated(EnumType.STRING)
    private SiteStatus status;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastErrorMessage;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Page> pages = new ArrayList<>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Lemma> lemmas = new ArrayList<>();

    @Transient
    private boolean readable = true;

    public WebSite(){
        statusTime = new Date();
//        status = SiteStatus.INDEXING;
//        setStatus(SiteStatus.INDEXING);
    }

    public WebSite(String url, String name) {
        this();
        this.name = name;
        setUrl(url);
    }

    public void setUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .referrer(REFERER)
                    .get();
            this.url = doc.baseUri();
        } catch (IOException e) {
//            throw new RuntimeException(e);
            this.url = url;
            this.readable = false;
        }
    }

    public void setStatus(SiteStatus status) { //устанавливаем новый статус и обновляем время статуса
        this.status = status;
        this.statusTime = new Date();
    }

    public void setStatus(SiteStatus status, String lastErrorMessage) { //устанавливаем статус ошибки
        setStatus(status);
        this.lastErrorMessage = lastErrorMessage;
    }

    @Override
    public String toString() {
        return "WebSite{" +
                "Id=" + Id +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
//                ", status=" + status +
//                ", statusTime=" + statusTime +
//                ", lastErrorMessage='" + lastErrorMessage + '\'' +
//                ", readable=" + readable +
                '}';
    }

//    public String getProtocol() {
//        return url.substring(0, url.indexOf("/"));
//    }
}
