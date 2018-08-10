package cc.dmji.api.web.controller.admin;

import cc.dmji.api.common.Result;
import cc.dmji.api.enums.Role;
import cc.dmji.api.web.controller.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by echisan on 2018/6/9
 */
@RestController
@RequestMapping("/admin/roles")
public class AdminRoleController extends BaseController {

    @GetMapping
    public ResponseEntity<Result> listRoles(){
        Role[] roles = Role.values();
        Map<String,String> data = new HashMap<>();
        for (Role r :
                roles) {
            data.put(r.name(), r.getName());
        }
        return getResponseEntity(HttpStatus.OK, getSuccessResult(data));
    }
}
