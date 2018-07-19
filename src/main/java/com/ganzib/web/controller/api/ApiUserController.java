package com.ganzib.web.controller.api;

import com.ganzib.model.domain.User;
import com.ganzib.model.dto.JsonResult;
import com.ganzib.model.enums.ResponseStatus;
import com.ganzib.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : RYAN0UP
 * @date : 2018/6/6
 */
@RestController
@RequestMapping(value = "/api/user")
public class ApiUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取博主信息
     *
     * @return JsonResult
     */
    @GetMapping
    public JsonResult user() {
        User user = userService.findUser();
        return new JsonResult(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), user);
    }
}
