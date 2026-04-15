package com.example.canteen.service;


import com.example.canteen.dao.OrderDAO;
import com.example.canteen.entity.Dish;
import com.example.canteen.entity.Order;
import com.example.canteen.entity.User;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 订单服务类
 * 负责订单创建和提交
 *
 * 重构说明：
 * - 移除统计相关方法（已拆分到 ReportService）
 * - 移除不必要的 DishService 依赖
 * - 单一职责：只负责订单创建和提交
 */
public class OrderService {
    private OrderDAO orderDAO;

    /**
     * 构造函数注入 OrderDAO
     * @param orderDAO 订单数据访问对象
     */
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    /**
     * 获取明日日期字符串
     * @return 日期字符串
     */
    public String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    /**
     * 创建订单
     * @param user 用户对象
     * @param dish 菜品对象
     * @param portion 份量 (whole/half)
     * @return 订单对象
     */
    public Order createOrder(User user, Dish dish, String portion) {
        double price = dish.getPrice();
        if ("half".equals(portion)) {
            price = dish.getPrice() / 2.0;
        }

        Order order = new Order();
        order.setOrderId(orderDAO.generateOrderId());
        order.setUsername(user.getUsername());
        order.setStudentName(user.getName());
        order.setDishId(dish.getId());
        order.setDishName(dish.getName());
        order.setPortion(portion);
        order.setPrice(price);
        order.setDate(getTomorrowDate());

        return order;
    }

    /**
     * 提交订单
     * @param order 订单对象
     * @return 是否成功
     */
    public boolean submitOrder(Order order) {
        return orderDAO.saveOrder(order);
    }
}

