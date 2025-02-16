package searchengine.services;

import java.util.List;

public interface EntityService<E, Id> {
    List<E> findAll();
    E findById(Id id);
    boolean save(E e); //void
    E update(E e);
    void deleteById(Id id);
    //List<Entity> filterBy(Filter filter);
}
