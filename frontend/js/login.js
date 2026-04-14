/**
 * 登录页面逻辑
 * 支持学生和管理员两种角色登录
 */

// 配置
const API_BASE_URL = 'http://localhost:4010/api/v1';
let currentRole = 'student'; // 当前选择的角色

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
  console.log('[初始化] 登录页面加载完成');
  
  // 检查是否已登录
  const savedUser = localStorage.getItem('currentUser');
  if (savedUser) {
    const user = JSON.parse(savedUser);
    console.log('[提示] 检测到已登录用户，正在跳转...', user);
    redirectToPage(user.role);
  }
  
  // 绑定回车键登录
  document.getElementById('password').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      handleLogin(e);
    }
  });
});

/**
 * 选择角色
 */
function selectRole(role) {
  currentRole = role;
  
  // 更新 UI
  document.querySelectorAll('.role-btn').forEach(btn => {
    btn.classList.remove('active');
  });
  document.querySelector(`[data-role="${role}"]`).classList.add('active');
  
  // 清空表单
  document.getElementById('username').value = '';
  document.getElementById('password').value = '';
  hideError();
  
  console.log('[角色] 已切换到', role === 'student' ? '学生' : '管理员');
}

/**
 * 填充测试账号
 */
function fillAccount(username, password, role) {
  document.getElementById('username').value = username;
  document.getElementById('password').value = password;
  
  // 自动选择对应角色
  selectRole(role);
  
  console.log('[测试] 已填充账号', { username, role });
  
  // 自动登录
  setTimeout(() => {
    handleLogin(new Event('submit'));
  }, 300);
}

/**
 * 处理登录
 */
async function handleLogin(event) {
  event.preventDefault();
  
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;
  const loginBtn = document.getElementById('loginBtn');
  
  // 验证输入
  if (!username || !password) {
    showError('请输入账号和密码');
    return;
  }
  
  // 禁用按钮，显示加载状态
  loginBtn.disabled = true;
  loginBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 登录中...';
  
  try {
    console.log('[请求] 尝试登录', { username, role: currentRole });
    
    // 尝试调用 Mock API
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 3000);
    
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
      signal: controller.signal
    });
    clearTimeout(timeoutId);
    
    if (response.ok) {
      const result = await response.json();
      console.log('[响应] 登录成功', result);
      
      // 保存用户信息
      const user = result.data.user;
      localStorage.setItem('currentUser', JSON.stringify(user));
      localStorage.setItem('token', result.data.token);
      
      // 跳转到对应页面
      redirectToPage(user.role);
    } else {
      throw new Error('账号或密码错误');
    }
  } catch (error) {
    console.warn('[警告] Mock 服务未响应，使用本地验证', error.message);
    
    // 使用本地验证（模拟数据）
    handleLocalLogin(username, password);
  } finally {
    loginBtn.disabled = false;
    loginBtn.innerHTML = '<i class="bi bi-box-arrow-in-right"></i> 登录';
  }
}

/**
 * 本地登录（模拟数据）
 */
function handleLocalLogin(username, password) {
  // 模拟用户数据
  const mockUsers = {
    'stu1': { username: 'stu1', role: 'student', name: '张三', password: '123456' },
    'stu2': { username: 'stu2', role: 'student', name: '李四', password: '123456' },
    'stu3': { username: 'stu3', role: 'student', name: '王五', password: '123456' },
    'admin': { username: 'admin', role: 'admin', name: '管理员', password: '123456' }
  };
  
  const user = mockUsers[username];
  
  // 验证账号密码
  if (!user) {
    showError('账号不存在');
    return;
  }
  
  if (user.password !== password) {
    showError('密码错误');
    return;
  }
  
  // 验证角色是否匹配
  if (user.role !== currentRole) {
    showError(`该账号不是${currentRole === 'student' ? '学生' : '管理员'}`);
    return;
  }
  
  console.log('[本地登录] 登录成功', user);
  
  // 保存用户信息
  localStorage.setItem('currentUser', JSON.stringify(user));
  localStorage.setItem('token', 'mock-token-' + username);
  
  // 跳转到对应页面
  redirectToPage(user.role);
}

/**
 * 根据角色跳转到对应页面
 */
function redirectToPage(role) {
  if (role === 'admin') {
    console.log('[跳转] 管理员 -> admin-stats.html');
    window.location.href = 'admin-stats.html';
  } else {
    console.log('[跳转] 学生 -> student-order.html');
    window.location.href = 'student-order.html';
  }
}

/**
 * 显示错误信息
 */
function showError(message) {
  const alertEl = document.getElementById('errorAlert');
  const messageEl = document.getElementById('errorMessage');
  
  messageEl.textContent = message;
  alertEl.style.display = 'block';
  
  // 3秒后自动隐藏
  setTimeout(() => {
    hideError();
  }, 3000);
}

/**
 * 隐藏错误信息
 */
function hideError() {
  document.getElementById('errorAlert').style.display = 'none';
}

/**
 * 登出功能（供其他页面调用）
 */
function logout() {
  localStorage.removeItem('currentUser');
  localStorage.removeItem('token');
  window.location.href = 'login.html';
}
