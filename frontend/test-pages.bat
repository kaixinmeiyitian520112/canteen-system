@echo off
REM 前端页面测试启动脚本
REM 用途：快速打开前端页面进行测试

echo ==========================================
echo   前端页面测试
echo ==========================================
echo.

echo 请选择要打开的页面:
echo.
echo [1] 登录页 (login.html)
echo [2] 学生订餐页 (student-order.html)
echo [3] 管理员统计页 (admin-stats.html)
echo [4] 全部打开
echo.
set /p choice=请输入选项 (1/2/3/4): 

if "%choice%"=="1" (
    echo.
    echo 正在打开登录页...
    start "" "%~dp0login.html"
) else if "%choice%"=="2" (
    echo.
    echo 正在打开学生订餐页...
    start "" "%~dp0student-order.html"
) else if "%choice%"=="3" (
    echo.
    echo 正在打开管理员统计页...
    start "" "%~dp0admin-stats.html"
) else if "%choice%"=="4" (
    echo.
    echo 正在打开所有页面...
    start "" "%~dp0login.html"
    timeout /t 1 /nobreak >nul
    start "" "%~dp0student-order.html"
    timeout /t 1 /nobreak >nul
    start "" "%~dp0admin-stats.html"
) else (
    echo.
    echo 无效选项，退出。
    pause
    exit /b 1
)

echo.
echo ==========================================
echo 提示:
echo ==========================================
echo - 页面将自动使用模拟数据
echo - 按 F12 可打开浏览器控制台查看日志
echo - 如需使用 Mock API 数据，请先启动:
echo   cd mock ^&^& docker-compose up -d
echo ==========================================
echo.
pause
