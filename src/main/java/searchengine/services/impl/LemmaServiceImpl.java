package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Lemma;
import searchengine.repository.LemmaRepository;
import searchengine.services.LemmaService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LemmaServiceImpl implements LemmaService {

    private final LemmaRepository lemmaRepository;

    @Override
    public List<Lemma> findAll() {
        return lemmaRepository.findAll();
    }

    @Override
    public Lemma findById(Integer Id) {
        return lemmaRepository.findById(Id).orElse(null);
    }

    @Override
    public boolean save(Lemma lemma) { //void
        try {
            lemmaRepository.save(lemma);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public Lemma update(Lemma lemma) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {
        lemmaRepository.deleteById(Id);
    }

    @Override
    public Lemma findLemmaBySiteIdAndWord(Integer siteId, String word) {
//        try {
            return lemmaRepository.findLemmaBySiteIdAndWord(siteId, word).orElse(null);
//        } catch (Exception e) {
//            log.warn("{}. Id сайта: {}, слово: '{}'",  e.getMessage(), siteId, word);
//            throw new RuntimeException();
//            //todo добавить
//        }
    }

    @Override
    public List<Lemma> findByWord(String word) {
        try {
            return lemmaRepository.findByLemma(word);    //findByLemma(word)
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
