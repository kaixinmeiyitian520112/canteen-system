@echo off
chcp 65001 >nul
cd /d %~dp0
echo 正在启动校园食堂订餐系统...
java -classpath out\classes;out\com.canteen.Main com.canteen.Main
pause
