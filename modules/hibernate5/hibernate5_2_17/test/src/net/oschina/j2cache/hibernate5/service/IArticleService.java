/**
 * Copyright (c) 2014-Now http://j2eplus.com All rights reserved.
 */
package net.oschina.j2cache.hibernate5.service;

import net.oschina.j2cache.hibernate5.bean.Article;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This guy is lazy, nothing left.
 *
 * @author Tandy 2018/9/20 13:54
 */
public interface IArticleService {

    public void save(Article article);

    public List<Article> find(Criterion... criterions);

    public void delete(String id);

    public Article findUnique(Criterion... criterions);

    public Article get(String id);
}
