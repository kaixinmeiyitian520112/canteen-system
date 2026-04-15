<%--
  Created by IntelliJ IDEA.
  User: admin‘
  Date: 2026/4/15
  Time: 13:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>管理员统计 - 校园食堂智能推荐系统</title>
</head>
<body>
    <script>
        /**
         * 管理员统计页面逻辑
         * 基于 Mock API (Prism) 实现逻辑闭环与边界测试
         */

// 配置
        const API_BASE_URL = 'http://localhost:4010/api/v1';
        let dishStatistics = [];
        let dishChart = null;
        let portionChart = null;

        // 页面加载完成后初始化
        document.addEventListener('DOMContentLoaded', async () => {
            console.log('[初始化] 管理员统计页面加载完成');

            // 检查登录状态
            const savedUser = localStorage.getItem('currentUser');
            if (!savedUser) {
                console.log('[登录] 未登录，跳转到登录页');
                window.location.href = 'login.html';
                return;
            }

            const currentUser = JSON.parse(savedUser);
            if (currentUser.role !== 'admin') {
                alert('无权访问管理员页面');
                window.location.href = 'student-order.html';
                return;
            }

            document.getElementById('userInfo').textContent = `管理员：${currentUser.name}`;

            // 设置默认日期为明天
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('dateSelector').value = formatDate(tomorrow);

            // 加载统计数据
            await loadStatistics();
        });

        /**
         * 格式化日期
         */
        function formatDate(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        }

        /**
         * 加载统计数据
         */
        async function loadStatistics() {
            const loadingEl = document.getElementById('loading');
            const contentEl = document.getElementById('statisticsContent');
            const date = document.getElementById('dateSelector').value;

            loadingEl.style.display = 'block';
            contentEl.style.display = 'none';

            try {
                console.log(`[请求] GET /admin/statistics/orders?date=${date}`);

                // 尝试从 Mock API 获取数据，设置 3 秒超时
                const controller = new AbortController();
                const timeoutId = setTimeout(() => controller.abort(), 3000);

                const response = await fetch(`${API_BASE_URL}/admin/statistics/orders?date=${date}`, {
                    headers: { 'Authorization': 'Bearer mock-admin-token' },
                    signal: controller.signal
                });
                clearTimeout(timeoutId);

                if (response.ok) {
                    const result = await response.json();
                    const data = result.data || result;

                    console.log('[响应] 统计数据加载成功', data);

                    // 更新界面
                    updateOverview(data);
                    updateCharts(data.dishStatistics || []);
                    updateTable(data.dishStatistics || []);

                    // 切换显示状态
                    loadingEl.style.display = 'none';
                    contentEl.style.display = 'block';
                } else {
                    throw new Error('获取统计数据失败');
                }
            } catch (error) {
                console.warn('[警告] Mock 服务未响应，使用模拟数据', error.message);
                // 自动使用模拟数据
                loadMockStatistics();
            }
        }

        /**
         * 更新概览卡片
         */
        function updateOverview(data) {
            document.getElementById('totalOrders').textContent = data.totalOrders || 0;
            document.getElementById('totalUsers').textContent = data.totalUsers || 0;
            document.getElementById('totalDishes').textContent = (data.dishStatistics || []).length;
        }

        /**
         * 更新图表
         */
        function updateCharts(statistics) {
            dishStatistics = statistics;

            // 计算总量
            let totalWhole = 0;
            let totalHalf = 0;

            statistics.forEach(stat => {
                totalWhole += stat.wholePortion || 0;
                totalHalf += stat.halfPortion || 0;
            });

            // 菜品需求量柱状图
            const dishCtx = document.getElementById('dishChart').getContext('2d');

            if (dishChart) {
                dishChart.destroy();
            }

            dishChart = new Chart(dishCtx, {
                type: 'bar',
                data: {
                    labels: statistics.map(s => s.dishName),
                    datasets: [{
                        label: '总需求量',
                        data: statistics.map(s => s.totalQuantity),
                        backgroundColor: 'rgba(13, 110, 253, 0.7)',
                        borderColor: 'rgba(13, 110, 253, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return `需求量: ${context.parsed.y} 份`;
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { stepSize: 1 }
                        }
                    }
                }
            });

            // 份量分布饼图
            const portionCtx = document.getElementById('portionChart').getContext('2d');

            if (portionChart) {
                portionChart.destroy();
            }

            portionChart = new Chart(portionCtx, {
                type: 'doughnut',
                data: {
                    labels: ['整份', '半份'],
                    datasets: [{
                        data: [totalWhole, totalHalf],
                        backgroundColor: [
                            'rgba(13, 110, 253, 0.7)',
                            'rgba(25, 135, 84, 0.7)'
                        ],
                        borderColor: [
                            'rgba(13, 110, 253, 1)',
                            'rgba(25, 135, 84, 1)'
                        ],
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                    const percentage = ((context.parsed / total) * 100).toFixed(1);
                                    return `${context.label}: ${context.parsed} 份 (${percentage}%)`;
                                }
                            }
                        }
                    }
                }
            });
        }

        /**
         * 更新表格
         */
        function updateTable(statistics) {
            const tbody = document.getElementById('dishTableBody');
            tbody.innerHTML = '';

            const totalQuantity = statistics.reduce((sum, stat) => sum + stat.totalQuantity, 0);

            statistics.forEach(stat => {
                const percentage = totalQuantity > 0 ? ((stat.totalQuantity / totalQuantity) * 100).toFixed(1) : 0;

                const row = `
      <tr>
        <td><code>${stat.dishId}</code></td>
        <td><strong>${stat.dishName}</strong></td>
        <td><span class="badge bg-primary">${stat.totalQuantity}</span></td>
        <td>${stat.wholePortion || 0}</td>
        <td>${stat.halfPortion || 0}</td>
        <td>
          <div class="progress" style="height: 20px;">
            <div class="progress-bar" role="progressbar" style="width: ${percentage}%"
                 aria-valuenow="${percentage}" aria-valuemin="0" aria-valuemax="100">
              ${percentage}%
            </div>
          </div>
        </td>
      </tr>
    `;
                tbody.innerHTML += row;
            });
        }

        /**
         * 使用模拟数据
         */
        function loadMockStatistics() {
            console.log('[模拟] 使用模拟数据');

            const mockData = {
                date: document.getElementById('dateSelector').value,
                totalOrders: 45,
                totalUsers: 38,
                dishStatistics: [
                    { dishId: 'D001', dishName: '红烧肉', totalQuantity: 25, wholePortion: 20, halfPortion: 5 },
                    { dishId: 'D002', dishName: '宫保鸡丁', totalQuantity: 18, wholePortion: 10, halfPortion: 8 },
                    { dishId: 'D003', dishName: '鱼香肉丝', totalQuantity: 15, wholePortion: 12, halfPortion: 3 },
                    { dishId: 'D004', dishName: '麻婆豆腐', totalQuantity: 12, wholePortion: 8, halfPortion: 4 },
                    { dishId: 'D005', dishName: '番茄炒蛋', totalQuantity: 10, wholePortion: 6, halfPortion: 4 }
                ]
            };

            console.log('[模拟] 数据', mockData);

            // 更新界面
            updateOverview(mockData);
            updateCharts(mockData.dishStatistics);
            updateTable(mockData.dishStatistics);

            // 切换显示状态
            document.getElementById('loading').innerHTML = `
    <div class="alert alert-warning">
      <strong>提示:</strong> Mock 服务未启动，当前显示的是模拟数据
      <br><small>启动 Mock 服务: <code>cd mock && docker-compose up -d</code></small>
    </div>
  `;
            document.getElementById('loading').style.display = 'block';
            document.getElementById('statisticsContent').style.display = 'block';

            // 3秒后自动隐藏提示
            setTimeout(() => {
                document.getElementById('loading').style.display = 'none';
            }, 3000);
        }

        /**
         * 退出登录
         */
        function logout() {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        }

        // ========== 边界测试用例 ==========

        /**
         * 测试用例 1: 空数据场景
         */
        function testEmptyData() {
            console.log('[测试] 空数据场景');
            const emptyData = {
                date: '2026-04-20',
                totalOrders: 0,
                totalUsers: 0,
                dishStatistics: []
            };

            updateOverview(emptyData);
            updateCharts(emptyData.dishStatistics);
            updateTable(emptyData.dishStatistics);

            console.log('[测试] ✅ 空数据处理正常');
        }

        /**
         * 测试用例 2: 大数据量场景
         */
        function testLargeData() {
            console.log('[测试] 大数据量场景');
            const largeData = {
                date: '2026-04-15',
                totalOrders: 999,
                totalUsers: 500,
                dishStatistics: Array.from({ length: 20 }, (_, i) => ({
                    dishId: `D${String(i + 1).padStart(3, '0')}`,
                    dishName: `菜品${i + 1}`,
                    totalQuantity: Math.floor(Math.random() * 100),
                    wholePortion: Math.floor(Math.random() * 50),
                    halfPortion: Math.floor(Math.random() * 50)
                }))
            };

            updateOverview(largeData);
            updateCharts(largeData.dishStatistics);
            updateTable(largeData.dishStatistics);

            console.log('[测试] ✅ 大数据量处理正常');
        }

        /**
         * 运行所有边界测试
         */
        function runBoundaryTests() {
            console.log('========== 边界测试开始 ==========');
            testEmptyData();
            testLargeData();
            console.log('========== 边界测试完成 ==========');
        }

        // 在控制台运行边界测试
        // runBoundaryTests();

    </script>
</body>
</html>
