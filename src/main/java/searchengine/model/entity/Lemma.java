package searchengine.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "lemma",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"lemma", "site_id"})}     //lemma  word
)
@Data
@NoArgsConstructor
@Slf4j
public class Lemma implements Comparable<Lemma> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private WebSite site;

    @Column(name = "lemma", nullable = false)
    private String lemma;

    @Column(name = "frequency", nullable = false)
    private Integer frequency = 0;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    private List<Index> indexes = new ArrayList<>();

    @Transient
    private int totalFrequency;

    public Lemma(String word, int totalFrequency) { //вспомогательный конструктор для организации поиска
        this.lemma = lemma;
        this.totalFrequency = totalFrequency;
    }

    @Override
    public int compareTo(Lemma lemma) {
        return totalFrequency - lemma.totalFrequency;
    }
}
