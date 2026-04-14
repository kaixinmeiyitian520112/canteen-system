package com.canteen.view;

import com.canteen.entity.Dish;
import com.canteen.entity.Order;
import com.canteen.entity.User;
import com.canteen.service.DishService;
import com.canteen.service.OrderService;
import java.util.Scanner;

/**
 * 学生控制台视图
 * 负责学生端的交互界面
 * 
 * 重构说明：
 * - 支持依赖注入，便于单元测试
 * - 移除直接 new Service 的硬编码
 */
public class StudentView {
    private User currentUser;
    private DishService dishService;
    private OrderService orderService;
    private Scanner scanner;

    /**
     * 构造函数注入依赖
     * @param user 当前用户
     * @param dishService 菜品服务
     * @param orderService 订单服务
     */
    public StudentView(User user, DishService dishService, OrderService orderService) {
        this.currentUser = user;
        this.dishService = dishService;
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * 显示学生主菜单
     */
    public void showMainMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║   欢迎，" + currentUser.getName() + "！          ║");
        System.out.println("║      校园食堂订餐系统            ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /**
     * 显示订餐选项
     */
    public void showOrderMenu() {
        dishService.printDishes();

        System.out.print("请选择菜品编号 (1-" + dishService.getAllDishes().size() + "): ");
        String dishChoice = scanner.nextLine().trim();

        int dishIndex;
        try {
            dishIndex = Integer.parseInt(dishChoice);
            if (dishIndex < 1 || dishIndex > dishService.getAllDishes().size()) {
                System.out.println("❌ 无效的选择！请输入正确的菜品编号。");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 无效输入！请输入数字。");
            return;
        }

        Dish selectedDish = dishService.getAllDishes().get(dishIndex - 1);
        System.out.println("✅ 已选择：" + selectedDish.getName() + " (整份￥" + selectedDish.getPrice() + ")");

        // 选择份量
        System.out.println("\n请选择份量：");
        System.out.println("  1. 整份");
        System.out.println("  2. 半份 (半价)");
        System.out.print("请输入选项 (1/2): ");
        String portionChoice = scanner.nextLine().trim();

        String portion;
        if ("1".equals(portionChoice)) {
            portion = "whole";
        } else if ("2".equals(portionChoice)) {
            portion = "half";
        } else {
            System.out.println("❌ 无效的选择！默认选择整份。");
            portion = "whole";
        }

        // 确认订单
        System.out.print("确认提交订单？(y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if ("y".equals(confirm) || "yes".equals(confirm)) {
            Order order = orderService.createOrder(currentUser, selectedDish, portion);
            boolean success = orderService.submitOrder(order);

            if (success) {
                System.out.println("\n✅ 订单提交成功！");
                System.out.println("   订单号：" + order.getOrderId());
                System.out.println("   菜品：" + order.getDishName());
                System.out.println("   份量：" + ("whole".equals(portion) ? "整份" : "半份"));
                System.out.println("   价格：￥" + String.format("%.2f", order.getPrice()));
                System.out.println("   预定日期：" + order.getDate());
            } else {
                System.out.println("\n❌ 订单提交失败，请稍后重试！");
            }
        } else {
            System.out.println("\n❌ 已取消订单。");
        }
    }

    /**
     * 运行学生界面
     */
    public void run() {
        showMainMenu();

        while (true) {
            System.out.println("\n--- 学生菜单 ---");
            System.out.println("  1. 查看菜品并订餐");
            System.out.println("  2. 退出登录");
            System.out.print("请选择 (1/2): ");

            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                showOrderMenu();
            } else if ("2".equals(choice)) {
                System.out.println("\n👋 感谢使用，再见！");
                break;
            } else {
                System.out.println("❌ 无效选择，请重新输入。");
            }
        }
    }
}
