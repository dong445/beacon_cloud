package com.dong.controller;

import com.dong.common.BaseResponse;
import com.dong.domain.LoginUser;
import com.dong.entity.ClientBusiness;
import com.dong.service.ClientBusinessService;
import com.dong.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/sys/clientbusiness")
public class ClientBusinessController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ClientBusinessService clientBusinessService;

    @GetMapping("/all")
    public BaseResponse all(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return BaseResponse.error(-102, "用户未登录");
        }
        Integer userId = loginUser.getUser().getId();
        Set<String> roles = loginUser.getPermissions();

        List<ClientBusiness> list;
        if (roles != null && roles.contains("管理员")) {
            list = clientBusinessService.findAll();            // 管理员：全部
        } else {
            list = clientBusinessService.findByUserId(userId); // 普通用户：自己绑定的
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (ClientBusiness cb : list) {
            // 每次循环，都在堆内存中开辟一块新空间，创建一个全新的 Map 对象
            Map<String, Object> vo = new HashMap<>();
            vo.put("id", cb.getId());
            vo.put("corpname", cb.getCorpname());
            data.add(vo);
        }
        return BaseResponse.success(data);
    }
}
