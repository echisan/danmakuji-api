package cc.dmji.api.service;

import cc.dmji.api.entity.IndexRecommend;
import org.springframework.data.domain.Page;

public interface IndexRecommendService {

    IndexRecommend insert(IndexRecommend indexRecommend);

    IndexRecommend update(IndexRecommend indexRecommend);

    Page<IndexRecommend> listByShowIndex(Integer pn, Integer ps);

    Page<IndexRecommend> listByCreateTimeDesc(Integer pn, Integer ps);

    IndexRecommend deleteById(Long id);

    IndexRecommend delete(IndexRecommend indexRecommend);

    IndexRecommend getById(Long id);
}
