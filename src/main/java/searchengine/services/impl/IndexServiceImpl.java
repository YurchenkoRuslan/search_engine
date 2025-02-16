package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Index;
import searchengine.repository.IndexRepository;
import searchengine.services.IndexService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexServiceImpl implements IndexService {

    private final IndexRepository indexRepository;

    @Override
    public List<Index> findAll() {
        return indexRepository.findAll();
    }

    @Override
    public Index findById(Integer Id) {
        return indexRepository.findById(Id).orElse(null);
    }

    @Override
    public boolean save(Index index) { //void
        try {
            indexRepository.save(index);
            return true;
        } catch (Exception e) {
            log.warn(e.getMessage());
            return false;
        }

    }

    @Override
    public Index update(Index index) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {
        indexRepository.deleteById(Id);
    }

    @Override
    public Index findIndexByPageIdAndLemmaId(Integer pageId, Integer lemmaId) {
//        try {
            return indexRepository.findIndexByPageIdAndLemmaId(pageId, lemmaId).orElse(null);
//        } catch (Exception e) {
//            log.warn(e.getMessage());
//            return null;
//        }

    }

    @Override
    public List<Index> findByLemmaId(Integer lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }
}
