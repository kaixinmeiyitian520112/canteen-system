package com.example.canteen.dao.impl;


import com.example.canteen.dao.DishDAO;
import com.example.canteen.entity.Dish;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品数据访问实现类（文件存储）
 * 负责菜品数据的文件读写操作
 */
public class FileDishDAO implements DishDAO {
    private static final String FILE_PATH = getFilePath("dishes.txt");

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
     * 根据 ID 查找菜品
     * @param dishId 菜品 ID
     * @return 菜品对象，不存在返回 null
     */
    @Override
    public Dish findById(String dishId) {
        List<Dish> dishes = readAll();
        for (Dish dish : dishes) {
            if (dish.getId().equals(dishId)) {
                return dish;
            }
        }
        return null;
    }

    /**
     * 查找所有菜品
     * @return 菜品列表
     */
    @Override
    public List<Dish> findAll() {
        return readAll();
    }

    /**
     * 保存菜品（新增或更新）
     * @param dish 菜品对象
     * @return 是否保存成功
     */
    @Override
    public boolean save(Dish dish) {
        List<Dish> dishes = readAll();
        boolean found = false;

        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getId().equals(dish.getId())) {
                dishes.set(i, dish);
                found = true;
                break;
            }
        }

        if (!found) {
            dishes.add(dish);
        }

        return writeAll(dishes);
    }

    /**
     * 删除菜品
     * @param dishId 菜品 ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String dishId) {
        List<Dish> dishes = readAll();
        boolean removed = dishes.removeIf(d -> d.getId().equals(dishId));
        return removed && writeAll(dishes);
    }

    /**
     * 读取所有菜品数据
     * @return 菜品列表
     */
    private List<Dish> readAll() {
        List<Dish> dishes = new ArrayList<>();
        File file = new File(FILE_PATH);

        // 如果文件不存在，尝试创建默认菜品数据
        if (!file.exists()) {
            System.out.println("⚠️  菜品文件不存在，尝试创建默认菜品数据...");
            createDefaultDishes();
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
                // 支持中文逗号和英文逗号（但描述中的中文逗号不分割）
                String[] parts = line.split(",", 4);  // 最多分割成 4 部分

                if (parts.length >= 4) {
                    Dish dish = new Dish();
                    dish.setId(parts[0].trim());
                    dish.setName(parts[1].trim());
                    dish.setPrice(Double.parseDouble(parts[2].trim()));
                    dish.setDescription(parts[3].trim());
                    dishes.add(dish);
                }
            }
        } catch (IOException e) {
            System.err.println("读取菜品文件失败：" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dishes;
    }

    /**
     * 创建默认菜品数据
     */
    private void createDefaultDishes() {
        File file = new File(FILE_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("ID,名称,价格,描述");
            writer.newLine();
            writer.write("D001,红烧肉,12.00,肥而不腻,入口即化的经典菜品");
            writer.newLine();
            writer.write("D002,宫保鸡丁,10.00,鲜香微辣,口感丰富的川菜经典");
            writer.newLine();
            writer.write("D003,麻婆豆腐,6.00,麻辣鲜香,下饭神器");
            writer.newLine();
            writer.write("D004,清蒸鱼,15.00,鲜嫩多汁,营养健康");
            writer.newLine();
            writer.write("D005,炒时蔬,5.00,新鲜时令蔬菜,清淡爽口");
            writer.newLine();
            writer.flush();
            System.out.println("✅ 默认菜品数据创建成功！");
        } catch (IOException e) {
            System.err.println("创建默认菜品数据失败：" + e.getMessage());
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
     * 写入所有菜品数据（全量覆盖）
     * @param dishes 菜品列表
     * @return 是否写入成功
     */
    private boolean writeAll(List<Dish> dishes) {
        BufferedWriter writer = null;
        try {
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            writer = new BufferedWriter(new FileWriter(file));

            // 写入标题行
            writer.write("ID,名称,价格,描述");
            writer.newLine();

            // 写入菜品数据
            for (Dish dish : dishes) {
                writer.write(String.format("%s,%s,%.2f,%s",
                        dish.getId(),
                        dish.getName(),
                        dish.getPrice(),
                        dish.getDescription()));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("写入菜品文件失败：" + e.getMessage());
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
