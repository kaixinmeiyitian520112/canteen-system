@echo off
chcp 65001 >nul
cd /d %~dp0
echo 正在编译...
rmdir /s /q out 2>nul
mkdir out
javac -encoding UTF-8 -d out src\com\canteen\entity\*.java ^
    src\com\canteen\dao\*.java ^
    src\com\canteen\dao\impl\*.java ^
    src\com\canteen\service\*.java ^
    src\com\canteen\view\*.java ^
    src\com\canteen\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo 编译成功！正在启动...
    echo.
    java -cp out com.canteen.Main
) else (
    echo.
    echo 编译失败！请检查错误信息。
)
pause
