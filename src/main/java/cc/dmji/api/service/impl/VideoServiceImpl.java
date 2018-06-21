package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Video;
import cc.dmji.api.repository.VideoRepository;
import cc.dmji.api.service.VideoService;
import cc.dmji.api.utils.PageInfo;
import cc.dmji.api.utils.VideoPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public Video getVideoByFileSizeAndVmd5(Long fileSize, String md5) {
        return  videoRepository.findVideoByFileSizeEqualsAndVMd5Equals(fileSize,md5);
    }

    @Override
    public Video getVideoByVideoId(String videoId) {
        return videoRepository.findById(videoId).orElse(null);
    }

    @Override
    public VideoPageInfo listVideos() {
        return this.listVideos(1,20);
    }

    @Override
    public VideoPageInfo listVideos(int pageNum) {
        return this.listVideos(pageNum,20);
    }

    @Override
    public VideoPageInfo listVideos(int pageNum, int pageSize) {
        Page<Video> result = videoRepository.findAll(PageRequest.of(pageNum-1,pageSize));
        PageInfo pageInfo = new PageInfo(pageNum,pageSize,result.getTotalElements());
        return new VideoPageInfo(result.getContent(),pageInfo);
    }


    @Override
    public VideoPageInfo listVideosByEpId(Integer epId) {
        return this.listVideosByEpId(epId,1,20);
    }

    @Override
    public VideoPageInfo listVideosByEpId(Integer epId, int pageNum) {
        return this.listVideosByEpId(epId,pageNum,20);
    }

    @Override
    public VideoPageInfo listVideosByEpId(Integer epId, int pageNum, int pageSize) {
        Page<Video> result = videoRepository.findVideosByEpId(epId,PageRequest.of(pageNum-1,pageSize));
        PageInfo pageInfo = new PageInfo(pageNum,pageSize,result.getTotalElements());
        return new VideoPageInfo(result.getContent(),pageInfo);
    }

    @Override
    public List<Video> listVideosByEpIds(List<String> ids) {
        return videoRepository.findAllById(ids);
    }

    @Override
    public Video insertVideo(Video video) {
        setCreateAndModifyTime(video);
        return videoRepository.save(video);
    }

    @Override
    public Video updateVideo(Video video) {
        setModifyTime(video);
        return videoRepository.save(video);
    }

    @Override
    public void deleteVideoById(String videoId) {
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

    private void setModifyTime(Video video){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        video.setModifyTime(date);
    }

    private void setCreateAndModifyTime(Video video){
        Timestamp date = new Timestamp(System.currentTimeMillis());
        video.setModifyTime(date);
        video.setCreateTime(date);
    }
}
