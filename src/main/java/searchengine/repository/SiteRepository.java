package searchengine.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteStatus;
import searchengine.model.entity.WebSite;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<WebSite, Integer> {

//    @Transactional //операции изменения записей БД должны производиться транзакционно
//    @Modifying
//    @Query(value = "UPDATE WebSite SET status = :newStatus, statusTime = :newTime WHERE id = :Id")
//    void updateStatus(Integer Id, SiteStatus newStatus, Date newTime);

    @Transactional
    void deleteByUrl(String url);

    Optional<WebSite> findByUrl(String url);

}
