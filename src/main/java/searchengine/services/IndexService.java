package searchengine.services;

import jakarta.transaction.Transactional;
import searchengine.model.entity.Index;

import java.util.List;
import java.util.Optional;

public interface IndexService extends EntityService<Index, Integer>{

//    @Transactional
    public Index findIndexByPageIdAndLemmaId(Integer pageId, Integer lemmaId);

    public List<Index> findByLemmaId(Integer lemmaId);

}
