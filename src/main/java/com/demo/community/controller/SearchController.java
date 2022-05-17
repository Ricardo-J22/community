package com.demo.community.controller;

import com.demo.community.entity.DiscussPost;
import com.demo.community.entity.Page;
import com.demo.community.service.ElasticSearchService;
import com.demo.community.service.LikeService;
import com.demo.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.demo.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Controller
public class SearchController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) throws IOException {
        List<DiscussPost> posts = elasticSearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(posts != null){
            for(DiscussPost post: posts){
                if(post == null){
                    break;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        page.setPath("/search?keyword="+ keyword);
        page.setRows(posts == null ? 0 : posts.size());

        return "/site/search";
    }

}
