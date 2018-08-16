package cc.dmji.api.service.impl;

import cc.dmji.api.entity.PostBangumi;
import cc.dmji.api.enums.Direction;
import cc.dmji.api.enums.PostBangumiOrderBy;
import cc.dmji.api.enums.PostBangumiStatus;
import cc.dmji.api.enums.Status;
import cc.dmji.api.mapper.PostBangumiMapper;
import cc.dmji.api.repository.PostBangumiRepository;
import cc.dmji.api.service.PostBangumiService;
import cc.dmji.api.web.model.admin.PostBangumiInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by echisan on 2018/7/12
 */
@Service
public class PostBangumiServiceImpl implements PostBangumiService {
    private static final Logger logger = LoggerFactory.getLogger(PostBangumiServiceImpl.class);

    @Autowired
    private PostBangumiRepository postBangumiRepository;

    @Resource
    private PostBangumiMapper postBangumiMapper;

    @Override
    @Transactional
    public PostBangumi insertPostBangumi(PostBangumi postBangumi) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        postBangumi.setCreateTime(ts);
        postBangumi.setModifyTime(ts);
        return postBangumiRepository.save(postBangumi);
    }

    @Override
    @Transactional
    public PostBangumi updatePostBangumi(PostBangumi postBangumi) {
        return postBangumiRepository.save(postBangumi);
    }

    @Override
    @Transactional
    public List<PostBangumi> insertPostBangumiList(List<PostBangumi> postBangumiList) {
        return postBangumiRepository.saveAll(postBangumiList);
    }

    @Override
    @Transactional
    public void deletePostBangumiById(Long id) {
        postBangumiRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByPostBangumiList(List<PostBangumi> postBangumiList) {
        postBangumiList.forEach(p -> {
            p.setStatus(Status.DELETE);
        });
        List<PostBangumi> postBangumis = postBangumiRepository.saveAll(postBangumiList);
        logger.debug("delete postBangumi list {}", postBangumiList);
    }

    @Override
    public Page<PostBangumi> listByUserId(Long userId, Integer pn, Integer ps, PostBangumiStatus postBangumiStatus, Status status, Sort sort) {
        PageRequest pr = PageRequest.of(pn - 1, ps, sort);
        return postBangumiRepository.findByUserIdEqualsAndStatusEqualsAndPostBangumiStatusEquals(userId, status, postBangumiStatus, pr);
    }

    @Override
    public Page<PostBangumi> listByUserId(Long userId, Integer pn, Integer ps, Status status) {
        PageRequest pr = PageRequest.of(pn - 1, ps, Sort.by(Sort.Direction.DESC, "createTime"));
        return postBangumiRepository.findByUserIdEqualsAndStatusEquals(userId, status, pr);
    }

    @Override
    public Page<PostBangumi> listByUserId(Long userId, Integer pn, Integer ps, Status status, Sort sort) {
        PageRequest pr = PageRequest.of(pn - 1, ps, sort);
        return postBangumiRepository.findByUserIdEqualsAndStatusEquals(userId, status, pr);
    }

    @Override
    public PostBangumi getById(Long id) {
        return postBangumiRepository.findById(id).orElse(null);
    }

    @Override
    public List<PostBangumi> listByBangumiName(String name) {
        return postBangumiRepository.findByBangumiNameEquals(name);
    }

    @Override
    public Page<PostBangumi> listPostBangumis(Integer pn, Integer ps, PostBangumiStatus postBangumiStatus, Status status, Sort sort) {
        PageRequest pr = PageRequest.of(pn - 1, ps, sort);
        return postBangumiRepository.findByPostBangumiStatusEqualsAndStatusEquals(postBangumiStatus, status, pr);
    }

    @Override
    public Page<PostBangumi> listPostBangumis(Integer pn, Integer ps, Status status, Sort sort) {
        return postBangumiRepository.findByStatusEquals(status, PageRequest.of(pn - 1, ps, sort));
    }

    @Override
    public Page<PostBangumi> listPostBangumis(Integer pn, Integer ps, Status status) {
        return listPostBangumis(pn, ps, status, Sort.by(Sort.Direction.DESC, "modifyTime"));
    }

    @Override
    public List<PostBangumiInfo> listPostBangumi(Status status,
                                                 PostBangumiStatus postBangumiStatus,
                                                 Timestamp beginTime, Timestamp endTime,
                                                 PostBangumiOrderBy orderBy,
                                                 Direction direction) {

        if (status == null){
            status = Status.NORMAL;
        }
        String postBangumiStatusName = null;
        if (postBangumiStatus != null){
            postBangumiStatusName = postBangumiStatus.name();
        }
        String orderByColum = null;
        String directionString = null;
        if (orderBy != null && direction != null){
            orderByColum = orderBy.getColum();
            directionString = direction.name();
        }

        return postBangumiMapper.listPostBangumi(status.name(),
                postBangumiStatusName, beginTime, endTime, orderByColum, directionString);
    }

    @Override
    public Page<PostBangumi> listPostBangumiByBangumiName(Long userId, String bangumiName,
                                                              Integer pn, Integer ps,
                                                              PostBangumiStatus postBangumiStatus, Status status, Sort sort) {
        String queryBangumiName = "%"+bangumiName+"%";
        return postBangumiRepository
                .findByUserIdEqualsAndStatusEqualsAndPostBangumiStatusEqualsAndBangumiNameLike(userId,
                        status,postBangumiStatus,queryBangumiName,PageRequest.of(pn-1,ps,sort));
    }

    @Override
    public Page<PostBangumi> listByBangumiName(Long userId, Integer pn, Integer ps, String bangumiName, Status status) {
        String queryBangumiName = "%"+bangumiName+"%";
        return postBangumiRepository.findByUserIdEqualsAndStatusEqualsAndBangumiNameLike(
                userId,status,queryBangumiName,PageRequest.of(pn-1,ps,Sort.by(Sort.Direction.DESC, "createTime")));
    }
}
