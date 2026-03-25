package com.canteen.service;

import com.canteen.entity.Dish;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品服务类
 * 负责菜品数据管理
 */
public class DishService {
    private List<Dish> dishes;

    public DishService() {
        initDishes();
    }

    /**
     * 初始化菜品数据
     */
    private void initDishes() {
        dishes = new ArrayList<>();
        dishes.add(new Dish("D001", "红烧肉", 12.0, "肥而不腻，入口即化的经典菜品"));
        dishes.add(new Dish("D002", "宫保鸡丁", 10.0, "鲜香微辣，口感丰富的川菜经典"));
        dishes.add(new Dish("D003", "麻婆豆腐", 6.0, "麻辣鲜香，下饭神器"));
        dishes.add(new Dish("D004", "清蒸鱼", 15.0, "鲜嫩多汁，营养健康"));
        dishes.add(new Dish("D005", "炒时蔬", 5.0, "新鲜时令蔬菜，清淡爽口"));
    }

    /**
     * 获取所有菜品
     * @return 菜品列表
     */
    public List<Dish> getAllDishes() {
        return dishes;
    }

    /**
     * 根据 ID 获取菜品
     * @param dishId 菜品 ID
     * @return 菜品对象，不存在返回 null
     */
    public Dish getDishById(String dishId) {
        for (Dish dish : dishes) {
            if (dish.getId().equals(dishId)) {
                return dish;
            }
        }
        return null;
    }

    /**
     * 打印菜品列表
     */
    public void printDishes() {
        System.out.println("\n========== 明日菜品预览 ==========");
        System.out.printf("%-6s %-12s %-8s %s%n", "编号", "菜名", "价格 (整)", "描述");
        System.out.println("----------------------------------");
        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            System.out.printf("%-6s %-12s %-8.2f %s%n", 
                    (i + 1), dish.getName(), dish.getPrice(), dish.getDescription());
        }
        System.out.println("----------------------------------");
        System.out.println("💡 提示：半份价格为整份的一半");
        System.out.println("==================================\n");
    }
}
