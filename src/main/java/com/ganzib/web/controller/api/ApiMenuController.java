package com.ganzib.web.controller.api;

import com.ganzib.model.domain.Menu;
import com.ganzib.model.dto.JsonResult;
import com.ganzib.model.enums.ResponseStatus;
import com.ganzib.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2018/6/6
 */
@RestController
@RequestMapping(value = "/api/menus")
public class ApiMenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 获取所有菜单
     *
     * @return JsonResult
     */
    @GetMapping
    public JsonResult menus() {
        List<Menu> menus = menuService.findAllMenus();
        if (null != menus && menus.size() > 0) {
            return new JsonResult(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), menus);
        } else {
            return new JsonResult(ResponseStatus.EMPTY.getCode(), ResponseStatus.EMPTY.getMsg());
        }
    }
}
