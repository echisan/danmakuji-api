package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.repository.BangumiRepository;
import cc.dmji.api.service.BangumiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;
import java.util.List;


@Service
public class BangumiServiceImpl implements BangumiService {

    @Autowired
    private BangumiRepository bangumiRepository;

    @Override
    public List<Bangumi> listBangumis() {
        List<Bangumi> result = null;
        result = bangumiRepository.findAll();
        return result;
    }

    @Override
    public List<Bangumi> listBangumisByName(String name) {
        List<Bangumi> result = null;
        result = bangumiRepository.findBangumisByBangumiNameLike(name);
        return result;
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
