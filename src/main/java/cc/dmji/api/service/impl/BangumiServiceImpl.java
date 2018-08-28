package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.repository.BangumiRepository;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.utils.BangumiPageInfo;
import cc.dmji.api.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;
import java.util.List;


@Service
public class BangumiServiceImpl implements BangumiService {

    @Autowired
    private BangumiRepository bangumiRepository;

    @Override
    public BangumiPageInfo listBangumis() {
        return listBangumis(1, 20);
    }

    @Override
    public BangumiPageInfo listBangumis(Integer pageNum) {
        return listBangumis(pageNum, 20);
    }

    @Override
    public BangumiPageInfo listBangumis(Integer pageNum, Integer pageSize) {
        Page<Bangumi> result = bangumiRepository.findAll(PageRequest.of(pageNum - 1, pageSize,Sort.Direction.DESC,"viewCount"));
        PageInfo pageInfo = new PageInfo(pageNum, pageSize, result.getTotalElements());
        return new BangumiPageInfo(result.getContent(), pageInfo);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name) {
        return listBangumisByName(name, 1, 20);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name, Integer pageNum) {
        return listBangumisByName(name, pageNum, 20);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name, Integer pageNum, Integer pageSize) {
        Page<Bangumi> result = null;
        result = bangumiRepository.findBangumisByBangumiNameLike(name, PageRequest.of(pageNum - 1, pageSize));
        PageInfo pageInfo = new PageInfo(pageNum, pageSize, result.getTotalElements());
        return new BangumiPageInfo(result.getContent(), pageInfo);
    }


    @Override
    public List<Bangumi> listBangumisAmbiguous(String name) {
        throw new NotImplementedException();
    }

    @Override
    public Bangumi getBangumiById(Long id) {
        return bangumiRepository.findById(id).orElse(null);
    }

    @Override
    public Bangumi getBangumiByName(String name) {
        return bangumiRepository.findByBangumiNameEquals(name);
    }

    @Override
    public List<Bangumi> getBangumisByIds(List<Long> ids) {
        return bangumiRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public Bangumi insertBangumi(Bangumi bangumi) {
        bangumi.setViewCount(0L);
        setCreateAndModifyTime(bangumi);
        return bangumiRepository.save(bangumi);
    }

    @Override
    @Transactional
    public Bangumi updateBangumi(Bangumi bangumi) {
        return bangumiRepository.save(bangumi);
    }

    @Override
    @Transactional
    public void deleteBangumiById(Long id) {
        bangumiRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBangumis(List<Bangumi> bangumis) {
        bangumiRepository.deleteInBatch(bangumis);
    }

    @Override
    public Integer getEposideTotalByBangumiId(Long bangumiId) {
        Bangumi bangumi = bangumiRepository.findById(bangumiId).orElse(null);
        return bangumi == null ? null : bangumi.getEpisodeTotal();
    }

    @Override
    public List<Bangumi> listBangumiByIds(List<Long> ids) {
        return bangumiRepository.findByBangumiIdIn(ids);
    }

    @Override
    public Page<Bangumi> listBangumiOrderByViewCount(Integer pn, Integer ps) {
        return bangumiRepository.findAll(PageRequest.of(pn - 1, ps, Sort.Direction.DESC, "viewCount"));
    }

    private void setModifyTime(Bangumi bangumi) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        bangumi.setModifyTime(date);
    }

    private void setCreateAndModifyTime(Bangumi bangumi) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        bangumi.setModifyTime(date);
        bangumi.setCreateTime(date);
    }


}
