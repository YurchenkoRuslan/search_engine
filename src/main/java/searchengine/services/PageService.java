package searchengine.services;

import searchengine.model.entity.Page;

public interface PageService extends EntityService<Page, Integer>{

   Page findByPath(String path);

}
