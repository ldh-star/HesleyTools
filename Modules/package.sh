#!/bin/bash

# 指定要打包的目录，请根据需要修改此路径
TARGET_DIRECTORY="./Hesley自用澎湃增强模块"

# 指定打包模式: "tar" 或 "zip", 请根据需要修改为 "tar" 或 "zip"
#ARCHIVE_MODE="tar"
ARCHIVE_MODE="zip"

# =======================以上是配置区，下面是脚本区===============================

# 获取脚本所在目录
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

# 检查目录是否存在
if [ ! -d "$TARGET_DIRECTORY" ]; then
  echo "错误：目录 $TARGET_DIRECTORY 不存在。"
  exit 1
fi

# 获取目录的名称
DIR_NAME=$(basename "$TARGET_DIRECTORY")

# 确定打包文件的名称
if [ "$ARCHIVE_MODE" == "tar" ]; then
  ARCHIVE_FILE="${SCRIPT_DIR}/${DIR_NAME}.tar.gz"
elif [ "$ARCHIVE_MODE" == "zip" ]; then
  ARCHIVE_FILE="${SCRIPT_DIR}/${DIR_NAME}.zip"
else
  echo "错误：未知的打包模式 $ARCHIVE_MODE。请使用 \"tar\" 或 \"zip\"。"
  exit 1
fi

# 打包目录下的所有文件和子目录
cd "$TARGET_DIRECTORY" || exit

if [ "$ARCHIVE_MODE" == "tar" ]; then
  tar -czvf "$ARCHIVE_FILE" ./*
elif [ "$ARCHIVE_MODE" == "zip" ]; then
  zip -r "$ARCHIVE_FILE" ./*
fi

# 打包完成的提示
echo "目录 $TARGET_DIRECTORY 下的所有文件和子目录已打包为 $ARCHIVE_FILE"
