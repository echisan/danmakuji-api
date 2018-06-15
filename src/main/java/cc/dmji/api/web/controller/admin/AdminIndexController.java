package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.service.UserService;
import cc.dmji.api.web.controller.BaseController;
import cc.dmji.api.web.model.admin.IndexInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by echisan on 2018/6/9
 */
@RestController
@RequestMapping("/admin/mainInfo")
public class AdminIndexController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Result> getIndexInfo(){
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setUsers(userService.countUsers());
        indexInfo.setNew_users(0L);
        indexInfo.setOnline(0L);

        return getResponseEntity(HttpStatus.OK, getSuccessResult(indexInfo));
    }
}
