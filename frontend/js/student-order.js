/**
 * 学生订餐页面逻辑
 * 基于 Mock API (Prism) 实现逻辑闭环
 */

// 配置
const API_BASE_URL = 'http://localhost:4010/api/v1';
let currentUser = null;
let cart = [];
let dishes = [];

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', async () => {
  console.log('[初始化] 学生订餐页面加载完成');
  
  // 模拟登录（实际应由登录页跳转）
  await mockLogin();
  
  // 加载菜品列表
  await loadDishes();
  
  // 绑定事件
  bindEvents();
});

/**
 * 模拟登录
 */
async function mockLogin() {
  try {
    // 先从 localStorage 获取登录信息
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      currentUser = JSON.parse(savedUser);
      document.getElementById('userInfo').textContent = `欢迎，${currentUser.name}`;
      console.log('[登录] 从本地存储恢复', currentUser);
      return;
    }
    
    // 如果没有本地存储，跳转到登录页
    console.log('[登录] 未登录，跳转到登录页');
    window.location.href = 'login.html';
    return;
    
  } catch (error) {
    console.error('[登录] 错误:', error);
    // 默认用户（仅用于开发调试）
    currentUser = { username: 'stu1', name: '张三', role: 'student' };
  }
}

/**
 * 加载菜品列表
 */
async function loadDishes() {
  const loadingEl = document.getElementById('loading');
  const dishListEl = document.getElementById('dishList');
  
  try {
    console.log('[请求] GET /dishes');
    
    // 尝试从 Mock API 获取数据
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 3000); // 3秒超时
    
    const response = await fetch(`${API_BASE_URL}/dishes`, {
      signal: controller.signal
    });
    clearTimeout(timeoutId);
    
    if (response.ok) {
      const result = await response.json();
      dishes = result.data || result; // 兼容不同响应格式
      
      console.log('[响应] 获取菜品列表', dishes);
      
      // 渲染菜品
      renderDishes(dishes);
      
      // 切换显示状态
      loadingEl.style.display = 'none';
      dishListEl.style.display = 'flex';
    } else {
      throw new Error('获取菜品失败');
    }
  } catch (error) {
    console.warn('[警告] Mock 服务未响应，使用模拟数据', error.message);
    // 使用模拟数据
    loadMockDishes();
  }
}

/**
 * 使用模拟数据加载菜品
 */
function loadMockDishes() {
  const loadingEl = document.getElementById('loading');
  const dishListEl = document.getElementById('dishList');
  
  console.log('[模拟] 使用模拟数据');
  
  dishes = [
    {
      id: 'D001',
      name: '红烧肉',
      description: '经典家常菜，肥而不腻',
      price: 12.00,
      category: '荤菜',
      portions: [
        { type: 'whole', label: '整份', price: 12.00 },
        { type: 'half', label: '半份', price: 6.00 }
      ],
      remaining: 50,
      image: '/images/dishes/D001.jpg'
    },
    {
      id: 'D002',
      name: '宫保鸡丁',
      description: '川菜经典，鲜香麻辣',
      price: 10.00,
      category: '荤菜',
      portions: [
        { type: 'whole', label: '整份', price: 10.00 },
        { type: 'half', label: '半份', price: 5.00 }
      ],
      remaining: 45,
      image: '/images/dishes/D002.jpg'
    },
    {
      id: 'D003',
      name: '鱼香肉丝',
      description: '甜酸口味，老少皆宜',
      price: 9.00,
      category: '荤菜',
      portions: [
        { type: 'whole', label: '整份', price: 9.00 },
        { type: 'half', label: '半份', price: 4.50 }
      ],
      remaining: 38,
      image: '/images/dishes/D003.jpg'
    },
    {
      id: 'D004',
      name: '麻婆豆腐',
      description: '豆腐嫩滑，麻辣鲜香',
      price: 6.00,
      category: '素菜',
      portions: [
        { type: 'whole', label: '整份', price: 6.00 },
        { type: 'half', label: '半份', price: 3.00 }
      ],
      remaining: 60,
      image: '/images/dishes/D004.jpg'
    },
    {
      id: 'D005',
      name: '番茄炒蛋',
      description: '营养健康，酸甜可口',
      price: 5.00,
      category: '素菜',
      portions: [
        { type: 'whole', label: '整份', price: 5.00 },
        { type: 'half', label: '半份', price: 2.50 }
      ],
      remaining: 55,
      image: '/images/dishes/D005.jpg'
    },
    {
      id: 'D006',
      name: '清炒时蔬',
      description: '新鲜时令蔬菜',
      price: 4.00,
      category: '素菜',
      portions: [
        { type: 'whole', label: '整份', price: 4.00 },
        { type: 'half', label: '半份', price: 2.00 }
      ],
      remaining: 70,
      image: '/images/dishes/D006.jpg'
    }
  ];
  
  // 渲染菜品
  renderDishes(dishes);
  
  // 切换显示状态
  loadingEl.innerHTML = `
    <div class="alert alert-warning">
      <strong>提示:</strong> Mock 服务未启动，当前显示的是模拟数据
      <br><small>启动 Mock 服务: <code>cd mock && docker-compose up -d</code></small>
    </div>
  `;
  loadingEl.style.display = 'block';
  dishListEl.style.display = 'flex';
  
  // 3秒后自动隐藏提示
  setTimeout(() => {
    loadingEl.style.display = 'none';
  }, 3000);
}

