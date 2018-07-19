package com.ganzib.web.controller.api;

import com.ganzib.model.dto.Archive;
import com.ganzib.model.dto.JsonResult;
import com.ganzib.model.enums.ResponseStatus;
import com.ganzib.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2018/6/6
 */
@RestController
@RequestMapping(value = "/api/archives")
public class ApiArchivesController {

    @Autowired
    private PostService postService;

    /**
     * 根据年份归档
     *
     * @return JsonResult
     */
    @GetMapping(value = "/year")
    public JsonResult archivesYear() {
        List<Archive> archives = postService.findPostGroupByYear();
        if (null != archives || archives.size() > 0) {
            return new JsonResult(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), archives);
        } else {
            return new JsonResult(ResponseStatus.EMPTY.getCode(), ResponseStatus.EMPTY.getMsg());
        }
    }

    /**
     * 根据月份归档
     *
     * @return JsonResult
     */
    @GetMapping(value = "/year/month")
    public JsonResult archivesYearAndMonth() {
        List<Archive> archives = postService.findPostGroupByYearAndMonth();
        if (null != archives || archives.size() > 0) {
            return new JsonResult(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMsg(), archives);
        } else {
            return new JsonResult(ResponseStatus.EMPTY.getCode(), ResponseStatus.EMPTY.getMsg());
        }
    }
}
