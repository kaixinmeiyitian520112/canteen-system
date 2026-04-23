package com.example.canteen.service;

import com.example.canteen.dao.OrderDAO;
import com.example.canteen.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private OrderDAO mockOrderDAO;
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        mockOrderDAO = mock(OrderDAO.class);
        reportService = new ReportService(mockOrderDAO);
    }

    @Test
    void testGetStatisticsWithMultipleOrders() {
        // Arrange: 构造测试数据
        List<Order> orders = Arrays.asList(
                new Order("ORD001", "stu1", "张三", "D001", "红烧肉", "whole", 12.0, "2026-04-23"),
                new Order("ORD002", "stu1", "张三", "D002", "宫保鸡丁", "half", 5.0, "2026-04-23"),
                new Order("ORD003", "stu2", "李四", "D001", "红烧肉", "whole", 12.0, "2026-04-23"),
                new Order("ORD004", "stu3", "王五", "D003", "麻婆豆腐", "half", 3.0, "2026-04-23"),
                new Order("ORD005", "stu3", "王五", "D001", "红烧肉", "half", 6.0, "2026-04-23")
        );
        when(mockOrderDAO.readAllOrders()).thenReturn(orders);

        // Act
        Map<String, Object> stats = reportService.getStatistics();

        // Assert: 验证学生人数去重（stu1, stu2, stu3 → 3人）
        assertEquals(3, stats.get("totalStudents"));
        assertEquals(5, stats.get("totalOrders"));

        // 验证菜品份量统计
        @SuppressWarnings("unchecked")
        Map<String, Double> dishStats = (Map<String, Double>) stats.get("dishStats");

        // 红烧肉: stu1整份(1.0) + stu2整份(1.0) + stu3半份(0.5) = 2.5
        assertEquals(2.5, dishStats.get("红烧肉"), 0.01);

        // 宫保鸡丁: stu1半份(0.5) = 0.5
        assertEquals(0.5, dishStats.get("宫保鸡丁"), 0.01);

        // 麻婆豆腐: stu3半份(0.5) = 0.5
        assertEquals(0.5, dishStats.get("麻婆豆腐"), 0.01);
    }

    @Test
    void testGetStatisticsWithEmptyOrders() {
        // Arrange
        when(mockOrderDAO.readAllOrders()).thenReturn(new ArrayList<>());

        // Act
        Map<String, Object> stats = reportService.getStatistics();

        // Assert
        assertEquals(0, stats.get("totalStudents"));
        assertEquals(0, stats.get("totalOrders"));

        @SuppressWarnings("unchecked")
        Map<String, Double> dishStats = (Map<String, Double>) stats.get("dishStats");
        assertTrue(dishStats.isEmpty());
    }

    @Test
    void testPrintReportDoesNotThrowException() {
        // Arrange
        List<Order> orders = Arrays.asList(
                new Order("ORD001", "stu1", "张三", "D001", "红烧肉", "whole", 12.0, "2026-04-23")
        );
        when(mockOrderDAO.readAllOrders()).thenReturn(orders);

        // Act & Assert: 打印方法不应抛出异常
        assertDoesNotThrow(() -> reportService.printReport());
    }
}