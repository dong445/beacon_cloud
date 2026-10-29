package com.dong.controller;

import com.dong.client.SearchClient;
import com.dong.common.TableDataInfo;
import com.dong.domain.LoginUser;
import com.dong.entity.ClientBusiness;
import com.dong.service.ClientBusinessService;
import com.dong.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class SearchController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ClientBusinessService clientBusinessService;
    @Autowired
    private SearchClient searchClient;

    @GetMapping("/sys/search/list")
    public TableDataInfo list(@RequestParam Map<String, Object> map, HttpServletRequest request){
        LoginUser loginUser=tokenService.getLoginUser(request);
        if (loginUser == null){
            return TableDataInfo.of(0,new ArrayList<>());
        }
        Integer userId=loginUser.getUser().getId();
        Set<String> roles=loginUser.getPermissions();

        // 非管理员：校验 clientID 权限
        if (roles != null && !roles.contains("管理员")) {
            String clientIDStr = (String) map.get("clientID");
            List<ClientBusiness> clients = clientBusinessService.findByUserId(userId);

            if (clientIDStr == null || clientIDStr.isEmpty()) {
                // 没传公司：补上当前用户绑定的全部公司 id
                List<Long> ids = new ArrayList<>();
                for (ClientBusiness cb : clients) {
                    ids.add(cb.getId());
                }
                map.put("clientID", ids);
            } else {
                Long clientID = Long.parseLong(clientIDStr);
                boolean ok = false;
                for (ClientBusiness cb : clients) {
                    if (clientID.equals(cb.getId())) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    // 传了不属于自己的公司，直接返回空
                    return TableDataInfo.of(0, new ArrayList<>());
                }
            }
        }

        // 调 beacon-search 查 ES
        Map<String, Object> data = searchClient.findSmsByParameters(map);
        long total = Long.parseLong(String.valueOf(data.get("total")));
        List<?> rows = (List<?>) data.get("rows");
        return TableDataInfo.of(total, rows);
    }
}