/**
 * 渲染菜品列表
 */
function renderDishes(dishList) {
  const container = document.getElementById('dishList');
  container.innerHTML = '';
  
  dishList.forEach(dish => {
    const card = `
      <div class="col-md-6">
        <div class="card dish-card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start">
              <h5 class="card-title">${dish.name}</h5>
              <span class="badge bg-primary">${dish.category}</span>
            </div>
            <p class="card-text text-muted small">${dish.description || '暂无描述'}</p>
            
            <div class="mt-3">
              <p class="mb-2"><strong>份量选择:</strong></p>
              <div class="portion-selector">
                ${dish.portions.map(p => `
                  <button class="btn btn-outline-primary portion-btn" 
                          data-dish-id="${dish.id}" 
                          data-portion="${p.type}" 
                          data-price="${p.price}">
                    ${p.label} - ¥${p.price.toFixed(2)}
                  </button>
                `).join('')}
              </div>
            </div>
            
            <div class="mt-3 text-end">
              <small class="text-success">剩余: ${dish.remaining || 50} 份</small>
            </div>
          </div>
        </div>
      </div>
    `;
    container.innerHTML += card;
  });
}

/**
 * 绑定事件
 */
function bindEvents() {
  // 份量选择按钮点击事件
  document.querySelectorAll('.portion-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const dishId = e.target.dataset.dishId;
      const portion = e.target.dataset.portion;
      const price = parseFloat(e.target.dataset.price);
      
      addToCart(dishId, portion, price);
    });
  });
}

/**
 * 添加到购物车
 */
function addToCart(dishId, portion, price) {
  // 查找菜品信息
  const dish = dishes.find(d => d.id === dishId);
  if (!dish) {
    console.error('[错误] 找不到菜品', dishId);
    return;
  }
  
  // 查找份量标签
  const portionInfo = dish.portions.find(p => p.type === portion);
  
  // 检查是否已存在
  const existingItem = cart.find(item => item.dishId === dishId && item.portion === portion);
  
  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({
      dishId: dish.id,
      dishName: dish.name,
      portion: portion,
      portionLabel: portionInfo.label,
      price: price,
      quantity: 1
    });
  }
  
  console.log('[购物车] 已添加', { dishId, portion, price });
  renderCart();
}

/**
 * 渲染购物车
 */
function renderCart() {
  const cartEl = document.getElementById('cartItems');
  const totalEl = document.getElementById('totalAmount');
  const submitBtn = document.getElementById('submitOrderBtn');
  
  if (cart.length === 0) {
    cartEl.innerHTML = '<p class="text-muted text-center py-3">购物车为空，请选择菜品</p>';
    totalEl.textContent = '0.00';
    submitBtn.disabled = true;
    return;
  }
  
  let html = '';
  let total = 0;
  
  cart.forEach((item, index) => {
    const itemTotal = item.price * item.quantity;
    total += itemTotal;
    
    html += `
      <div class="d-flex justify-content-between align-items-center mb-2 p-2 border-bottom">
        <div>
          <strong>${item.dishName}</strong>
          <br><small class="text-muted">${item.portionLabel} x${item.quantity}</small>
        </div>
        <div class="text-end">
          <span class="text-primary">¥${itemTotal.toFixed(2)}</span>
          <button class="btn btn-sm btn-outline-danger ms-2" onclick="removeFromCart(${index})">×</button>
        </div>
      </div>
    `;
  });
  
  cartEl.innerHTML = html;
  totalEl.textContent = total.toFixed(2);
  submitBtn.disabled = false;
}

