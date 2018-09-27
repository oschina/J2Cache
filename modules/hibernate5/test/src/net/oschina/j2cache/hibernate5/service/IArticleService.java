package net.oschina.j2cache.hibernate5.service;

import net.oschina.j2cache.hibernate5.bean.Article;
import org.hibernate.criterion.Criterion;

import java.util.List;

public interface IArticleService {

    public void save(Article article);

    public List<Article> find(Criterion... criterions);

    public void delete(String id);

    public Article findUnique(Criterion... criterions);

    public Article get(String id);
}
