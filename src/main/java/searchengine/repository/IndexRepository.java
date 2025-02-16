package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import searchengine.model.entity.Index;

import java.util.List;
import java.util.Optional;

public interface IndexRepository extends JpaRepository<Index, Integer> {

    @Query("SELECT i from Index i WHERE i.page.id = :pageId AND i.lemma.id = :lemmaId")
    public Optional<Index> findIndexByPageIdAndLemmaId(Integer pageId, Integer lemmaId);

    public List<Index> findByLemmaId(Integer lemmaId);
}
