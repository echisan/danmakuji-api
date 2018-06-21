package cc.dmji.api.web.controller;

import cc.dmji.api.common.Result;

import cn.echisan.wbp4j.Entity.ImageInfo;
import cn.echisan.wbp4j.WbpUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by echisan on 2018/6/12
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController{

    private static final String REQUEST_KEY_B64_PIC = "b64_pic";

    @Autowired
    private WbpUpload wbpUpload;

    @PostMapping
    public ResponseEntity<Result> upload(@RequestPart MultipartFile file) throws IOException {

        ImageInfo imageInfo = null;
        imageInfo = wbpUpload.upload(file.getBytes());
        return getResponseEntity(HttpStatus.OK,getSuccessResult(imageInfo,"上传成功"));
    }
}
