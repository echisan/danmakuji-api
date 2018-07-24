package cc.dmji.api.service;

import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.utils.BangumiPageInfo;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface BangumiService {

    BangumiPageInfo listBangumis();

    BangumiPageInfo listBangumis(Integer pageNum);

    BangumiPageInfo listBangumis(Integer pageNum,Integer pageSize);

    BangumiPageInfo listBangumisByName(String name);

    BangumiPageInfo listBangumisByName(String name, Integer pageNum);

    BangumiPageInfo listBangumisByName(String name, Integer pageNum, Integer pageSize);

    List<Bangumi> listBangumisAmbiguous(String name);

    Bangumi getBangumiById(Long id);

    Bangumi getBangumiByName(String name);

    List<Bangumi> getBangumisByIds(List<Long> ids);

    Bangumi insertBangumi(Bangumi bangumi);

    Bangumi updateBangumi(Bangumi bangumi);

    void deleteBangumiById(Long id);

    void deleteBangumis(List<Bangumi> bangumis);

    Integer getEposideTotalByBangumiId(Long bangumiId);

    List<Bangumi> listBangumiByIds(List<Long> ids);

    Page<Bangumi> listBangumiOrderByViewCount(Integer pn, Integer ps);
}
