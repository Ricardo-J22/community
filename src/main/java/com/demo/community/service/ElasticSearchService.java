package com.demo.community.service;

import com.alibaba.fastjson.JSONObject;
import com.demo.community.dao.elasticsearch.DiscussPostRepository;
import com.demo.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticSearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    public List<DiscussPost> searchDiscussPost(String keyword, int current, int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current)// 指定从哪条开始查询
                .size(limit)// 需要查出的总记录条数
                .highlighter(highlightBuilder);//高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<DiscussPost> list = new ArrayList<>(Arrays.asList(new DiscussPost[(int) searchResponse.getHits().getTotalHits().value]));
        int i = 0;
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            DiscussPost discussPost = new DiscussPost();
            String id = hit.getSourceAsMap().get("id").toString();
            discussPost.setId(Integer.parseInt(id));

            String userId = hit.getSourceAsMap().get("userId").toString();
            discussPost.setUserId(Integer.parseInt(userId));

            String status = hit.getSourceAsMap().get("status").toString();
            discussPost.setStatus(Integer.parseInt(status));

            String createTime = hit.getSourceAsMap().get("createTime").toString();
            discussPost.setCreateTime(new Date(Long.parseLong(createTime)));

            String commentCount = hit.getSourceAsMap().get("commentCount").toString();
            discussPost.setCommentCount(Integer.parseInt(commentCount));

            String title = hit.getSourceAsMap().get("title").toString();
            discussPost.setTitle(title);

            String content = hit.getSourceAsMap().get("content").toString();
            discussPost.setContent(content);
            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
//            HighlightField timeField = hit.getHighlightFields().get("createTime");
//            if (timeField != null) {
//                discussPost.setCreateTime(new Date(Long.parseLong(timeField.getFragments()[0].toString())));
//            }
//            HighlightField statusField = hit.getHighlightFields().get("status");
//            if (statusField != null) {
//                discussPost.setStatus(Integer.parseInt(statusField.getFragments()[0].toString()));
//            }
//            HighlightField idField = hit.getHighlightFields().get("id");
//            if (idField != null) {
//                discussPost.setStatus(Integer.parseInt(idField.getFragments()[0].toString()));
//            }
//            HighlightField userIdField = hit.getHighlightFields().get("userId");
//            if (userIdField != null) {
//                discussPost.setStatus(Integer.parseInt(userIdField.getFragments()[0].toString()));
//            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            list.set(i,discussPost);
            i++;
        }
        return list;
    }
}
