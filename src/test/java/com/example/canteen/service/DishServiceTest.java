package com.example.canteen.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DishServiceTest {

    private DishService dishService = new DishService();

    @Test
    void testGetDishes() {
        assertNotNull(dishService.getAllDishes());
        System.out.println("✅ 菜品服务测试通过！");
    }
}