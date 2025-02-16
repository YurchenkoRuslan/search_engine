package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import searchengine.model.entity.Page;

import java.util.Optional;

public interface PageRepository extends JpaRepository<Page,Integer> {

    Optional<Page> findByPath(String path);

}
