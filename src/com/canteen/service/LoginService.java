package com.canteen.service;

import com.canteen.dao.UserDAO;
import com.canteen.entity.User;

/**
 * 用户登录服务类
 * 负责用户身份验证和角色管理
 * 
 * 重构说明：
 * - 移除硬编码的 USERS Map
 * - 通过 UserDAO 接口访问用户数据
 * - 支持依赖注入，便于测试
 */
public class LoginService {
    private UserDAO userDAO;

    /**
     * 默认构造函数，使用 FileUserDAO
     */
    public LoginService() {
        this.userDAO = null; // 懒加载
    }

    /**
     * 构造函数注入 UserDAO
     * @param userDAO 用户数据访问对象
     */
    public LoginService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * 获取 UserDAO（懒加载）
     */
    private UserDAO getUserDAO() {
        if (userDAO == null) {
            // 为了向后兼容，如果没有注入 DAO，使用默认测试数据
            return new com.canteen.dao.impl.FileUserDAO();
        }
        return userDAO;
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

        UserDAO dao = getUserDAO();
        User user = dao.findByUsername(username);
        
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
     * 获取测试账号列表（从文件读取）
     * @return 账号信息字符串
     */
    public String getTestAccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== 测试账号 ==========\n");
        
        UserDAO dao = getUserDAO();
        java.util.List<User> users = dao.findAll();
        
        if (users.isEmpty()) {
            sb.append("暂无用户数据，请先创建用户文件\n");
        } else {
            for (User user : users) {
                sb.append(String.format("  账号：%s, 密码：%s (%s) - 角色：%s\n",
                        user.getUsername(),
                        user.getPassword(),
                        user.getName(),
                        user.getRole()));
            }
        }
        
        sb.append("==============================\n");
        return sb.toString();
    }
}
