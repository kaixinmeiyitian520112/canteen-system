@echo off
REM Prism Mock 环境启动脚本
REM 用途：使用 Docker 启动 Prism Mock 服务器

echo ==========================================
echo   Prism Mock 环境启动
echo ==========================================
echo.

REM 检查 Docker 是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未运行，请先启动 Docker Desktop
    pause
    exit /b 1
)

echo [1/3] 检查 OpenAPI 契约文件...
if not exist ..\api\openapi.yaml (
    echo [错误] 找不到 openapi.yaml 文件
    pause
    exit /b 1
)
echo [成功] 契约文件存在
echo.

echo [2/3] 启动 Prism Mock 服务器...
docker-compose up -d
echo.

echo [3/3] 等待服务就绪...
timeout /t 5 /nobreak >nul

echo.
echo ==========================================
echo   Mock 环境启动成功！
echo ==========================================
echo.
echo Mock 服务器地址: http://localhost:4010
echo API 文档地址:  http://localhost:4010/docs
echo.
echo 可用接口示例:
echo   - POST http://localhost:4010/api/v1/auth/login
echo   - GET  http://localhost:4010/api/v1/dishes
echo   - POST http://localhost:4010/api/v1/orders
echo.
echo 停止服务: docker-compose down
echo.
pause
