package cc.dmji.api.service.impl;

import cc.dmji.api.entity.IndexRecommend;
import cc.dmji.api.enums.Status;
import cc.dmji.api.repository.IndexRecommendRepository;
import cc.dmji.api.service.IndexRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class IndexRecommendServiceImpl implements IndexRecommendService {
    @Autowired
    private IndexRecommendRepository indexRecommendRepository;

    @Override
    public IndexRecommend insert(IndexRecommend indexRecommend) {
        return indexRecommendRepository.save(indexRecommend);
    }

    @Override
    public IndexRecommend update(IndexRecommend indexRecommend) {
        return indexRecommendRepository.save(indexRecommend);
    }

    @Override
    public Page<IndexRecommend> listByShowIndex(Integer pn, Integer ps) {
        return indexRecommendRepository
                .findByShowIndexEqualsAndRecommendStatusEquals(true,Status.NORMAL,
                        PageRequest.of(pn-1,ps, Sort.Direction.DESC,"modifyTime"));
    }

    @Override
    public Page<IndexRecommend> listByCreateTimeDesc(Integer pn, Integer ps) {
        return indexRecommendRepository.findAll(PageRequest.of(pn - 1, ps, Sort.Direction.DESC, "createTime"));
    }

    @Override
    public IndexRecommend deleteById(Long id) {
        IndexRecommend indexRecommend = indexRecommendRepository.findById(id).orElse(null);
        if (indexRecommend == null){
            return null;
        }
        indexRecommend.setRecommendStatus(Status.DELETE);
        return indexRecommendRepository.save(indexRecommend);
    }

    @Override
    public IndexRecommend delete(IndexRecommend indexRecommend) {
        indexRecommend.setRecommendStatus(Status.DELETE);
        return indexRecommendRepository.save(indexRecommend);
    }

    @Override
    public IndexRecommend getById(Long id) {
        return indexRecommendRepository.findById(id).orElse(null);
    }
}
