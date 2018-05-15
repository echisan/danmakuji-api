package cc.dmji.api.service.impl;

import cc.dmji.api.entity.Video;
import cc.dmji.api.repository.VideoRepository;
import cc.dmji.api.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Video> listVideosByEpId(Integer epId) {
        return videoRepository.findVideosByEpId(epId);
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
