# 无尽浏览器 (VeryWeb)

![GitHub](https://img.shields.io/github/license/1990569689/VeryWeb?style=for-the-badge)
![GitHub release](https://img.shields.io/github/v/release/1990569689/VeryWeb?style=for-the-badge)
![GitHub Downloads](https://img.shields.io/github/downloads/1990569689/VeryWeb/total?style=for-the-badge)

**轻量 · 无广告 · 高定制化**的开源安卓浏览器，基于GPL-3.0协议发布

[English](./README.md) | 简体中文

## ✨ 核心特色

### 🚀 极速体验
- **返回不重载**：采用`ViewPager`双层嵌套技术，实现页面无刷新返回
- **解决301/302重载**：优化重定向流程，减少页面重复加载

### 📚 本地数据管理
- **多级书签系统**：文件夹嵌套结构，支持HTML书签导入
- **本地历史记录**：使用SQLite数据库存储浏览历史
- **小纸条功能**：轻量级本地笔记系统

### 🎨 深度UI定制
```html
<!-- 自定义主页示例 -->
<!DOCTYPE html>
<html>
<head>
    <title>我的主页</title>
    <style>
        :root { --primary-color: #4CAF50; }
        .header { background: var(--primary-color); }
    </style>
</head>
<body>
    <h1>欢迎使用无尽浏览器</h1>
</body>
</html>
```
- 自定义主页（支持本地HTML/URL）
- 可配置头部文字、头像、CSS样式
- 自定义背景图片

### 🌙 智能浏览辅助
- **夜间模式**：通过JavaScript注入实现页面暗色转换
- **资源嗅探**：自动提取页面中的视频/图片资源
- **油猴脚本支持**：兼容用户脚本扩展

### 🛠️ 实用工具
- **网页保存**：支持MHT/PDF格式导出
- **扫码功能**：快速识别二维码
- **下载管理器**：内置文件下载功能
- **长按增强**：
  - 图片/链接特殊操作
  - 文本选择优化

## 📦 安装使用

### 要求环境
- Android 5.0 (API 21) 及以上
- 最小支持屏幕尺寸：320×480 dp

### 下载安装
[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?style=for-the-badge&logo=android)](https://github.com/1990569689/VeryWeb/releases/latest)

或从源码构建：
```bash
git clone https://github.com/yourusername/veryweb.git
cd veryweb
./gradlew assembleDebug
```

## 🧩 功能演示
| 书签管理 | 夜间模式 | 资源嗅探 |
|----------|----------|----------|
| ![书签管理](screenshots/bookmarks.gif) | ![夜间模式](screenshots/nightmode.gif) | ![资源嗅探](screenshots/resources.gif) |

| UI定制 | 网页保存 | UA切换 |
|--------|----------|--------|
| ![UI定制](screenshots/customize.jpg) | ![网页保存](screenshots/save_as_pdf.jpg) | ![UA切换](screenshots/ua_switch.jpg) |

## 🤝 参与贡献
欢迎通过以下方式参与项目：
1. 提交Issue报告问题或建议
2. Fork仓库并提交Pull Request
3. 完善项目文档
4. 帮助翻译多语言版本

贡献前请阅读[贡献指南](CONTRIBUTING.md)

## 📄 开源协议
```text
Copyright (C) 2023 Your Name

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```
项目基于 **GPL-3.0** 协议开源 - 查看完整[许可证文件](LICENSE)

## 📬 联系我们
- 项目主页：https://github.com/1990569689/VeryWeb
- 问题反馈：https://github.com/1990569689/VeryWeb/issues
- 邮箱：1990569689@qq.com
---

**让浏览回归纯粹** - 无尽浏览器团队 ✨
### 使用的第三方库 
[QRCodeView](https://github.com/0xZhangKe/QRCodeView)

[jiaozivideoplayer](https://github.com/lipangit/JiaoZiVideoPlayer)

[webview-gm](https://github.com/wbayer/webview-gm)
