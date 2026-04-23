package com.example.canteen.service;

import com.example.canteen.dao.UserDAO;
import com.example.canteen.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private UserDAO mockUserDAO;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        mockUserDAO = mock(UserDAO.class);
        loginService = new LoginService(mockUserDAO);
    }

    @Test
    void testLoginSuccess() {
        // Arrange: 模拟 DAO 返回用户
        User expectedUser = new User("stu1", "123456", "student", "张三");
        when(mockUserDAO.findByUsername("stu1")).thenReturn(expectedUser);

        // Act: 执行登录
        User result = loginService.login("stu1", "123456");

        // Assert: 验证返回结果正确
        assertNotNull(result);
        assertEquals("stu1", result.getUsername());
        assertEquals("张三", result.getName());
        assertEquals("student", result.getRole());

        // 验证 DAO 方法被正确调用
        verify(mockUserDAO, times(1)).findByUsername("stu1");
    }

    @Test
    void testLoginFailureWrongPassword() {
        // Arrange
        User user = new User("stu1", "123456", "student", "张三");
        when(mockUserDAO.findByUsername("stu1")).thenReturn(user);

        // Act
        User result = loginService.login("stu1", "wrongpassword");

        // Assert
        assertNull(result);
    }

    @Test
    void testLoginFailureUserNotFound() {
        // Arrange
        when(mockUserDAO.findByUsername("unknown")).thenReturn(null);

        // Act
        User result = loginService.login("unknown", "123456");

        // Assert
        assertNull(result);
    }
}