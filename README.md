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
  GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

                            Preamble

  The GNU General Public License is a free, copyleft license for
software and other kinds of works.

  The licenses for most software and other practical works are designed
to take away your freedom to share and change the works.  By contrast,
the GNU General Public License is intended to guarantee your freedom to
share and change all versions of a program--to make sure it remains free
software for all its users.  We, the Free Software Foundation, use the
GNU General Public License for most of our software; it applies also to
any other work released this way by its authors.  You can apply it to
your programs, too.

  When we speak of free software, we are referring to freedom, not
price.  Our General Public Licenses are designed to make sure that you
have the freedom to distribute copies of free software (and charge for
them if you wish), that you receive source code or can get it if you
want it, that you can change the software or use pieces of it in new
free programs, and that you know you can do these things.

  To protect your rights, we need to prevent others from denying you
these rights or asking you to surrender the rights.  Therefore, you have
certain responsibilities if you distribute copies of the software, or if
you modify it: responsibilities to respect the freedom of others.

  For example, if you distribute copies of such a program, whether
gratis or for a fee, you must pass on to the recipients the same
freedoms that you received.  You must make sure that they, too, receive
or can get the source code.  And you must show them these terms so they
know their rights.

  Developers that use the GNU GPL protect your rights with two steps:
(1) assert copyright on the software, and (2) offer you this License
giving you legal permission to copy, distribute and/or modify it.

  For the developers' and authors' protection, the GPL clearly explains
that there is no warranty for this free software.  For both users' and
authors' sake, the GPL requires that modified versions be marked as
changed, so that their problems will not be attributed erroneously to
authors of previous versions.

  Some devices are designed to deny users access to install or run
modified versions of the software inside them, although the manufacturer
can do so.  This is fundamentally incompatible with the aim of
protecting users' freedom to change the software.  The systematic
pattern of such abuse occurs in the area of products for individuals to
use, which is precisely where it is most unacceptable.  Therefore, we
have designed this version of the GPL to prohibit the practice for those
products.  If such problems arise substantially in other domains, we
stand ready to extend this provision to those domains in future versions
of the GPL, as needed to protect the freedom of users.

  Finally, every program is threatened constantly by software patents.
States should not allow patents to restrict development and use of
software on general-purpose computers, but in those that do, we wish to
avoid the special danger that patents applied to a free program could
make it effectively proprietary.  To prevent this, the GPL assures that
patents cannot be used to render the program non-free.

  The precise terms and conditions for copying, distribution and
modification follow.

                       TERMS AND CONDITIONS

  0. Definitions.

  "This License" refers to version 3 of the GNU General Public License.

  "Copyright" also means copyright-like laws that apply to other kinds of
works, such as semiconductor masks.

  "The Program" refers to any copyrightable work licensed under this
License.  Each licensee is addressed as "you".  "Licensees" and
"recipients" may be individuals or organizations.

  To "modify" a work means to copy from or adapt all or part of the work
in a fashion requiring copyright permission, other than the making of an
exact copy.  The resulting work is called a "modified version" of the
earlier work or a work "based on" the earlier work.

  A "covered work" means either the unmodified Program or a work based
on the Program.

  To "propagate" a work means to do anything with it that, without
permission, would make you directly or secondarily liable for
infringement under applicable copyright law, except executing it on a
computer or modifying a private copy.  Propagation includes copying,
distribution (with or without modification), making available to the
public, and in some countries other activities as well.

  To "convey" a work means any kind of propagation that enables other
parties to make or receive copies.  Mere interaction with a user through
a computer network, with no transfer of a copy, is not conveying.

  An interactive user interface displays "Appropriate Legal Notices"
to the extent that it includes a convenient and prominently visible
feature that (1) displays an appropriate copyright notice, and (2)
tells the user that there is no warranty for the work (except to the
extent that warranties are provided), that licensees may convey the
work under this License, and how to view a copy of this License.  If
the interface presents a list of user commands or options, such as a
menu, a prominent item in the list meets this criterion.
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
