package com.demo.community.service;

import com.demo.community.dao.DiscussPostMapper;
import com.demo.community.entity.DiscussPost;
import com.demo.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter filter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //filter
        post.setTitle(filter.filter(post.getTitle()));
        post.setContent(filter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }


    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

}
