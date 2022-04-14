package cn.xmb.hotel.constatnts;

public class MqConstatnts {
    /**
     * 交换机
     */
    public final static String HOTEL_EXCHANGE = "hotel.topic";

    /**
     * 监听插入和更新的队列
     */
    public final static String HOTEL_INSERT_OR_UPDATE_QUEUE = "hotel.insertOrUpdate.queue";

    /**
     * 监听删除的队列
     */
    public final static String HOTEL_DELETE_QUEUE = "hotel.delete.queue";

    /**
     * 插入和更新的RoutingKey
     */
    public final static String HOTEL_INSERT_OR_UPDATE_KEY = "hotel.insertOrUpdate";
    /**
     * 删除的RoutingKey
     */
    public final static String HOTEL_DELETE_KEY = "hotel.delete";
}
