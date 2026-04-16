package com.example.canteen.mock;

import com.example.canteen.entity.Dish;
import com.example.canteen.entity.Order;
import com.example.canteen.entity.User;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Mock 环境测试类
 * 模拟前后端分离架构中的 API 调用
 * 基于 OpenAPI 契约进行边界测试
 */
public class ApiMockTest {

    // ========== 模拟 API 响应数据 ==========

    /**
     * 模拟登录接口响应
     */
    private User mockLoginResponse(String username, String password) {
        // Mock 数据：验证用户名密码
        if ("stu1".equals(username) && "123456".equals(password)) {
            return new User("stu1", "123456", "student", "张三");
        }
        if ("admin".equals(username) && "123456".equals(password)) {
            return new User("admin", "123456", "admin", "管理员");
        }
        return null;  // 登录失败
    }

    /**
     * 模拟获取菜品列表接口响应
     */
    private List<Dish> mockGetDishesResponse() {
        return Arrays.asList(
                new Dish("D001", "红烧肉", 12.0, "肥而不腻，入口即化"),
                new Dish("D002", "宫保鸡丁", 10.0, "鲜香微辣，口感丰富"),
                new Dish("D003", "麻婆豆腐", 6.0, "麻辣鲜香，下饭神器"),
                new Dish("D004", "清蒸鱼", 15.0, "鲜嫩多汁，营养健康"),
                new Dish("D005", "炒时蔬", 5.0, "新鲜时令蔬菜，清淡爽口")
        );
    }

    /**
     * 模拟创建订单接口响应
     */
    private Order mockCreateOrderResponse(String dishId, String portion, User user) {
        // 根据菜品 ID 查找菜品
        Dish dish = mockGetDishesResponse().stream()
                .filter(d -> d.getId().equals(dishId))
                .findFirst()
                .orElse(null);

        if (dish == null) {
            return null;  // 菜品不存在
        }

        double price = "half".equals(portion) ? dish.getPrice() / 2 : dish.getPrice();

        Order order = new Order();
        order.setOrderId("ORD" + System.currentTimeMillis());
        order.setUsername(user.getUsername());
        order.setStudentName(user.getName());
        order.setDishId(dish.getId());
        order.setDishName(dish.getName());
        order.setPortion(portion);
        order.setPrice(price);
        order.setDate("2026-04-17");

        return order;
    }

    // ========== 测试用例（验证 Mock 逻辑） ==========

    @Test
    void testMockLoginSuccess() {
        System.out.println("\n========== Mock 测试1: 登录成功 ==========");

        User user = mockLoginResponse("stu1", "123456");

        assertNotNull(user);
        assertEquals("stu1", user.getUsername());
        assertEquals("student", user.getRole());
        System.out.println("✅ 登录成功: " + user.getName());
    }

    @Test
    void testMockLoginFailure() {
        System.out.println("\n========== Mock 测试2: 登录失败 ==========");

        User user = mockLoginResponse("wronguser", "wrongpass");

        assertNull(user);
        System.out.println("✅ 登录失败（预期行为）");
    }

    @Test
    void testMockGetDishes() {
        System.out.println("\n========== Mock 测试3: 获取菜品列表 ==========");

        List<Dish> dishes = mockGetDishesResponse();

        assertNotNull(dishes);
        assertEquals(5, dishes.size());
        System.out.println("✅ 获取到 " + dishes.size() + " 个菜品:");
        for (Dish dish : dishes) {
            System.out.println("   - " + dish.getName() + " ￥" + dish.getPrice());
        }
    }

    @Test
    void testMockCreateOrderWholePortion() {
        System.out.println("\n========== Mock 测试4: 创建订单（整份） ==========");

        User user = mockLoginResponse("stu1", "123456");
        Order order = mockCreateOrderResponse("D001", "whole", user);

        assertNotNull(order);
        assertEquals(12.0, order.getPrice());
        assertEquals("whole", order.getPortion());
        System.out.println("✅ 订单创建成功: " + order.getDishName() + " 整份 ￥" + order.getPrice());
    }

    @Test
    void testMockCreateOrderHalfPortion() {
        System.out.println("\n========== Mock 测试5: 创建订单（半份） ==========");

        User user = mockLoginResponse("stu1", "123456");
        Order order = mockCreateOrderResponse("D001", "half", user);

        assertNotNull(order);
        assertEquals(6.0, order.getPrice());
        assertEquals("half", order.getPortion());
        System.out.println("✅ 订单创建成功: " + order.getDishName() + " 半份 ￥" + order.getPrice());
    }

    @Test
    void testMockCreateOrderWithInvalidDish() {
        System.out.println("\n========== Mock 测试6: 无效菜品 ==========");

        User user = mockLoginResponse("stu1", "123456");
        Order order = mockCreateOrderResponse("INVALID_ID", "whole", user);

        assertNull(order);
        System.out.println("✅ 无效菜品返回 null（预期行为）");
    }

    @Test
    void testAdminLogin() {
        System.out.println("\n========== Mock 测试7: 管理员登录 ==========");

        User admin = mockLoginResponse("admin", "123456");

        assertNotNull(admin);
        assertEquals("admin", admin.getRole());
        System.out.println("✅ 管理员登录成功: " + admin.getName());
    }
}