/**
 * 从购物车移除
 */
function removeFromCart(index) {
  cart.splice(index, 1);
  renderCart();
}

/**
 * 提交订单
 */
async function submitOrder() {
  if (cart.length === 0) {
    alert('购物车为空');
    return;
  }
  
  const submitBtn = document.getElementById('submitOrderBtn');
  submitBtn.disabled = true;
  submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 提交中...';
  
  try {
    // 构建订单数据
    const orderData = {
      items: cart.map(item => ({
        dishId: item.dishId,
        portion: item.portion,
        quantity: item.quantity
      }))
    };
    
    console.log('[请求] POST /orders', orderData);
    
    // 尝试调用 Mock API，设置 3 秒超时
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 3000);
    
    const response = await fetch(`${API_BASE_URL}/orders`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer mock-token'
      },
      body: JSON.stringify(orderData),
      signal: controller.signal
    });
    clearTimeout(timeoutId);
    
    if (response.ok) {
      const result = await response.json();
      console.log('[响应] 订单创建成功', result);
      showOrderSuccess(result.data);
    } else {
      throw new Error('订单提交失败');
    }
  } catch (error) {
    console.warn('[警告] Mock 服务未响应，使用模拟订单', error.message);
    // 使用模拟订单数据
    submitMockOrder();
  } finally {
    submitBtn.disabled = false;
    submitBtn.innerHTML = '提交订单';
  }
}

/**
 * 提交模拟订单
 */
function submitMockOrder() {
  console.log('[模拟] 创建模拟订单');
  
  // 生成订单号
  const orderId = 'ORD' + String(Math.floor(Math.random() * 9000) + 1000);
  
  // 计算总价
  let totalAmount = 0;
  const items = cart.map(item => {
    const itemTotal = item.price * item.quantity;
    totalAmount += itemTotal;
    return {
      dishId: item.dishId,
      dishName: item.dishName,
      portion: item.portion,
      portionLabel: item.portionLabel,
      quantity: item.quantity,
      price: item.price
    };
  });
  
  // 构建模拟订单响应
  const mockOrder = {
    id: orderId,
    userId: currentUser ? currentUser.username : 'stu1',
    userName: currentUser ? currentUser.name : '张三',
    items: items,
    totalAmount: totalAmount,
    status: 'confirmed',
    orderDate: new Date().toISOString(),
    deliveryDate: '2026-04-15'
  };
  
  console.log('[模拟] 订单数据', mockOrder);
  
  // 显示成功弹窗
  showOrderSuccess(mockOrder);
  
  // 清空购物车
  cart = [];
  renderCart();
}

/**
 * 显示订单成功弹窗
 */
function showOrderSuccess(order) {
  const modalEl = document.getElementById('orderDetails');
  modalEl.innerHTML = `
    <p><strong>订单号:</strong> ${order.id}</p>
    <p><strong>总金额:</strong> <span class="text-primary">¥${order.totalAmount.toFixed(2)}</span></p>
    <p><strong>状态:</strong> <span class="badge bg-success">${order.status}</span></p>
    <hr>
    <p class="mb-2"><strong>菜品清单:</strong></p>
    <ul class="list-unstyled">
      ${order.items.map(item => `
        <li>${item.dishName} (${item.portionLabel}) x${item.quantity} - ¥${(item.price * item.quantity).toFixed(2)}</li>
      `).join('')}
    </ul>
    <p class="text-muted small mt-3">取餐日期: ${order.deliveryDate}</p>
  `;
  
  const modal = new bootstrap.Modal(document.getElementById('orderSuccessModal'));
  modal.show();
}

/**
 * 退出登录
 */
function logout() {
  localStorage.removeItem('currentUser');
  localStorage.removeItem('token');
  window.location.href = 'login.html';
}
