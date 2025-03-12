#!/system/bin/sh

MODDIR=${0%/*}

while [ "$(/system/bin/app_process -Djava.class.path=$MODDIR/Rely/isKeyguardLocked.dex /system/bin com.rosan.shell.ActiviteJava)" == "true" ];
do
sleep 1
done

# 小白条跟随应用移动
sleep 2 && cmd overlay fabricate --target android --name NavbarAttach android:bool/config_attachNavBarToAppDuringTransition 0x12 1
cmd overlay enable com.android.shell:NavbarAttach

# 锁屏常驻小白条
su -c device_config put systemui nav_bar_handle_show_over_lockscreen false default
