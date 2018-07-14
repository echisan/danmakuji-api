package cc.dmji.api.service.impl;

import cc.dmji.api.constants.ReplyConstants;
import cc.dmji.api.entity.LikeRecord;
import cc.dmji.api.entity.Reply;
import cc.dmji.api.enums.Status;
import cc.dmji.api.repository.ReplyRepository;
import cc.dmji.api.service.LikeRecordService;
import cc.dmji.api.service.ReplyService;
import cc.dmji.api.utils.ReplyPageInfo;
import cc.dmji.api.web.model.Replies;
import cc.dmji.api.web.model.ReplyInfo;
import cc.dmji.api.web.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private LikeRecordService likeRecordService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Reply insertReply(Reply reply) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        reply.setCreateTime(timestamp);
        reply.setModifyTime(timestamp);
        return replyRepository.save(reply);
    }

    @Override
    public Reply updateReply(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public Reply deleteReply(Reply reply) {
        reply.setrStatus(Status.DELETE.name());
        return replyRepository.save(reply);
    }

    @Override
    public Long countReplyByEpId(Integer epId) {
        return replyRepository.countByEpIdEquals(epId);
    }

    @Override
    public Long countParentReplyByEpId(Integer epId) {
        return replyRepository.countByEpIdEqualsAndIsParentEqualsAndRStatusEquals(epId, (byte) 1, Status.NORMAL.name());
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
    public List<Replies> listEpisodeReplies(Integer epId, String userId, Integer pn, Integer ps) {

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
        List<String> parentReplyIds = new ArrayList<>();

        // 如果评论为空，则直接返回
        if (replyInfoList.size() == 0) {
            return repliesList;
        }

        // 记录父级评论id
        replyInfoList.forEach(replyInfo -> parentReplyIds.add(replyInfo.getReply().getReplyId()));
        // 查询点赞情况
        // 将likeRecord设置成 replyId:isLike 样式，方便后面getKey去获取状态
        Map<String, Byte> isLikeMap = new HashMap<>();
        if (userId != null) {
            List<LikeRecord> likeRecords = likeRecordService.listByReplyIdsAndUserId(parentReplyIds, userId);
            likeRecords.forEach(likeRecord -> isLikeMap.put(likeRecord.getReplyId(), likeRecord.getStatus()));
        }

        Map<String, Long> parentSonCountMap = countByReplyIds(parentReplyIds);
        List<ReplyInfo> sonReplyList = listSonReplyInfoByParentIds(parentReplyIds);

        replyInfoList.forEach(replyInfo -> {
            Replies replies = new Replies();
            if (replyInfo.getReply().getIsParent().equals(ReplyConstants.IS_PARENT)) {
                // 分配子评论
                String parentId = replyInfo.getReply().getReplyId();
                List<ReplyInfo> tempReplyInfoList = new ArrayList<>();
                for (int i = sonReplyList.size() - 1; i >= 0; i--) {
                    ReplyInfo temp = sonReplyList.get(i);
                    if (parentId.equals(temp.getReply().getParentId())) {
                        tempReplyInfoList.add(temp);
                    }
                }
                replies.setReplies(tempReplyInfoList);
                sonReplyList.removeAll(tempReplyInfoList);
                // 获取子评论总数
                Long count = parentSonCountMap.get(replyInfo.getReply().getReplyId());
                replyInfo.setTotalSize(count == null ? 0 : count);
                replyInfo.setCurPage(1);
                // 获取用户点赞状态
                if (userId != null) {
                    if (isLikeMap.containsKey(replyInfo.getReply().getReplyId())) {
                        replyInfo.setLikeStatus((byte) 1);
                    }
                }
            }
            replies.setReply(replyInfo);
            repliesList.add(replies);
        });

        return repliesList;
    }

    @Override
    public Map<String, Object> listEpisodeReplies(String userId, Integer epId, Integer pn) {

        ReplyPageInfo pageInfo = new ReplyPageInfo();
        Long totalSize = replyRepository.countByEpIdEquals(epId);
        // 设置总长度
        pageInfo.setTotalSize(totalSize);
        // 设置父级评论大小
        pageInfo.setParentTotalSize(replyRepository.countByEpIdEqualsAndIsParentEqualsAndRStatusEquals(epId, ReplyConstants.IS_PARENT, Status.NORMAL.name()));
        pageInfo.setPageNumber(pn);
        pageInfo.setPageSize(ReplyPageInfo.DEFAULT_PAGE_SIZE);

        Map<String, Object> data = new HashMap<>();
        data.put("page", pageInfo);

        data.put("replies", listEpisodeReplies(epId, userId, pn, ReplyPageInfo.DEFAULT_PAGE_SIZE));
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
            reply.setrStatus(resultSet.getString("r_status"));
            reply.setFloor(resultSet.getLong("floor"));
            userInfo.setUid(resultSet.getString("user_id"));
            userInfo.setNick(resultSet.getString("nick"));
            userInfo.setFace(resultSet.getString("face"));
            userInfo.setSex(resultSet.getString("sex"));
//            replyInfo.setLikeStatus(resultSet.getByte("like_status"));
            replyInfo.setReply(reply);
            replyInfo.setUser(userInfo);
            return replyInfo;
        }
    }

    /**
     * 默认返回10条
     *
     * @param parentId 父级评论id
     * @return 评论列表
     */
    @Override
    public List<ReplyInfo> listSonRepliesByParentId(String parentId, String userId, Integer pn, Integer ps) {
        if (StringUtils.isEmpty(parentId)) {
            return Collections.emptyList();
        }

        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where r_status='NORMAL' and parent_id = ? order by dm_reply.create_time limit ?,?";
        Integer pageLimit = pn == 1 ? 0 : (pn - 1) * ps;
        List<ReplyInfo> query = jdbcTemplate.query(sql, new ReplyInfoMapper(), parentId, pageLimit, ps);
        if (userId != null) {
            query = addReplyInfoLikeStatus(query, userId);
        }

        return query;
    }

    /**
     * 查询子评论以及分页
     *
     * @param parentId 父级评论id
     * @param pn       页数
     * @param ps       页大小
     * @return 子评论列表
     */
    @Override
    public Map<String, Object> listPageSonRepliesByParentId(String parentId, String userId, Integer pn, Integer ps) {

        if (StringUtils.isEmpty(parentId)) {
            return new HashMap<>();
        }

        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where r_status='NORMAL' and parent_id = ? order by dm_reply.create_time limit ?,?";

        Integer pageLimit = pn == 1 ? 0 : (pn - 1) * ps;
        List<ReplyInfo> replyInfos = jdbcTemplate.query(sql, new ReplyInfoMapper(), parentId, pageLimit, ps);
        if (userId != null) {
            replyInfos = addReplyInfoLikeStatus(replyInfos, userId);
        }

        Map<String, Object> data = new HashMap<>();
        ReplyPageInfo replyPageInfo = new ReplyPageInfo();
        replyPageInfo.setTotalSize(replyRepository.countByParentIdEqualsAndRStatusEquals(parentId, Status.NORMAL.name()));
        replyPageInfo.setPageNumber(pn);
        replyPageInfo.setPageSize(ps);
        replyPageInfo.setParentTotalSize(0L);
        data.put("replies", replyInfos);
        data.put("page", replyPageInfo);
        return data;
    }

    @Override
    public List<ReplyInfo> addReplyInfoLikeStatus(List<ReplyInfo> replyInfos, String userId) {
        List<ReplyInfo> list = new ArrayList<>();
        List<String> replyIds = new ArrayList<>();
        replyInfos.forEach(replyInfo -> replyIds.add(replyInfo.getReply().getReplyId()));
        List<LikeRecord> likeRecords = likeRecordService.listByReplyIdsAndUserId(replyIds, userId);
        Map<String, Byte> isLikeMap = new HashMap<>();
        likeRecords.forEach(likeRecord -> isLikeMap.put(likeRecord.getReplyId(), likeRecord.getStatus()));

        replyInfos.forEach(replyInfo -> {
            if (isLikeMap.containsKey(replyInfo.getReply().getReplyId())) {
                replyInfo.setLikeStatus((byte) 1);
            }
            list.add(replyInfo);
        });
        return list;
    }

    @Override
    public ReplyInfo getReplyInfoById(String replyId) {

        String sql = "select * from dm_reply" +
                " right join dm_user" +
                " on dm_reply.user_id = dm_user.user_id " +
                " where r_status='NORMAL' and reply_id = ?";
        return jdbcTemplate.queryForObject(sql, new ReplyInfoMapper(), replyId);
    }

    @Override
    public Long countReplysBetween(Date begin, Date end) {
        return replyRepository.countByCreateTimeBetween(begin, end);
    }

    @Override
    public List<Replies> listEpisodeRepliesByEpIdAndUserId(Integer epId, String userId, Integer pn, Integer ps) {
        return null;
    }

    @Override
    public Map<String, Long> countByReplyIds(List<String> replyIds) {

        StringBuilder sqlBuilder = new StringBuilder();
        String sql = "select parent_id reply_id,count(*) son_count from dm_reply where parent_id = '{}' and r_status = 'NORMAL' \n";
        for (int i = 0; i < replyIds.size() - 1; i++) {
            String sql1 = sql.replace("{}", replyIds.get(i));
            String sql2 = " union all \n";
            sqlBuilder.append(sql1);
            sqlBuilder.append(sql2);
        }
        String sql1 = sql.replace("{}", replyIds.get(replyIds.size() - 1));
        sqlBuilder.append(sql1);

        String finalSql = sqlBuilder.toString();

//        logger.debug("final count reply sql : \n {} ", finalSql);

        Map<String, Long> parentCountSonMap = new HashMap<>();
        jdbcTemplate.query(finalSql, rs -> {
            parentCountSonMap.put(rs.getString("reply_id"), rs.getLong("son_count"));
        });

        return parentCountSonMap;
    }

    @Override
    public Long countFloorByEpId(Integer epId) {
        return replyRepository.countByEpIdEqualsAndIsParentEqualsAndRStatusEquals(epId, ReplyConstants.IS_PARENT, Status.NORMAL.name());
    }

    @Override
    public Long countFloorBetweenByEpId(Integer epId, Long begin, Long end) {
        return replyRepository.countByEpIdEqualsAndFloorBetweenAndIsParentEqualsAndRStatusEquals(epId, begin, end, ReplyConstants.IS_PARENT, Status.NORMAL.name());
    }

    @Override
    public Long countSonRepliesByParentId(String parentId) {
        return replyRepository.countByParentIdEqualsAndRStatusEquals(parentId, Status.NORMAL.name());
    }

    @Override
    public Long countByParentIdAndCreateTimeBetween(String parentId, Date begin, Date end) {
        return replyRepository.countByParentIdEqualsAndCreateTimeBetween(parentId, begin, end);
    }

    @Override
    public Reply getLatestSonReplyByParentId(String parentId) {
        return replyRepository.getLatestSonReplyByParentId(parentId, Status.NORMAL.name());
    }

    @Override
    public Reply getFirstSonReplyByParentId(String parentId) {
        return replyRepository.getFirstSonReplyByParentId(parentId, Status.NORMAL.name());
    }

    @Override
    public Reply getLatestReplyByEpId(Integer epId) {
        return replyRepository.getLatestParentReplyByEpId(epId, Status.NORMAL.name());
    }

    @Override
    public Reply getFirstReplyByEpid(Integer epId) {
        return replyRepository.getFirstParentReplyByEpId(epId, Status.NORMAL.name());
    }

    @Override
    public Long countByEpIdAndCreateTimeBetween(Integer epId, Date begin, Date end) {
        return replyRepository.
                countByEpIdEqualsAndRStatusEqualsAndIsParentEqualsAndCreateTimeBetween(epId, Status.NORMAL.name(), ReplyConstants.IS_PARENT, begin, end);
    }

    @Override
    public List<ReplyInfo> listSonReplyInfoByParentIds(List<String> parentIds) {

        StringBuilder sqlBuilder = new StringBuilder();

        String sql1 = "select * from dm_reply \n";
        String sql2 = "right join dm_user on dm_reply.user_id = dm_user.user_id \n";
        String sql3 = "where r_status='NORMAL'and parent_id in ( ";

        sqlBuilder.append(sql1).append(sql2).append(sql3);

        for (int i = 0; i < parentIds.size() - 1; i++) {
            String id = parentIds.get(i);
            String sep = " , ";
            sqlBuilder.append("'").append(id).append("'").append(sep);
        }
        String lastId = parentIds.get(parentIds.size() - 1);
        sqlBuilder.append("'").append(lastId).append("'").append(" ) ");
        String sql4 = "order by dm_reply.create_time";
        sqlBuilder.append(sql4);
        String sql = sqlBuilder.toString();
        return jdbcTemplate.query(sql, new ReplyInfoMapper());
    }
}
