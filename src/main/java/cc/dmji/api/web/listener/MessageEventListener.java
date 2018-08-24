package cc.dmji.api.web.listener;

import cc.dmji.api.constants.RedisKey;
import cc.dmji.api.entity.Bangumi;
import cc.dmji.api.entity.Episode;
import cc.dmji.api.entity.Notice;
import cc.dmji.api.entity.User;
import cc.dmji.api.entity.v2.MessageV2;
import cc.dmji.api.entity.v2.ReplyV2;
import cc.dmji.api.enums.MessageType;
import cc.dmji.api.enums.Status;
import cc.dmji.api.enums.v2.ReplyType;
import cc.dmji.api.service.BangumiService;
import cc.dmji.api.service.EpisodeService;
import cc.dmji.api.service.NoticeService;
import cc.dmji.api.service.UserService;
import cc.dmji.api.service.v2.MessageV2Service;
import cc.dmji.api.utils.DmjiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageEventListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageEventListener.class);
    private static final String BASE_URL = "http://localhost:8080";
    // #{【1月】戒律的复活 23【独家正版】}{"http://www.bilibili.com/bangumi/play/ep200158/?aid=25388017"}评论中@了你"
    // 1: 对象的名称 2: 评论所在的地址
    private static final String MSG_AT_TITLE = "#{ %s }{\"%s\"}评论中@了你";
    private static final String MSG_REPLY_TITLE = "#{ %s }{\"%s\"}评论中回复了你";
    private static final String MSG_LIKE_TITLE = "#{%s}{\"%s\"}";
    private static final String MSG_SYSTEM_TITLE = "#{%s}{\"%s\"}";
    private static final String MSG_COMMENT_TITLE = "#{%s}{\"%s\"}";
    // #{@e73 kljhgjh}{"http://www.bilibili.com/bangumi/play/ep200158/?aid=25388017#reply869958652"}"
    // 1: 评论内容 2: 评论描点
    private static final String MSG_CONTENT = "#{%s}{\"%s\"}";

    private static final String MSG_BANGUMI_EPISODE_URL = BASE_URL+"/#/video/%d";
    private static final String MSG_BANGUMI_EPISODE_AT_POINT = BASE_URL+"/#/video/%d?rpid=%d";
    private static final String MSG_NOTICE_URL = BASE_URL+"/#/announce/%d";
    private static final String MSG_NOTICE_AT_POINT = BASE_URL+"/#/announce/%d?rpid=%d";
    private static final String MSG_BANGUMI_URL = BASE_URL+"/#/bangumi/%d";
    private static final String MSG_BANGUMI_AT_POINT = BASE_URL+"/#/bangumi/%d?rpid=%d";
    private static final String MSG_USER_URL = BASE_URL+"/#/user/%d";
    private static final String MSG_USER_AT_POINT = BASE_URL+"/#/user/%d?rpid=%d";

    @Autowired
    private MessageV2Service messageV2Service;
    @Autowired
    private UserService userService;
    @Autowired
    private BangumiService bangumiService;
    @Autowired
    private EpisodeService episodeService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @EventListener
    @Async
    public void sendAtMessage(AtMessageEvent event) {
        ReplyV2 replyV2 = event.getReplyV2();
        List<String> nicks = event.getNicks();
        if (event.getTargetUid() != null) {
            User targetUser = userService.getUserById(event.getTargetUid());
            nicks.remove(targetUser.getNick());
        }

        if (nicks != null && nicks.size() != 0) {
            List<User> userList = userService.listUserByNickIn(nicks);
            if (userList != null && userList.size() != 0) {

                String title = createTitle(replyV2.getReplyType(), replyV2.getObjectId(), MessageType.AT);
                String content = createContent(replyV2.getReplyType(), replyV2.getContent(), replyV2.getObjectId(), replyV2.getId());
                List<MessageV2> messageV2List = new ArrayList<>();
                for (User user : userList) {
                    MessageV2 messageV2 = new MessageV2();
                    messageV2.setTitle(title);
                    messageV2.setContent(content);
                    messageV2.setType(MessageType.AT);
                    messageV2.setUid(user.getUserId());
                    messageV2.setRead(false);
                    messageV2.setPublisherUid(event.getPublisherUid());
                    messageV2.setCreateTime(new Timestamp(event.getTimestamp()));
                    messageV2.setStatus(Status.NORMAL);
                    messageV2.setSysMessageId(0L);
                    messageV2List.add(messageV2);
                }
                List<MessageV2> messageV2List1 = messageV2Service.insertAll(messageV2List);
                logger.debug("send at message:{}", messageV2List1);
            }
        }
        cleanUserMsgCountCache(event.getTargetUid());
    }

    @EventListener
    @Async
    public void sendReplyMessage(ReplyMessageEvent event) {
        ReplyV2 reply = event.getReplyV2();
        String title = createTitle(reply.getReplyType(), reply.getObjectId(), MessageType.REPLY);
        String content = createContent(reply.getReplyType(), reply.getContent(), reply.getObjectId(), reply.getId());
        MessageV2 messageV2 = new MessageV2();
        messageV2.setCreateTime(new Timestamp(event.getTimestamp()));
        messageV2.setPublisherUid(event.getPublisherUid());
        messageV2.setRead(false);
        messageV2.setUid(event.getUid());
        messageV2.setType(MessageType.REPLY);
        messageV2.setTitle(title);
        messageV2.setContent(content);
        messageV2.setStatus(Status.NORMAL);
        messageV2.setSysMessageId(0L);
        MessageV2 insert = messageV2Service.insert(messageV2);
        logger.debug("send reply message:{}", insert.getContent());
        cleanUserMsgCountCache(event.getUid());
    }

    @EventListener
    @Async
    public void sendLikeMessage(LikeMessageEvent event) {
        if (event.getTargetUid().equals(event.getPublisherUid())) {
            return;
        }
        ReplyV2 reply = event.getReplyV2();
        String title = String.format(
                MSG_LIKE_TITLE,
                DmjiUtils.formatReplyContent(reply.getContent()),
                String.format(MSG_BANGUMI_EPISODE_AT_POINT, reply.getObjectId(), reply.getId())
        );
        String content = "等" + reply.getLikeCount() + "人赞了你的回复";
        MessageV2 messageV2 = new MessageV2();
        messageV2.setCreateTime(new Timestamp(event.getTimestamp()));
        messageV2.setPublisherUid(event.getPublisherUid());
        messageV2.setRead(false);
        messageV2.setUid(event.getTargetUid());
        messageV2.setType(MessageType.LIKE);
        messageV2.setTitle(title);
        messageV2.setStatus(Status.NORMAL);
        messageV2.setContent(content);
        messageV2.setSysMessageId(0L);
        MessageV2 insert = messageV2Service.insert(messageV2);
        logger.debug("send like message:{}", insert.getContent());
        cleanUserMsgCountCache(event.getTargetUid());
    }

    @EventListener
    @Async
    public void deleteReplyAndSendMessage(DeleteReplyMessageEvent event) {
        MessageV2 messageV2 = new MessageV2();
        messageV2.setSysMessageId(0L);
        messageV2.setStatus(Status.NORMAL);
        messageV2.setUid(event.getTargetUid());
        messageV2.setPublisherUid(event.getPublisherUid());
        messageV2.setCreateTime(new Timestamp(event.getTimestamp()));
        messageV2.setType(MessageType.SYSTEM);
        messageV2.setTitle("评论删除通知");
        messageV2.setRead(false);
        String content = "尊敬的用户您好，由于您的评论【" +
                DmjiUtils.formatReplyContent(event.getDeleteReply().getContent()) +
                "】被多次举报已被删除。请遵守相关法律法规，珍惜发言机会，共同营造一个良好的讨论环境。" +
                "若有疑问请联系help@darker.online，感谢您的支持。";
        messageV2.setContent(content);
        messageV2Service.insert(messageV2);
        cleanUserMsgCountCache(event.getTargetUid());
    }


    /**
     * 由于有不同格式的标题，需要根据不同类型的对象
     * 去创建相应的标题，所以需要这个
     *
     * @param replyType   回复的类型，例如bangumi_episode,notice
     * @param oid         对象id
     * @param messageType 消息的类型，例如at,like
     * @return 带有一定格式的标题
     */
    private String createTitle(ReplyType replyType, Long oid, MessageType messageType) {
        String titleFormat;
        switch (messageType) {
            case SYSTEM:
                titleFormat = MSG_SYSTEM_TITLE;
                break;
            case AT: {
                titleFormat = MSG_AT_TITLE;
                break;
            }
            case LIKE:
                titleFormat = MSG_LIKE_TITLE;
                break;
            case REPLY: {
                titleFormat = MSG_REPLY_TITLE;
                break;
            }
            case COMMENT: {
                titleFormat = MSG_COMMENT_TITLE;
                break;
            }
            default:
                titleFormat = "%s%s";
                break;
        }

        switch (replyType) {
            case BANGUMI_EPISODE: {
                Episode episode = episodeService.getEpisodeByEpId(oid);
                logger.debug("episode:{}", episode);
                if (episode != null) {
                    Bangumi bangumi = bangumiService.getBangumiById(episode.getBangumiId());
                    logger.debug("bangumi:{}", bangumi);
                    if (bangumi != null) {
                        String name = bangumi.getBangumiName() + " " + episode.getEpIndex();
                        return String.format(titleFormat, name, String.format(MSG_BANGUMI_EPISODE_URL, oid));
                    }
                }
                return String.format(titleFormat, "未知的领域", String.format(MSG_BANGUMI_EPISODE_URL, oid));
            }
            case NOTICE: {
                Notice notice = noticeService.getNoticeById(oid);
                String title = notice == null ? "未知的公告领域" : notice.getTitle();
                return String.format(titleFormat, title, String.format(MSG_NOTICE_URL, oid));
            }
            case BANGUMI: {
                Bangumi bangumi = bangumiService.getBangumiById(oid);
                String title = bangumi == null ? "未知的番剧领域" : bangumi.getBangumiName();
                return String.format(titleFormat, title, String.format(MSG_BANGUMI_URL, oid));
            }
            case USER: {
                User user = userService.getUserById(oid);
                String title = "用户【" + user.getNick() + "】下的评论";
                return String.format(titleFormat, title, String.format(MSG_USER_URL, oid));
            }
            default: {
                break;
            }
        }
        return "";
    }

    /**
     * 根据不同类型的对象去创建相应格式的消息内容
     *
     * @param replyType 回复的类型
     * @param content   回复的正文
     * @param oid       对象id
     * @param rpId      回复id
     * @return 带有一定格式的消息正文
     */
    private String createContent(ReplyType replyType, String content, Long oid, Long rpId) {
        content = DmjiUtils.formatReplyContent(content);
        switch (replyType) {
            case NOTICE: {
                return String.format(MSG_CONTENT, content, String.format(MSG_NOTICE_AT_POINT, oid, rpId));
            }
            case BANGUMI_EPISODE: {
                return String.format(MSG_CONTENT, content, String.format(MSG_BANGUMI_EPISODE_AT_POINT, oid, rpId));
            }
            case BANGUMI: {
                return String.format(MSG_CONTENT, content, String.format(MSG_BANGUMI_AT_POINT, oid, rpId));
            }
            case USER: {
                return String.format(MSG_CONTENT, content, String.format(MSG_USER_AT_POINT, oid, rpId));
            }
            default:
                break;
        }

        return "";
    }

    /**
     * 清除用户的消息缓存
     *
     * @param uid uid
     */
    private void cleanUserMsgCountCache(Long uid) {
        Boolean delete = stringRedisTemplate.delete(RedisKey.USER_MSG_COUNT_CACHE + uid);
        if (delete) {
            logger.debug("清除用户id:{}的消息统计缓存成功", uid);
        }
    }

}