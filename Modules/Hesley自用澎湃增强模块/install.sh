#模块配置
SKIPMOUNT=false
LATESTARTSERVICE=true
POSTFSDATA=true
PROPFILE=true
#打印的信息
Manufacturer=$(getprop ro.product.vendor.manufacturer)
Codename=$(getprop ro.product.device)
Model=$(getprop ro.product.vendor.model)
Build=$(getprop ro.build.version.incremental)
AndroidVersion=$(getprop ro.build.version.release)
CPU_ABI=$(getprop ro.product.cpu.abi)
HyperOS=$(getprop ro.miui.ui.version.code)
#操作路径
target_module="hyperos_hesley_plugin"
modules=/data/adb/modules
modules_update=/data/adb/modules_update/
# 定义颜色代码
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # 恢复默认颜色

# 在需要打印彩色字体的地方使用颜色代码
#echo -e "${RED}Error:${NC} Something went wrong."  # 输出红色字体
#echo -e "${GREEN}Success:${NC} Operation completed successfully."  # 输出绿色字体
#echo -e "${YELLOW}Warning:${NC} Please proceed with caution."  # 输出黄色字体

#打印信息，模块安装脚本自动调用
print_modname() {
    ui_print "=================================================="
    sleep 0.01
    ui_print "- 模块脚本作者:Hesley"
    sleep 0.01
    ui_print "- Github主页：https://github.com/ldh-star"
    sleep 0.01
    ui_print "=================================================="
    sleep 0.01
    ui_print "- 当前模块版本: V1.2 (202409014)"
    sleep 0.01
    ui_print "- 模块更新日期: 2024.09.14"
    sleep 0.01
    ui_print "- 模块依赖环境: China ROM｜HyperOS"
    sleep 0.01
    ui_print "=================================================="
    sleep 0.01
    ui_print "- 设备型号: $Model"
    sleep 0.01
    ui_print "- 设备厂商: $Manufacturer"
    sleep 0.01
    ui_print "- 设备代号: $Codename"
    sleep 0.01
    ui_print "- API级别: $API"
    sleep 0.01
    ui_print "- CPU_ABI: $CPU_ABI"
    sleep 0.01
    ui_print "- Android版本: $AndroidVersion"
    sleep 0.01
    ui_print "- OS版本: MIUI $HyperOS（HyperOS）"
    sleep 0.01
    ui_print "- Build版本: $Build"
    sleep 0.05
    ui_print "=================================================="
    sleep 0.01
    ui_print "* 请确保你的设备处于安卓14环境下的HyperOS系统并核心破解选项全开"
    ui_print "(部分设备需手动编译framework.jar和service.jar进行彻底核心破解)"
    ui_print "* 刷之前自备TWRP或自动救砖模块，出现卡第二屏，会自动禁用所有模块重启"
    ui_print "* 开机或者手动挂载系统分区rw权限，/data/adb/modules/路径下删除模块"
    ui_print "* 重启设备以恢复系统"
    ui_print "=================================================="
}

#安装的具体操作，模块安装脚本自动调用
on_install() {
        ui_print "=================================================="
        ui_print "- 正在释放文件"
        ui_print "=================================================="

        unzip -o "$ZIPFILE" 'system/*' -d $MODPATH >&2 || abort "解压挂载文件出错"
        unzip -o "$ZIPFILE" 'Rely/*' -d $MODPATH >&2 || abort "解压依赖文件出错"
        sleep 0.05

}

#设置权限，模块脚本自动调用
set_permissions() {
    set_perm_recursive $MODPATH 0 0 0755 0644 || true
}
