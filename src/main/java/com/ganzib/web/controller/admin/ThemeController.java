package com.ganzib.web.controller.admin;

import com.ganzib.model.domain.Logs;
import com.ganzib.model.dto.HaloConst;
import com.ganzib.model.dto.JsonResult;
import com.ganzib.model.dto.LogsRecord;
import com.ganzib.model.enums.BlogProperties;
import com.ganzib.model.enums.ResultCode;
import com.ganzib.service.LogsService;
import com.ganzib.service.OptionsService;
import com.ganzib.utils.HaloUtils;
import com.ganzib.web.controller.core.BaseController;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2017/12/16
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/themes")
public class ThemeController extends BaseController {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private LogsService logsService;

    /**
     * 渲染主题设置页面
     *
     * @param model model
     * @return 模板路径admin/admin_theme
     */
    @GetMapping
    public String themes(Model model) {
        model.addAttribute("activeTheme", BaseController.THEME);
        if (null != HaloConst.THEMES) {
            model.addAttribute("themes", HaloConst.THEMES);
        }
        return "admin/admin_theme";
    }

    /**
     * 激活主题
     *
     * @param siteTheme 主题名称
     * @param request   request
     * @return JsonResult
     */
    @GetMapping(value = "/set")
    @ResponseBody
    @CacheEvict(value = "posts", allEntries = true, beforeInvocation = true)
    public JsonResult activeTheme(@PathParam("siteTheme") String siteTheme,
                                  HttpServletRequest request) {
        try {
            //保存主题设置项
            optionsService.saveOption(BlogProperties.THEME.getProp(), siteTheme);
            //设置主题
            BaseController.THEME = siteTheme;
            log.info("已将主题改变为：{}", siteTheme);
            logsService.saveByLogs(
                    new Logs(LogsRecord.CHANGE_THEME, "更换为" + siteTheme, ServletUtil.getClientIP(request), DateUtil.date())
            );
            return new JsonResult(ResultCode.SUCCESS.getCode(), "主题已设置为" + siteTheme);
        } catch (Exception e) {
            log.error("主题设置失败，当前主题为：{}", siteTheme);
            return new JsonResult(ResultCode.FAIL.getCode(), "主题设置失败");
        }
    }


    /**
     * 上传主题
     *
     * @param file 文件
     * @return JsonResult
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadTheme(@RequestParam("file") MultipartFile file,
                                  HttpServletRequest request) {
        try {
            if (!file.isEmpty()) {
                //获取项目根路径
                File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
                File themePath = new File(basePath.getAbsolutePath(), new StringBuffer("templates/themes/").append(file.getOriginalFilename()).toString());
                file.transferTo(themePath);
                log.info("上传主题成功，路径：" + themePath.getAbsolutePath());
                logsService.saveByLogs(
                        new Logs(LogsRecord.UPLOAD_THEME, file.getOriginalFilename(), ServletUtil.getClientIP(request), DateUtil.date())
                );
                ZipUtil.unzip(themePath, new File(basePath.getAbsolutePath(), "templates/themes/"));
                FileUtil.del(themePath);
                HaloConst.THEMES.clear();
                HaloConst.THEMES = HaloUtils.getThemes();
            } else {
                log.error("上传主题失败，没有选择文件");
                return new JsonResult(ResultCode.FAIL.getCode(), "请选择上传的主题！");
            }
        } catch (Exception e) {
            log.error("上传主题失败：{}", e.getMessage());
            return new JsonResult(ResultCode.FAIL.getCode(), "主题上传失败！");
        }
        return new JsonResult(ResultCode.SUCCESS.getCode(), "主题上传成功！");
    }

    /**
     * 删除主题
     *
     * @param themeName 主题文件夹名
     * @return string 重定向到/admin/themes
     */
    @GetMapping(value = "/remove")
    public String removeTheme(@RequestParam("themeName") String themeName) {
        try {
            File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
            File themePath = new File(basePath.getAbsolutePath(), "templates/themes/" + themeName);
            FileUtil.del(themePath);
            HaloConst.THEMES.clear();
            HaloConst.THEMES = HaloUtils.getThemes();
        } catch (Exception e) {
            log.error("删除主题失败：{}", e.getMessage());
        }
        return "redirect:/admin/themes";
    }

    /**
     * 跳转到主题设置
     *
     * @param theme theme名称
     */
    @GetMapping(value = "/options")
    public String setting(Model model, @RequestParam("theme") String theme) {
        model.addAttribute("themeDir", theme);
        return "themes/" + theme + "/module/options";
    }

    /**
     * 编辑主题
     *
     * @param model model
     * @return 模板路径admin/admin_theme-editor
     */
    @GetMapping(value = "/editor")
    public String editor(Model model) {
        List<String> tpls = HaloUtils.getTplName(BaseController.THEME);
        model.addAttribute("tpls", tpls);
        return "admin/admin_theme-editor";
    }

    /**
     * 获取模板文件内容
     *
     * @param tplName 模板文件名
     * @return 模板内容
     */
    @GetMapping(value = "/getTpl", produces = "text/text;charset=UTF-8")
    @ResponseBody
    public String getTplContent(@RequestParam("tplName") String tplName) {
        String tplContent = "";
        try {
            //获取项目根路径
            File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
            //获取主题路径
            File themesPath = new File(basePath.getAbsolutePath(), new StringBuffer("templates/themes/").append(BaseController.THEME).append("/").append(tplName).toString());
            tplContent = HaloUtils.getFileContent(themesPath.getAbsolutePath());
        } catch (Exception e) {
            log.error("获取模板文件错误：{}", e.getMessage());
        }
        return tplContent;
    }

    /**
     * 保存修改模板
     *
     * @param tplName    模板名称
     * @param tplContent 模板内容
     * @return JsonResult
     */
    @PostMapping(value = "/editor/save")
    @ResponseBody
    public JsonResult saveTpl(@RequestParam("tplName") String tplName,
                              @RequestParam("tplContent") String tplContent) {
        if (StringUtils.isBlank(tplContent)) {
            return new JsonResult(ResultCode.FAIL.getCode(), "模板不能为空！");
        }
        try {
            //获取项目根路径
            File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
            //获取主题路径
            File tplPath = new File(basePath.getAbsolutePath(), new StringBuffer("templates/themes/").append(BaseController.THEME).append("/").append(tplName).toString());
            byte[] tplContentByte = tplContent.getBytes("UTF-8");
            Files.write(Paths.get(tplPath.getAbsolutePath()), tplContentByte);
        } catch (Exception e) {
            log.error("模板保存失败：{}", e.getMessage());
            return new JsonResult(ResultCode.FAIL.getCode(), "模板保存失败！");
        }
        return new JsonResult(ResultCode.SUCCESS.getCode(), "模板保存成功！");
    }
}
