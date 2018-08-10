package cc.dmji.api.repository;

import cc.dmji.api.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findVideoByFileSizeEqualsAndVMd5Equals(Long fileSize, String vMD5);

    List<Video> findVideoByFileSizeEqualsAndVMd5Equals(Long fileSize, String vMD5, Pageable pageable);

    Video findByFileSizeEqualsAndVMd5EqualsAndIsMatchEquals(Long fileSize, String vMD5, Byte isMatch);

    Page<Video> findVideosByEpId(Long epId, Pageable pageable);

    Page<Video> findByEpIdEqualsAndIsMatchEquals(Long epId, Byte isMatch, Pageable pageable);

    Page<Video> findByIsMatchEquals(Byte isMatch,Pageable pageable);
}
