package com.ganzib.web.controller.front;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.ganzib.model.domain.Category;
import com.ganzib.model.domain.Comment;
import com.ganzib.model.domain.Post;
import com.ganzib.model.domain.User;
import com.ganzib.model.dto.JsonResult;
import com.ganzib.model.enums.CommentStatus;
import com.ganzib.model.enums.PostStatus;
import com.ganzib.model.enums.PostType;
import com.ganzib.model.enums.ResultCode;
import com.ganzib.service.CommentService;
import com.ganzib.service.PostService;
import com.ganzib.service.UserService;
import com.ganzib.utils.CommentUtil;
import com.ganzib.web.controller.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2018/4/26
 */
@Slf4j
@Controller
@RequestMapping(value = "/archives")
public class FrontArchiveController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * 文章归档
     *
     * @param model model
     * @return 模板路径
     */
    @GetMapping
    public String archives(Model model) {
        return this.archives(model, 1);
    }

    /**
     * 文章归档分页
     *
     * @param model model
     * @param page  page 当前页码
     * @return 模板路径/themes/{theme}/archives
     */
    @GetMapping(value = "page/{page}")
    public String archives(Model model,
                           @PathVariable(value = "page") Integer page) {

        //所有文章数据，分页，material主题适用
        Sort sort = new Sort(Sort.Direction.DESC, "postDate");
        Pageable pageable = PageRequest.of(page - 1, 5, sort);
        Page<Post> posts = postService.findPostByStatus(PostStatus.PUBLISHED.getCode(), PostType.POST_TYPE_POST.getDesc(), pageable);
        if (null == posts) {
            return this.renderNotFound();
        }
        model.addAttribute("posts", posts);
        return this.render("archives");
    }

    /**
     * 文章归档，根据年月
     *
     * @param model model
     * @param year  year 年份
     * @param month month 月份
     * @return 模板路径/themes/{theme}/archives
     */
    @GetMapping(value = "{year}/{month}")
    public String archives(Model model,
                           @PathVariable(value = "year") String year,
                           @PathVariable(value = "month") String month) {
        Page<Post> posts = postService.findPostByYearAndMonth(year, month, null);
        if (null == posts) {
            return this.renderNotFound();
        }
        model.addAttribute("posts", posts);
        return this.render("archives");
    }

    /**
     * 渲染文章详情
     *
     * @param postUrl 文章路径名
     * @param model   model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = "{postUrl}")
    public String getPost(@PathVariable String postUrl, Model model) {
        Post post = postService.findByPostUrl(postUrl, PostType.POST_TYPE_POST.getDesc());
        if (null == post || !post.getPostStatus().equals(PostStatus.PUBLISHED.getCode())) {
            return this.renderNotFound();
        }
        //获得当前文章的发布日期
        Date postDate = post.getPostDate();
        //查询当前文章日期之前的所有文章
        List<Post> beforePosts = postService.findByPostDateBefore(postDate);
        //查询当前文章日期之后的所有文章
        List<Post> afterPosts = postService.findByPostDateAfter(postDate);

        if (null != beforePosts && beforePosts.size() > 0) {
            model.addAttribute("beforePost", beforePosts.get(beforePosts.size() - 1));
        }
        if (null != afterPosts && afterPosts.size() > 0) {
            model.addAttribute("afterPost", afterPosts.get(afterPosts.size() - 1));
        }
        List<Comment> comments = commentService.findCommentsByPostAndCommentStatus(post, CommentStatus.PUBLISHED.getCode());
        model.addAttribute("post", post);
        model.addAttribute("comments", CommentUtil.getComments(comments));
        model.addAttribute("commentsCount", comments.size());
        postService.updatePostView(post);
        return this.render("post");
    }

    @PostMapping(value = "/spider")
    @ResponseBody
    public JsonResult spiderPost(@ModelAttribute("data") String data, HttpServletRequest request) {
        if (!StringUtils.isBlank(data) && !StringUtils.isEmpty(data)){
            JSONObject postJson = JSONObject.parseObject(data);
            if (postJson.get("userId")==null){
                return new JsonResult(ResultCode.FAIL.getCode(), "缺少上传文章用户必要参数");
            }
            Long userId = postJson.getLong("userId");
            String passWord = postJson.getString("passWord");
            User user = userService.findByUserIdAndUserPass(userId,SecureUtil.md5(passWord));
            if (user==null){
                return new JsonResult(ResultCode.FAIL.getCode(), "错误");
            }
            Boolean flag = postService.spiderPost(data,user);
            if (flag){
                return new JsonResult(ResultCode.SUCCESS.getCode(), "你的评论已经提交，待博主审核之后可显示。");
            }else {
                return new JsonResult(ResultCode.FAIL.getCode(), "内容格式错误");
            }

        }
        return new JsonResult(ResultCode.FAIL.getCode(), "内容格式错误");

    }

}
