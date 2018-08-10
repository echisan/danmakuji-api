package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Video;
import cc.dmji.api.repository.VideoRepository;
import cc.dmji.api.service.VideoService;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.utils.VideoPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public List<Video> getVideoByFileSizeAndVmd5(Long fileSize, String md5) {
        return videoRepository.findVideoByFileSizeEqualsAndVMd5Equals(fileSize, md5);
    }

    @Override
    public List<Video> listVideoByFileSizeAndVmd5SortByScore(Long fileSize, String md5) {
        Sort sort = Sort.by(Sort.Direction.DESC,"score");
        return videoRepository.findVideoByFileSizeEqualsAndVMd5Equals(fileSize, md5,PageRequest.of(0,10,sort));
    }

    @Override
    public Video getMatchVideoByFileSizeAndMd5(Long fileSize, String md5) {
        return videoRepository.findByFileSizeEqualsAndVMd5EqualsAndIsMatchEquals(fileSize, md5, (byte)1);
    }

    @Override
    public Video getVideoByVideoId(Long videoId) {
        return videoRepository.findById(videoId).orElse(null);
    }

    @Override
    public VideoPageInfo listVideos() {
        return this.listVideos(1, 20);
    }

    @Override
    public VideoPageInfo listVideos(int pageNum) {
        return this.listVideos(pageNum, 20);
    }

    @Override
    public VideoPageInfo listVideos(int pageNum, int pageSize) {
        Page<Video> result = videoRepository.findAll(PageRequest.of(pageNum - 1, pageSize));
        PageInfo pageInfo = new PageInfo(pageNum, pageSize, result.getTotalElements());
        return new VideoPageInfo(result.getContent(), pageInfo);
    }


    @Override
    public VideoPageInfo listVideosByEpId(Long epId) {
        return this.listVideosByEpId(epId, 1, 20);
    }

    @Override
    public VideoPageInfo listVideosByEpId(Long epId, int pageNum) {
        return this.listVideosByEpId(epId, pageNum, 20);
    }

    @Override
    public VideoPageInfo listVideosByEpId(Long epId, int pageNum, int pageSize) {
        Page<Video> result = videoRepository.findVideosByEpId(epId, PageRequest.of(pageNum - 1, pageSize));
        PageInfo pageInfo = new PageInfo(pageNum, pageSize, result.getTotalElements());
        return new VideoPageInfo(result.getContent(), pageInfo);
    }

    @Override
    public List<Video> listVideosByEpIds(List<Long> ids) {
        return videoRepository.findAllById(ids);
    }

    @Override
    public Video insertVideo(Video video) {
        setCreateAndModifyTime(video);
        video.setIsMatch((byte) 0);
        video.setScore(0);
        return videoRepository.save(video);
    }

    @Override
    public Video updateVideo(Video video) {
        setModifyTime(video);
        return videoRepository.save(video);
    }

    @Override
    public void deleteVideoById(Long videoId) {
        videoRepository.deleteById(videoId);
    }

    @Override
    public void deleteVideos(List<Video> videos) {
        videoRepository.deleteInBatch(videos);
    }

    @Override
    public Long countVideo() {
        return videoRepository.count();
    }

    @Override
    public VideoPageInfo listVideoByEpIdAndIsMatch(Long epId, Byte isMatch, int pn, int ps) {
        PageRequest pageRequest = PageRequest.of(pn - 1, ps, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Video> videoPage = videoRepository.findByEpIdEqualsAndIsMatchEquals(epId, isMatch, pageRequest);
        VideoPageInfo vpi = new VideoPageInfo();
        vpi.setVideos(videoPage.getContent());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(ps);
        pageInfo.setPageNumber(pn);
        pageInfo.setTotalSize(videoPage.getTotalElements());
        vpi.setPageInfo(pageInfo);
        return vpi;
    }

    @Override
    public VideoPageInfo listVideoByIsMatch(Byte isMatch, int pn, int ps) {
        PageRequest pageRequest = PageRequest.of(pn - 1, ps, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Video> videoPage = videoRepository.findByIsMatchEquals(isMatch, pageRequest);
        VideoPageInfo vpi = new VideoPageInfo();
        vpi.setVideos(videoPage.getContent());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageSize(ps);
        pageInfo.setPageNumber(pn);
        pageInfo.setTotalSize(videoPage.getTotalElements());
        vpi.setPageInfo(pageInfo);
        return vpi;
    }

    private void setModifyTime(Video video) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        video.setModifyTime(date);
    }

    private void setCreateAndModifyTime(Video video) {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        video.setModifyTime(date);
        video.setCreateTime(date);
    }
}
