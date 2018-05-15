package cc.dmji.api.repository;

import cc.dmji.api.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, String> {

    Video findVideoByFileSizeEqualsAndVMd5Equals(Long fileSize, String vMD5);
    List<Video> findVideosByEpId(Integer epId);
}
