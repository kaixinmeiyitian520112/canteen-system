package com.example.canteen.service;

import com.example.canteen.dao.OrderDAO;
import com.example.canteen.entity.Dish;
import com.example.canteen.entity.Order;
import com.example.canteen.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderDAO mockOrderDAO;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        mockOrderDAO = mock(OrderDAO.class);
        orderService = new OrderService(mockOrderDAO);
    }

    @Test
    void testCreateOrderHalfPortion() {
        // Arrange
        when(mockOrderDAO.generateOrderId()).thenReturn("ORD9999");

        User user = new User("stu1", "123456", "student", "张三");
        Dish dish = new Dish("D001", "红烧肉", 12.0, "肥而不腻");

        // Act
        Order order = orderService.createOrder(user, dish, "half");

        // Assert: 价格应为整份的一半
        assertEquals(6.0, order.getPrice(), 0.01);
        assertEquals("half", order.getPortion());
        assertEquals("ORD9999", order.getOrderId());
        assertEquals("stu1", order.getUsername());
        assertEquals("张三", order.getStudentName());
        assertEquals("D001", order.getDishId());
        assertEquals("红烧肉", order.getDishName());

        // 验证日期格式正确（yyyy-MM-dd）
        assertTrue(order.getDate().matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    @Test
    void testCreateOrderWholePortion() {
        // Arrange
        when(mockOrderDAO.generateOrderId()).thenReturn("ORD8888");

        User user = new User("stu1", "123456", "student", "张三");
        Dish dish = new Dish("D002", "宫保鸡丁", 10.0, "鲜香微辣");

        // Act
        Order order = orderService.createOrder(user, dish, "whole");

        // Assert
        assertEquals(10.0, order.getPrice(), 0.01);
        assertEquals("whole", order.getPortion());
    }

    @Test
    void testSubmitOrder() {
        // Arrange
        Order order = new Order();
        order.setOrderId("ORD1234");
        when(mockOrderDAO.saveOrder(order)).thenReturn(true);

        // Act
        boolean result = orderService.submitOrder(order);

        // Assert
        assertTrue(result);
        verify(mockOrderDAO, times(1)).saveOrder(order);
    }
}