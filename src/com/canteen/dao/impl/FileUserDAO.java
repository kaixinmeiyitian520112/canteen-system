package com.canteen.dao.impl;

import com.canteen.dao.UserDAO;
import com.canteen.entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类（文件存储）
 * 负责用户数据的文件读写操作
 */
public class FileUserDAO implements UserDAO {
    private static final String FILE_PATH = getFilePath("users.txt");
    
    /**
     * 获取用户主目录路径（兼容不同运行环境）
     */
    private static String getUserHomePath() {
        String userDir = System.getProperty("user.dir");
        return userDir.endsWith(File.separator) ? userDir : userDir + File.separator;
    }
    
    /**
     * 获取文件路径
     */
    private static String getFilePath(String fileName) {
        return getUserHomePath() + "data" + File.separator + fileName;
    }

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，不存在返回 null
     */
    @Override
    public User findByUsername(String username) {
        List<User> users = readAll();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 查找所有用户
     * @return 用户列表
     */
    @Override
    public List<User> findAll() {
        return readAll();
    }

    /**
     * 保存用户（新增或更新）
     * @param user 用户对象
     * @return 是否保存成功
     */
    @Override
    public boolean save(User user) {
        List<User> users = readAll();
        boolean found = false;
        
        // 更新已存在的用户
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        
        // 添加新用户
        if (!found) {
            users.add(user);
        }
        
        return writeAll(users);
    }

    /**
     * 删除用户
     * @param username 用户名
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String username) {
        List<User> users = readAll();
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        return removed && writeAll(users);
    }

    /**
     * 读取所有用户数据
     * @return 用户列表
     */
    private List<User> readAll() {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        // 如果文件不存在，尝试创建默认用户数据
        if (!file.exists()) {
            createDefaultUsers();
            return readAll(); // 重新读取
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            // 跳过标题行
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // 支持中文逗号和英文逗号
                String[] parts = line.split("[,，]");
                
                if (parts.length >= 4) {
                    User user = new User();
                    user.setUsername(parts[0].trim());
                    user.setPassword(parts[1].trim());
                    user.setRole(parts[2].trim());
                    user.setName(parts[3].trim());
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("读取用户文件失败：" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return users;
    }
    
    /**
     * 创建默认用户数据
     */
    private void createDefaultUsers() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("账号，密码，角色，姓名");
            writer.newLine();
            writer.write("stu1,123456,student，张三");
            writer.newLine();
            writer.write("stu2,123456,student，李四");
            writer.newLine();
            writer.write("stu3,123456,student，王五");
            writer.newLine();
            writer.write("admin,123456,admin，管理员");
            writer.newLine();
            writer.flush();
            System.out.println("✅ 默认用户数据创建成功！");
        } catch (IOException e) {
            System.err.println("创建默认用户数据失败：" + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写入所有用户数据（全量覆盖）
     * @param users 用户列表
     * @return 是否写入成功
     */
    private boolean writeAll(List<User> users) {
        BufferedWriter writer = null;
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            writer = new BufferedWriter(new FileWriter(file));

            // 写入标题行
            writer.write("账号，密码，角色，姓名");
            writer.newLine();

            // 写入用户数据
            for (User user : users) {
                writer.write(String.format("%s,%s,%s,%s",
                        user.getUsername(),
                        user.getPassword(),
                        user.getRole(),
                        user.getName()));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("写入用户文件失败：" + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
