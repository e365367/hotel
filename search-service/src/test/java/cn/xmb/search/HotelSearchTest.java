package cn.xmb.search;

import cn.xmb.search.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HotelSearchTest {
    private RestHighLevelClient client;

    // match_all查询所有
    @Test
    void testMatchAll() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        request.source().query(QueryBuilders.matchAllQuery());
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // match全文检索查询
    @Test
    void testMatch() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.输入要查询的字段
        request.source().query(QueryBuilders.matchQuery("all", "北京"));
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // multi_match全文检索查询
    @Test
    void testMultiMatch() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.输入要查询的字段
        request.source().query(QueryBuilders.multiMatchQuery("杭州", "city", "brand", "name"));
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // term精确查询
    @Test
    void testTerm() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.输入要精确查询的字段
        request.source().query(QueryBuilders.termQuery("city", "北京"));
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // range精确查询
    @Test
    void testRange() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.输入要查询的字段，和范围
        request.source().query(QueryBuilders.rangeQuery("price").gte(250).lte(400));
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // boolean query 复合查询
    @Test
    void testBoolean() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.创建布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 2.2.must中添加term（进行算分）
        boolQuery.must(QueryBuilders.termQuery("city", "上海"));
        // 2.3.filter中添加range(不进行算分)
        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(250).lte(400));
        // 2.4.将query传给request
        request.source().query(boolQuery);

        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // 分页和排序
    @Test
    void testPageAndSort() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.设置query
        request.source().query(QueryBuilders.rangeQuery("price").gte(250).lte(400));
        // 2.2.分页
        request.source().from(3).size(3);
        // 2.3.排序
        request.source().sort("price", SortOrder.ASC);
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    // 高亮查询
    @Test
    void testHighlight() throws IOException {
        // 1.准备request
        SearchRequest request = new SearchRequest("hotel");
        // 2.组织DSL参数
        // 2.1.设置query
        request.source().query(QueryBuilders.matchQuery("all", "华美达"));
        // 2.2.设置高亮
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
        // 3.发送请求，得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析结果
        handleResponse(response);
    }

    @Test
    void testAggregation() throws IOException {
        // 1.准备request（设置要搜索的索引库名）
        SearchRequest request = new SearchRequest("hotel");
        // 2.设置DSL参数
        // 设置size为0，不显示文档
        request.source().size(0);
        request.source().aggregation(AggregationBuilders
                // 设置聚合名称
                .terms("brand_agg")
                // 设置要聚合的字段
                .field("brand")
                // 设置显示结果条数
                .size(20));
        // 3.请求得到响应结果
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 4.解析聚合结果response
        Aggregations aggregations = response.getAggregations();
        // 4.1.根据聚合名称获得聚合结果
        Terms brandTerms = aggregations.get("brand_agg");
        // 4.2.获取所有桶（Bucket）
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        System.out.println(buckets.size());
        // 4.3.遍历每个桶
        for (Terms.Bucket bucket : buckets) {
            // 4.4.获取key，即所聚合的字段brand
            String brandName = bucket.getKeyAsString();
            System.out.printf("酒店名称：%-4s\t酒店数量：%s%n",brandName,bucket.getDocCount());
        }
    }

    // 处理响应，解析结果
    private void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.查询的总条数
        long total = searchHits.getTotalHits().value;
        System.err.println("共查询到" + total + "条数据");
        // 4.2.查询的结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 4.3.得到文档source（为json字符串格式）
            String json = hit.getSourceAsString();
            // 4.4.反序列化json字符串并打印(FastJson)
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 4.5.设置高亮

            // 4.5.1.获取高亮map
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 4.5.2.根据key：”name“获取对应的HighlightField，即高亮字段数组
            // 判断map是否存在，且不为空
            if (highlightFields != null && highlightFields.size() != 0) {
                HighlightField highlightField = highlightFields.get("name");
                // 判断高亮字段数组是否存在(如果存在，则size不为0，一定不为空，否则为null）
                if (highlightField != null) {
                    // 4.5.3.获取高亮结果数组中的第一个，即name
                    String name = highlightField.getFragments()[0].string();
                    // 4.5.4.为文档重新设置name
                    hotelDoc.setName(name);
                }
            }
            // 4.6.输出解析结果
            System.out.println(hotelDoc);
        }
    }

    // 创建client
    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.225.128:9200")
        ));
    }

    // 关闭client
    @AfterEach
    void tearDown() throws IOException {
        client.close();
    }
}
