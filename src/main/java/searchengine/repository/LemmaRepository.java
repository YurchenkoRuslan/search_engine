package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.WebSite;

import java.util.List;
import java.util.Optional;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {

//    @Query(value = "UPDATE WebSite SET status = :newStatus, statusTime = :newTime WHERE id = :Id")
    @Query("SELECT l FROM Lemma l WHERE l.site.id = :siteId AND l.lemma = :word")
    public Optional<Lemma> findLemmaBySiteIdAndWord(Integer siteId, String word);

    //метод возвращает список сущностей Lemma по указанной лемме
    @Query("SELECT l FROM Lemma l WHERE l.lemma = :word")
    public List<Lemma> findByWord(String word);

}
