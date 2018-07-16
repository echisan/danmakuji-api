package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.repository.BangumiRepository;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.utils.BangumiPageInfo;
import cc.dmji.api.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


@Service
public class BangumiServiceImpl implements BangumiService {

    @Autowired
    private BangumiRepository bangumiRepository;

    @Override
    public BangumiPageInfo listBangumis() {
        return listBangumis(1,20);
    }

    @Override
    public BangumiPageInfo listBangumis(Integer pageNum) {
        return listBangumis(pageNum, 20);
    }

    @Override
    public BangumiPageInfo listBangumis(Integer pageNum, Integer pageSize) {
        Page<Bangumi> result = bangumiRepository.findAll(PageRequest.of(pageNum-1,pageSize));
        PageInfo pageInfo = new PageInfo(pageNum,pageSize,result.getTotalElements());
        return new BangumiPageInfo(result.getContent(),pageInfo);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name) {
        return listBangumisByName(name,1,20);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name, Integer pageNum) {
        return listBangumisByName(name,pageNum,20);
    }

    @Override
    public BangumiPageInfo listBangumisByName(String name, Integer pageNum, Integer pageSize) {
        Page<Bangumi> result = null;
        result = bangumiRepository.findBangumisByBangumiNameLike(name,PageRequest.of(pageNum-1,pageSize));
        PageInfo pageInfo = new PageInfo(pageNum,pageSize,result.getTotalElements());
        return new BangumiPageInfo(result.getContent(),pageInfo);
    }


    @Override
    public List<Bangumi> listBangumisAmbiguous(String name) {
        throw new NotImplementedException();
    }

    @Override
    public Bangumi getBangumiById(Integer id) {
        return bangumiRepository.findById(id).orElse(null);
    }

    @Override
    public Bangumi getBangumiByName(String name) {
        return bangumiRepository.findByBangumiNameEquals(name);
    }

    @Override
    public List<Bangumi> getBangumisByIds(List<Integer> ids) {
        return bangumiRepository.findAllById(ids);
    }

    @Override
    public Bangumi insertBangumi(Bangumi bangumi) {
        setCreateAndModifyTime(bangumi);
        return bangumiRepository.save(bangumi);
    }

    @Override
    public Bangumi updateBangumi(Bangumi bangumi) {
        setModifyTime(bangumi);
        return bangumiRepository.save(bangumi);
    }

    @Override
    public void deleteBangumiById(Integer id) {
        bangumiRepository.deleteById(id);
    }

    @Override
    public void deleteBangumis(List<Bangumi> bangumis) {
        bangumiRepository.deleteInBatch(bangumis);
    }

    @Override
    public Integer getEposideTotalByBangumiId(Integer bangumiId) {
        Bangumi bangumi = bangumiRepository.findById(bangumiId).orElse(null);
        return bangumi==null?null:bangumi.getEpisodeTotal();
    }

    private void setModifyTime(Bangumi bangumi){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        bangumi.setModifyTime(date);
    }

    private void setCreateAndModifyTime(Bangumi bangumi){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        bangumi.setModifyTime(date);
        bangumi.setCreateTime(date);
    }


}
