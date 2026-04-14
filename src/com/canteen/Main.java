package com.canteen;

import com.canteen.dao.OrderDAO;
import com.canteen.dao.UserDAO;
import com.canteen.dao.impl.FileUserDAO;
import com.canteen.dao.impl.FileDishDAO;
import com.canteen.entity.User;
import com.canteen.service.DishService;
import com.canteen.service.LoginService;
import com.canteen.service.OrderService;
import com.canteen.service.ReportService;
import com.canteen.view.AdminView;
import com.canteen.view.StudentView;

import java.util.Scanner;

/**
 * 校园食堂订餐系统 - 主程序入口
 * 版本：Sprint 2 重构版
 * 
 * 重构说明：
 * - 负责组装所有依赖（依赖注入的入口）
 * - 采用依赖倒置原则，依赖接口而非实现
 */
public class Main {
    private static LoginService loginService;
    private static Scanner scanner;

    public static void main(String[] args) {
        // ========== 依赖注入组装 ==========
        // 1. 创建 DAO 层
        UserDAO userDAO = new FileUserDAO();
        OrderDAO orderDAO = new OrderDAO();
        
        // 2. 创建服务层（注入 DAO）
        loginService = new LoginService(userDAO);
        DishService dishService = new DishService(new FileDishDAO());
        OrderService orderService = new OrderService(orderDAO);
        ReportService reportService = new ReportService(orderDAO);
        
        // 3. 创建视图层（注入服务）
        // 视图在登录时根据角色创建
        
        scanner = new Scanner(System.in);

        System.out.println("\n");
        System.out.println("  ╔════════════════════════════════════════╗");
        System.out.println("  ║                                        ║");
        System.out.println("  ║   校园食堂「光盘」行动智能推荐系统           ║");
        System.out.println("  ║        Sprint 2 - 重构版本              ║");
        System.out.println("  ║                                        ║");
        System.out.println("  ╚════════════════════════════════════════╝");

        while (true) {
            showWelcomeMenu();
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                // 用户登录
                handleLogin();
            } else if ("2".equals(choice)) {
                // 查看测试账号
                System.out.println(loginService.getTestAccounts());
            } else if ("3".equals(choice)) {
                // 退出系统
                System.out.println("\n👋 感谢使用，再见！");
                break;
            } else {
                System.out.println("\n❌ 无效选择，请重新输入。\n");
            }
        }

        scanner.close();
    }

    /**
     * 显示欢迎菜单
     */
    private static void showWelcomeMenu() {
        System.out.println("\n--- 主菜单 ---");
        System.out.println("  1. 用户登录");
        System.out.println("  2. 查看测试账号");
        System.out.println("  3. 退出系统");
        System.out.print("请选择 (1/2/3): ");
    }

    /**
     * 处理用户登录
     */
    private static void handleLogin() {
        System.out.print("\n请输入账号：");
        String username = scanner.nextLine().trim();

        System.out.print("请输入密码：");
        String password = scanner.nextLine().trim();

        User user = loginService.login(username, password);

        if (user != null) {
            // 根据角色跳转到不同界面
            if ("student".equals(user.getRole())) {
                // 创建学生视图（依赖已注入）
                StudentView studentView = new StudentView(user, 
                    new DishService(new FileDishDAO()), 
                    new OrderService(new OrderDAO()));
                studentView.run();
            } else if ("admin".equals(user.getRole())) {
                // 创建管理员视图（依赖已注入）
                AdminView adminView = new AdminView(user, 
                    new ReportService(new OrderDAO()));
                adminView.run();
            } else {
                System.out.println("❌ 未知角色，无法登录！");
            }
        }

        // 返回主菜单
        System.out.println("\n返回主菜单...");
    }
}
