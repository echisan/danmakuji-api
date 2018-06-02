package cc.dmji.api.service.impl;

import cc.dmji.api.constants.ReplyConstants;
import cc.dmji.api.entity.Reply;
import cc.dmji.api.entity.Status;
import cc.dmji.api.repository.ReplyRepository;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.utils.ReplyPageInfo;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import cc.dmji.api.web.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by echisan on 2018/5/14
 */
@Service
public class ReplyServiceImpl implements ReplyService {

    private static final Logger logger = LoggerFactory.getLogger(ReplyServiceImpl.class);

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Reply insertReply(Reply reply) {
        Reply r = new Reply();
        r.setEpId(reply.getEpId());
        r.setContent(reply.getContent());
        r.setIsParent(reply.getIsParent());
        r.setParentId(reply.getParentId());
        r.setrHate(0);
        r.setrLike(0);
        r.setrStatus(reply.getrStatus());
        r.setrPage(reply.getrPage());
        r.setUserId(reply.getUserId());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        r.setCreateTime(ts);
        r.setModifyTime(ts);
        return replyRepository.save(r);
    }

    @Override
    public void deleteReplyById(String id) {
        replyRepository.deleteById(id);
    }

    @Override
    public Long countReplyByEpId(Integer epId) {
        return replyRepository.countByEpIdEquals(epId);
    }

    @Override
    public List<Reply> listReplyByEpId(Integer epId) {
        return replyRepository.findRepliesByEpIdEquals(epId);
    }

    @Override
    public Page<Reply> listReplyByEpId(Integer epId, Integer page, Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return replyRepository.findByEpIdEquals(epId, pageable);
    }

    @Override
    public Reply getReplyById(String id) {
        return replyRepository.findById(id).orElse(null);
    }

    @Override
    public List<Replies> listEpisodeReplies(Integer epId, Integer pn, Integer ps) {

        // 获取当前页全部父级评论
        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where ep_id = ? and is_parent = 1 and r_status=? order by dm_reply.create_time desc limit ?,?";

        Integer pageLimit = pn == 1 ? 0 : (pn - 1) * ps;
        List<ReplyInfo> replyInfoList = jdbcTemplate.query(sql,
                new ReplyInfoMapper(), epId, Status.NORMAL.name(), pageLimit, ps);

//        logger.debug("reply info list: {}", replyInfoList);
        List<Replies> repliesList = new ArrayList<>();
        replyInfoList.forEach(replyInfo -> {
            Replies replies = new Replies();
            replies.setReply(replyInfo);
            if (replyInfo.getReply().getIsParent().equals(ReplyConstants.IS_PARENT)) {
                replies.setReplies(listSonRepliesByParentId(replyInfo.getReply().getReplyId()));
            }
            repliesList.add(replies);
        });

        return repliesList;
    }

    @Override
    public Map<String, Object> listEpisodeReplies(Integer epId, Integer pn) {

        ReplyPageInfo pageInfo = new ReplyPageInfo();
        Long totalSize = replyRepository.countByEpIdEquals(epId);
        // 设置总长度
        pageInfo.setTotalSize(totalSize);
        // 设置父级评论大小
        pageInfo.setParentTotalSize(replyRepository.countByEpIdAndIsParentEquals(epId,ReplyConstants.IS_PARENT));
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ReplyPageInfo.DEFAULT_PAGE_SIZE);

        Map<String, Object> data = new HashMap<>();
        data.put("page", pageInfo);
        data.put("replies", listEpisodeReplies(epId, pn, ReplyPageInfo.DEFAULT_PAGE_SIZE));
        return data;
    }

    class ReplyInfoMapper implements RowMapper<ReplyInfo> {

        @Override
        public ReplyInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            ReplyInfo replyInfo = new ReplyInfo();
            Reply reply = new Reply();
            UserInfo userInfo = new UserInfo();
            reply.setReplyId(resultSet.getString("reply_id"));
            reply.setEpId(resultSet.getInt("ep_id"));
            reply.setContent(resultSet.getString("content"));
            reply.setrPage(resultSet.getInt("r_page"));
            reply.setCreateTime(resultSet.getTimestamp("create_time"));
            reply.setModifyTime(resultSet.getTimestamp("modify_time"));
            reply.setUserId(resultSet.getString("user_id"));
            reply.setrLike(resultSet.getInt("r_like"));
            reply.setrHate(resultSet.getInt("r_hate"));
            reply.setParentId(resultSet.getString("parent_id"));
            reply.setIsParent(resultSet.getByte("is_parent"));
            reply.setrStatus("");
            userInfo.setUid(resultSet.getString("user_id"));
            userInfo.setNick(resultSet.getString("nick"));
            userInfo.setFace(resultSet.getString("face"));
            userInfo.setSex(resultSet.getString("sex"));
            replyInfo.setReply(reply);
            replyInfo.setUser(userInfo);
            return replyInfo;
        }
    }

    private List<ReplyInfo> listSonRepliesByParentId(String parentId) {
        if (StringUtils.isEmpty(parentId)){
            return Collections.emptyList();
        }

        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where r_status='NORMAL' and parent_id = ?";
        return jdbcTemplate.query(sql, new ReplyInfoMapper(), parentId);
    }

    @Override
    public ReplyInfo getReplyInfoById(String replyId) {

        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where r_status='NORMAL' and reply_id = ?";
        return jdbcTemplate.queryForObject(sql, new ReplyInfoMapper(), replyId);
    }

}
