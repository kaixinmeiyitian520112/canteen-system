@echo off
REM Git Flow 分支初始化脚本
REM 用途：创建 develop 主干和示例功能分支

echo ==========================================
echo   Git Flow 分支初始化
echo ==========================================
echo.

REM 检查是否在 git 仓库中
if not exist .git (
    echo [错误] 当前目录不是 Git 仓库
    pause
    exit /b 1
)

REM 创建 develop 分支
echo [1/5] 创建 develop 分支...
git checkout -b develop 2>nul || git checkout develop
echo.

REM 推送到远程（如果已配置）
echo [2/5] 推送 develop 到远程...
git push -u origin develop 2>nul || echo [提示] 未配置远程仓库，跳过
echo.

REM 创建示例功能分支 1
echo [3/5] 创建功能分支: feature/user-auth...
git checkout -b feature/user-auth develop
echo.

REM 创建示例功能分支 2
echo [4/5] 创建功能分支: feature/dish-order...
git checkout -b feature/dish-order develop
echo.

REM 切换回 develop
echo [5/5] 切换回 develop 分支...
git checkout develop
echo.

echo ==========================================
echo   分支创建完成！
echo ==========================================
echo.
echo 当前分支结构:
echo   main/master (生产)
echo     └── develop (开发主干) ◀ 当前
echo           ├── feature/user-auth (功能分支)
echo           └── feature/dish-order (功能分支)
echo.
echo 下一步:
echo   1. git checkout feature/user-auth
echo   2. 进行开发并提交
echo   3. git merge develop (解决冲突)
echo   4. git push origin feature/user-auth
echo   5. 在 GitHub 发起 PR
echo.
pause
