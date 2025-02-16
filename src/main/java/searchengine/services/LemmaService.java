package searchengine.services;

import jakarta.transaction.Transactional;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.WebSite;

import java.util.List;
import java.util.Optional;

public interface LemmaService extends EntityService<Lemma, Integer> {

//    @Transactional
    public Lemma findLemmaBySiteIdAndWord(Integer siteId, String word);

    public List<Lemma> findByWord(String word);
}
