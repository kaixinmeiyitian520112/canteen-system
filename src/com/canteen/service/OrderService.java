package com.canteen.service;

import com.canteen.dao.OrderDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.Order;
import com.canteen.entity.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务类
 * 负责订单处理和统计报表
 */
public class OrderService {
    private OrderDAO orderDAO;
    private DishService dishService;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.dishService = new DishService();
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

    /**
     * 获取统计报表数据
     * @return 统计结果 Map
     */
    public Map<String, Object> getStatistics() {
        List<Order> orders = orderDAO.readAllOrders();
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Double> dishStats = new HashMap<>();  // 菜品名称 -> 总份数
        Map<String, String> uniqueStudents = new HashMap<>();  // 去重学生

        for (Order order : orders) {
            // 统计学生人数（去重）
            uniqueStudents.put(order.getUsername(), order.getStudentName());
            
            // 统计菜品份量
            String dishName = order.getDishName();
            double portion = 1.0;
            if ("half".equals(order.getPortion())) {
                portion = 0.5;
            }
            
            if (dishStats.containsKey(dishName)) {
                dishStats.put(dishName, dishStats.get(dishName) + portion);
            } else {
                dishStats.put(dishName, portion);
            }
        }

        result.put("totalStudents", uniqueStudents.size());
        result.put("dishStats", dishStats);
        result.put("totalOrders", orders.size());

        return result;
    }

    /**
     * 打印统计报表
     */
    public void printStatistics() {
        Map<String, Object> stats = getStatistics();
        
        System.out.println("\n========== 订餐统计报表 ==========");
        System.out.println("📊 统计日期：" + getTomorrowDate());
        System.out.println("----------------------------------");
        System.out.println("👥 订餐总人数：" + stats.get("totalStudents") + " 人");
        System.out.println("📝 订单总数：" + stats.get("totalOrders") + " 单");
        System.out.println("----------------------------------");
        System.out.println("🍽️  各菜品需求量：");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> dishStats = (Map<String, Double>) stats.get("dishStats");
        
        if (dishStats.isEmpty()) {
            System.out.println("  暂无订单数据");
        } else {
            for (Map.Entry<String, Double> entry : dishStats.entrySet()) {
                String dishName = entry.getKey();
                double total = entry.getValue();
                String portionText = total % 1 == 0 ? 
                        String.format("%.0f 整份", total) : 
                        String.format("%.1f 份", total);
                System.out.printf("  - %-12s: %s%n", dishName, portionText);
            }
        }
        
        System.out.println("==================================\n");
    }
}
