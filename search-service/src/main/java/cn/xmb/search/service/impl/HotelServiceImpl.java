package cn.xmb.search.service.impl;

import cn.xmb.feign.clients.AdminClient;
import cn.xmb.feign.pojo.Hotel;
import cn.xmb.search.mapper.HotelMapper;
import cn.xmb.search.pojo.HotelDoc;
import cn.xmb.search.pojo.PageResult;
import cn.xmb.search.pojo.RequestParams;
import cn.xmb.search.service.HotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {
    /**
     * 注入RestHighLevelClient对象
     */
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private AdminClient adminClient;

    /**
     * 按条件过滤搜索
     *
     * @param requestParams 搜索参数
     * @return 返回搜索结果
     */
    @Override
    public PageResult search(RequestParams requestParams) {
        try {
            // 1.准备request
            SearchRequest request = new SearchRequest("hotel");
            // 2.准备DSL参数
            // 2.1.构建query
            buildBasicQuery(requestParams, request);
            // 2.2.设置分页参数
            int page = requestParams.getPage();
            int size = requestParams.getSize();
            request.source().from((page - 1) * size).size(size);
            // 2.3.价格排序(升序）
//            request.source().sort("price", SortOrder.ASC);
            // 2.4.距离排序（判断存在后，由近到远）
            if (requestParams.getLocation() != null && !"".equals(requestParams.getLocation())) {
                System.out.println(requestParams.getLocation());
                // 根据location排序，升序，单位为km
                request.source().sort(SortBuilders.geoDistanceSort("location", new GeoPoint(requestParams.getLocation())).order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS));
            }

            // 3.发送请求，得到响应结果
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析响应结果
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过聚合索引库得到搜索页面的品牌，城市等信息
     *
     * @return 聚合结果，格式：{"城市": ["上海", "北京"], "品牌": ["如家", "希尔顿"]}
     */
    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        try {
            // 1.准备request，设置要搜索的索引库名
            SearchRequest request = new SearchRequest("hotel");
            // 2.设置DSL参数
            // 2.1.构建query，并将构建的query放入request中
            buildBasicQuery(params, request);
            // 2.2.设置查显示文档数量size
            request.source().size(0);
            // 2.3.建立聚合查询
            buildAggregation(request);
            // 3.发起请求，得到响应结果response
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析结果response
            Map<String, List<String>> map = new HashMap<>();
            Aggregations aggregations = response.getAggregations();
            // 4.1.根据品牌名称，获取品牌结果
            List<String> brandList = getAggregationByName(aggregations, "brandAggregation");
            map.put("brand", brandList);
            // 4.1.根据城市名称，获取城市结果
            List<String> cityList = getAggregationByName(aggregations, "cityAggregation");
            map.put("city", cityList);
            // 4.1.根据星级名称，获取星级结果
            List<String> starList = getAggregationByName(aggregations, "starAggregation");
            map.put("starName", starList);

            System.out.println(map);
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> suggestion(String prefix) {
        List<String> suggestionList = null;
        try {
            // 1.准备request
            SearchRequest request = new SearchRequest("hotel");
            // 2.准备DSL
            request.source().suggest(new SuggestBuilder()
                    .addSuggestion("mySuggestion",
                            SuggestBuilders
                                    .completionSuggestion("suggestion")
                                    .prefix(prefix)
                                    .skipDuplicates(true)
                                    .size(10)
                    ));
            // 3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.获取结果
            Suggest suggest = response.getSuggest();
            // 4.1.根据名称获取补全结果
            CompletionSuggestion suggestion = suggest.getSuggestion("mySuggestion");
            // 4.2.获取options，并遍历options
            suggestionList = new ArrayList<>(suggestion.getOptions().size());
            for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
                // 4.3.获取一个option中的text，即所补全的词条
                String text = option.getText().toString();

                suggestionList.add(text);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return suggestionList;
    }

    /**
     * 根据id插入或更新文档
     *
     * @param id id
     */
    @Override
    public void insertOrUpdateById(Long id) {
        try {
            // 1.通过feign调用adminservice
            Hotel hotel = adminClient.queryById(id);
            System.out.println("调用adminClient");
            // 2.由hotel获得hotelDoc
            HotelDoc hotelDoc = new HotelDoc(hotel);
            // 3.对象转换为json字符串
            String jsonString = JSON.toJSONString(hotelDoc);

            // 1.准备Request
            IndexRequest request = new IndexRequest("hotel").id(id.toString());
            // 2.准备请求参数DSL，其实就是文档的JSON字符串
            request.source(jsonString, XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据id删除document
     *
     * @param id id
     */
    @Override
    public void deleteById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取聚合结果
     *
     * @param aggregations    所有聚合的结果的集合
     * @param aggregationName 要获取的聚合结果的聚合名字
     * @return 返回聚合结果List
     */
    private List<String> getAggregationByName(Aggregations aggregations, String aggregationName) {
        // 4.1.获取聚合结果
        Terms brandTerm = aggregations.get(aggregationName);
        // 4.2.获取所有桶（Bucket）
        List<? extends Terms.Bucket> buckets = brandTerm.getBuckets();
        // 4.3.遍历每个桶
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 获取key，即所聚合的字段
            String key = bucket.getKeyAsString();
            list.add(key);
        }
        return list;
    }

    /**
     * 建立聚合查询
     */
    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders.terms("brandAggregation").field("brand").size(100));
        request.source().aggregation(AggregationBuilders.terms("cityAggregation").field("city").size(100));
        request.source().aggregation(AggregationBuilders.terms("starAggregation").field("starName").size(100));
    }


    /**
     * 构建query,判断params中各参数的情况
     */
    private void buildBasicQuery(RequestParams requestParams, SearchRequest request) {
        // 1.创建boolean query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 判断key
        if (requestParams.getKey() != null && !"".equals(requestParams.getKey())) {
            boolQuery.must(QueryBuilders.matchQuery("all", requestParams.getKey()));
        }
        // 判断city
        if (requestParams.getCity() != null && !"".equals(requestParams.getCity())) {
            boolQuery.must(QueryBuilders.termQuery("city", requestParams.getCity()));
        }
        // 判断brand
        if (requestParams.getBrand() != null && !"".equals(requestParams.getBrand())) {
            boolQuery.must(QueryBuilders.termQuery("brand", requestParams.getBrand()));
        }
        // 判断starName
        if (requestParams.getStarName() != null && !"".equals(requestParams.getStarName())) {
            boolQuery.must(QueryBuilders.termQuery("starName", requestParams.getStarName()));
        }
        // price范围过滤
        if (requestParams.getMinPrice() != null && requestParams.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(requestParams.getMinPrice()).lte(requestParams.getMaxPrice()));
        }
        // 2.算分控制
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                // 原始查询
                boolQuery,
                // function score 的数组
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        // 其中的一个function score
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                // 过滤条件
                                QueryBuilders.termQuery("isAD", "true"),
                                // 算分函数
                                ScoreFunctionBuilders.weightFactorFunction(5))});

        // 将query设置到request中
        request.source().query(functionScoreQuery);
    }

    // 处理响应，解析结果
    private PageResult handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1.查询的总条数
        long total = searchHits.getTotalHits().value;
        System.err.println("共查询到" + total + "条数据");
        // 4.2.查询的结果数组
        SearchHit[] hits = searchHits.getHits();

        // 4.3.遍历查询得到的结果数组
        // 新建集合
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 4.3.1.得到文档source（为json字符串格式）
            String json = hit.getSourceAsString();
            // 4.3.2.反序列化json字符串，得到对象
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 4.3.3 获取排序值
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object sortValue = sortValues[0];
                hotelDoc.setDistance(sortValue);
            }
            // 将对象添加到集合中
            hotels.add(hotelDoc);
        }
        return new PageResult(total, hotels);
    }
}
