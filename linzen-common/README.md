> 特别说明：源码、JDK、MySQL、Redis等安装或存放路径禁止包含中文、空格、特殊字符等

## 一 项目结构

```text
linzen-common
    ├── linzen-common-dependencies - 所有依赖的版本号管理
    ├── linzen-common-boot - 单体版本涉及依赖
    ├         ├── linzen-common-userinfo - 单点数据推送模块
    ├         ├── linzen-common-file - 文件工具类模块
    ├         ├── linzen-common-message - 短信模块
    ├         ├── linzen-common-office - office操作模块
    ├         ├── linzen-common-scheduletask - 调度工具模块
    ├         ├── linzen-common-auth - 认证模块
    ├         ├── linzen-common-core - 基础类及常用工具
    ├         ├── linzen-common-database - 数据库配置及多数据库兼容
    ├         ├── linzen-common-redis - 缓存工具Redis组件配置
    ├         ├── linzen-common-security - 接口鉴权配置
    ├         └── linzen-common-swagger - API组件Swagger配置
```

## 二 环境要求

| 类目 | 版本或建议 |
| --- | --- |
| 电脑配置 | 建议开发电脑I3及以上CPU，内存32G及以上 |
| 操作系统 | Windows 10/11，MacOS |
| JDK | 建议使用1.8.0_281及以上版本，可使用Eclipse Temurin JDK 8、Alibaba Dragonwell 8、BiSheng JDK 8等 |
| Maven | 3.6.3及以上版本 |
| 开发工具 | IDEA2020及以上版本或 Eclipse、 Spring Tool Suite等IDE工具 |

## 三 关联项目

> 为以下项目提供基础依赖

| 项目 | 分支 | 分支(Coding) | 说明 |
| --- | --- | --- | --- |
| linzen-file-core-starter | v0.0.x | v0.0.x-stable | 文件基础依赖项目源码 |
| linzen-scheduletask | v0.0.x | v0.0.x-stable | 任务调度客户端依赖及服务端项目源码 |
| linzen-sso-starter | v0.0.x | - | 单点登录集成依赖项目源码 |
| linzen-java-boot | v0.0.x | v0.0.x-stable | java-boot单体后端项目源码 |
| linzen-java-cloud | v0.0.x | v0.0.x-stable | java-cloud微服务后端项目源码 |

## 四 使用方式

### 4.1 前置条件

#### 4.1.1 本地安装linzen-core

在IDEA中，双击右侧 `Maven` 中 `linzen-common` > `linzen-boot-common` > `linzen-common-core` > `Lifecycle` > `install`，将 `linzen-common-core` 包安装至本地

#### 4.1.2 本地安装dependencies

在IDEA中，双击右侧 `Maven` 中 `linzen-common` > `linzen-dependencies` > `Lifecycle` > `install`，将 `linzen-dependencies` 包安装至本地

#### 4.1.3 本地安装file-core-starter

IDEA打开 `linzen-file-core-starter` 项目, 双击右侧 `Maven`中 `linzen-file-core-starter` > `Lifecycle` > `install`，将 `linzen-file-core-starter` 包安装至本地

#### 4.1.4 本地安装scheduletask

IDEA打开 `linzen-scheduletask` 项目, 双击右侧 `Maven`中`linzen-scheduletask` > `Lifecycle` > `install`，将 `linzen-scheduletask` 包安装至本地

### 4.2 本地安装

在IDEA中，双击右侧 `Maven` 中 `linzen-common` > `Lifecycle` > `install`，将 `linzen-common` 包安装至本地

### 4.3 私服发布

#### 4.3.1 配置Maven

打开Maven安装目录中的 `conf/setttings.xml`，

在 `<servers></servers>` 节点增加 `<server></server>` ，如下所示:

```xml

<servers>
    <!-- 发布版 -->
    <server>
        <id>releases</id>
        <username>admin（账号，结合私服配置设置）</username>
        <password>123456（密码，结合私服配置设置）</password>
    </server>

    <server>
        <id>snapshots</id>
        <username>admin</username>
        <password>123456</password>
    </server>
</servers>

<mirrors>
<mirror>
    <id>nexus</id>
    <name>nexus maven</name>
    <url>http://127.0.0.1:8081/repository/maven-public/</url>
    <mirrorOf>*</mirrorOf>
</mirror>
</mirrors>
```

#### 4.3.2 配置项目
> 注意：pom.xml里 `<id>` 和 setting.xml 配置里 `<id>` 对应。

修改 `linzen-common/linzen-dependencies/pom.xml` 文件

```xml
    <!--私服仓库配置-->
<distributionManagement>
    <repository>
        <id>releases</id>
        <name>maven-releases</name>
        <url>http://127.0.0.1:8081/repository/maven-releases/</url>
        <uniqueVersion>true</uniqueVersion>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <name>maven-snapshots</name>
        <url>http://127.0.0.1:8081/repository/maven-snapshots/</url>
        <uniqueVersion>true</uniqueVersion>
    </snapshotRepository>
</distributionManagement>
```

#### 4.3.3 发布到私服

在IDEA中，双击右侧 `Maven` 中 `linzen-common` > `Lifecycle` > `deploy` 发布至私服。

## 五 更新版本号

打开 `linzen-common/linzen-dependencies/` 目录，执行如下命令

```
# mvn versions:set -DnewVersion=0.0.1-RELEASE
mvn versions:set -DnewVersion=版本号
```

## 六 开发安装顺序
首选需要 install

mvn install linzen-common
