package cc.dmji.api.web.controller;

import cc.dmji.api.annotation.UserLog;
import cc.dmji.api.common.Result;
import cc.dmji.api.common.ResultCode;
import cn.echisan.wbp4j.Entity.ImageInfo;
import cn.echisan.wbp4j.WbpUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by echisan on 2018/6/12
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {

    @Autowired
    private WbpUpload wbpUpload;

    @PostMapping
    @UserLog("上传图片")
    public ResponseEntity<Result> upload(@RequestPart MultipartFile multipartFile) throws IOException {
        ImageInfo imageInfo = wbpUpload.upload(multipartFile.getBytes());
        if (imageInfo==null){
            return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,
                    getErrorResult(ResultCode.SYSTEM_INTERNAL_ERROR,"上传失败, 稍后再试"));
        }
        return getResponseEntity(HttpStatus.OK, getSuccessResult(imageInfo, "上传成功"));
    }
}
