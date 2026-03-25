package com.canteen.service;

import com.canteen.entity.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录服务类
 * 负责用户身份验证和角色管理
 */
public class LoginService {
    // 硬编码测试账号
    private static final Map<String, User> USERS = new HashMap<>();

    static {
        // 学生账号
        USERS.put("stu1", new User("stu1", "123456", "student", "张三"));
        USERS.put("stu2", new User("stu2", "123456", "student", "李四"));
        USERS.put("stu3", new User("stu3", "123456", "student", "王五"));
        
        // 管理员账号
        USERS.put("admin", new User("admin", "123456", "admin", "管理员"));
    }

    /**
     * 用户登录验证
     * @param username 账号
     * @param password 密码
     * @return 登录成功的用户对象，失败返回 null
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ 账号不能为空！");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ 密码不能为空！");
            return null;
        }

        User user = USERS.get(username.trim());
        if (user == null) {
            System.out.println("❌ 账号不存在！");
            return null;
        }

        if (!user.getPassword().equals(password.trim())) {
            System.out.println("❌ 密码错误！");
            return null;
        }

        System.out.println("✅ 登录成功！欢迎，" + user.getName());
        return user;
    }

    /**
     * 获取测试账号列表
     * @return 账号信息字符串
     */
    public String getTestAccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== 测试账号 ==========\n");
        sb.append("学生账号：\n");
        sb.append("  账号：stu1, 密码：123456 (张三)\n");
        sb.append("  账号：stu2, 密码：123456 (李四)\n");
        sb.append("  账号：stu3, 密码：123456 (王五)\n");
        sb.append("管理员账号：\n");
        sb.append("  账号：admin, 密码：123456\n");
        sb.append("==============================\n");
        return sb.toString();
    }
}
