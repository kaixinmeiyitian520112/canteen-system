package com.example.canteen.view;

import com.example.canteen.entity.User;
import com.example.canteen.service.ReportService;
import java.util.Scanner;

/**
 * 管理员控制台视图
 * 负责管理员端的交互界面
 *
 * 重构说明：
 * - 支持依赖注入，便于单元测试
 * - 使用 ReportService 替代 OrderService 进行统计
 */
public class AdminView {
    private User currentUser;
    private ReportService reportService;
    private Scanner scanner;

    /**
     * 构造函数注入依赖
     * @param user 当前用户
     * @param reportService 报表服务
     */
    public AdminView(User user, ReportService reportService) {
        this.currentUser = user;
        this.reportService = reportService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * 显示管理员主菜单
     */
    public void showMainMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║   欢迎，" + currentUser.getName() + "！          ║");
        System.out.println("║      食堂管理后台                ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /**
     * 运行管理员界面
     */
    public void run() {
        showMainMenu();

        while (true) {
            System.out.println("\n--- 管理员菜单 ---");
            System.out.println("  1. 查看订餐统计报表");
            System.out.println("  2. 退出登录");
            System.out.print("请选择 (1/2): ");

            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                reportService.printReport();
            } else if ("2".equals(choice)) {
                System.out.println("\n👋 感谢使用，再见！");
                break;
            } else {
                System.out.println("❌ 无效选择，请重新输入。");
            }
        }
    }
}

