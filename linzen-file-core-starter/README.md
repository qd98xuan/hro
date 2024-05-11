> 特别说明：源码、JDK、MySQL、Redis等安装或存放路径禁止包含中文、空格、特殊字符等

## 一 项目结构

```text
linzen-file-core-starter
    ├── aspect- 切面层
    ├── exception- 自定义异常
    ├── platform - 存储平台实现层
    └── recorder- 记录器
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
| linzen-common | v0.0.x | v0.0.x-stable | java基础依赖项目源码 |
| linzen-java-boot | v0.0.x | v0.0.x-stable | java-boot单体后端项目源码 |
| linzen-java-cloud | v0.0.x | v0.0.x-stable | java-cloud微服务后端项目源码 |

## 四 使用方式

### 4.1 前置条件

#### 4.1.1 本地安装linzen-core

IDEA中打开 `linzen-common` 项目， 双击右侧 `Maven` 中 `linzen-common` > `linzen-boot-common` > `linzen-common-core` > `Lifecycle` > `install`，将 `linzen-common-core` 包安装至本地

#### 4.1.2 本地安装dependencies

IDEA中打开 `linzen-common` 项目，双击右侧 `Maven` 中 `linzen-common` > `linzen-dependencies` > `Lifecycle` > `install`，将 `linzen-dependencies` 包安装至本地

### 4.2 本地安装

在IDEA中，双击右侧 `Maven` 中`linzen-file-core-starter` > `Lifecycle` > `install`，将`linzen-file-core-starter`包安装至本地

### 4.3 私服发布

#### 4.3.1 配置Maven

打开Maven安装目录中的 `conf/setttings.xml` ，

在 `<servers></servers>`节点增加 `<server></server>` ，如下所示:

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

在IDEA中，双击右侧 `Maven` 中 `linzen-file-core-starter` > `Lifecycle` > `deploy` 发布至私服。
