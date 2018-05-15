package cc.dmji.api.service;

import cc.dmji.api.entity.Bangumi;

import java.util.List;

public interface BangumiService {

    List<Bangumi> listBangumis();

    List<Bangumi> listBangumisByName(String name);

    List<Bangumi> listBangumisAmbiguous(String name);

    Bangumi getBangumiById(Integer id);

    Bangumi insertBangumi(Bangumi bangumi);

    Bangumi updateBangumi(Bangumi bangumi);

    void deleteBangumiById(Integer id);

    Integer getEposideTotalByBangumiId(Integer bangumiId);
}
