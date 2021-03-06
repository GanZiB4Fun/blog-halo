package com.ganzib.web.controller.front;

import com.ganzib.model.domain.Post;
import com.ganzib.model.dto.HaloConst;
import com.ganzib.model.enums.BlogProperties;
import com.ganzib.model.enums.PostType;
import com.ganzib.service.PostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2018/4/26
 */
@Controller
public class FrontOthersController {

    @Autowired
    private PostService postService;

    /**
     * 获取文章rss
     *
     * @return rss
     */
    @GetMapping(value = {"feed", "feed.xml", "atom", "atom.xml"}, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String feed() {
        String rssPosts = HaloConst.OPTIONS.get(BlogProperties.RSS_POSTS.getProp());
        if (StringUtils.isBlank(rssPosts)) {
            rssPosts = "20";
        }
        //获取文章列表并根据时间排序
        Sort sort = new Sort(Sort.Direction.DESC, "postDate");
        Pageable pageable = PageRequest.of(0, Integer.parseInt(rssPosts), sort);
        Page<Post> postsPage = postService.findPostByStatus(0, PostType.POST_TYPE_POST.getDesc(), pageable);
        List<Post> posts = postsPage.getContent();
        return postService.buildRss(posts);
    }

    /**
     * 获取sitemap
     *
     * @return sitemap
     */
    @GetMapping(value = {"sitemap", "sitemap.xml"}, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String siteMap() {
        //获取文章列表并根据时间排序
        Sort sort = new Sort(Sort.Direction.DESC, "postDate");
        Pageable pageable = PageRequest.of(0, 999, sort);
        Page<Post> postsPage = postService.findPostByStatus(0, PostType.POST_TYPE_POST.getDesc(), pageable);
        List<Post> posts = postsPage.getContent();
        return postService.buildSiteMap(posts);
    }
}
