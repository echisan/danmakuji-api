package cc.dmji.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_video", schema = "dmji", catalog = "")
public class Video {
    private Long videoId;
    private String vMd5;
    private Long fileSize;
    private Timestamp createTime;
    private Timestamp modifyTime;
    private Long epId;
    @Column(name = "is_match")
    private Byte isMatch;
    private Integer score;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    @Basic
    @Column(name = "v_md5")
    public String getvMd5() {
        return vMd5;
    }

    public void setvMd5(String vMd5) {
        this.vMd5 = vMd5;
    }

    @Basic
    @Column(name = "file_size")
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "modify_time")
    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Basic
    @Column(name = "ep_id")
    public Long getEpId() {
        return epId;
    }

    public void setEpId(Long epId) {
        this.epId = epId;
    }

    public Byte getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(Byte isMatch) {
        this.isMatch = isMatch;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", vMd5='" + vMd5 + '\'' +
                ", fileSize=" + fileSize +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", epId=" + epId +
                ", isMatch=" + isMatch +
                ", score=" + score +
                '}';
    }
}
