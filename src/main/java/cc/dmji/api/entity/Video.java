package cc.dmji.api.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by echisan on 2018/5/14
 */
@Entity
@Table(name = "dm_video", schema = "dmji", catalog = "")
public class Video {
    private String videoId;
    private String vMd5;
    private Long fileSize;
    private Timestamp createTime;
    private Timestamp modifyTime;

    @Id
    @Column(name = "video_id")
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
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

}
