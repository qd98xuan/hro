> 特别说明：源码、JDK、MySQL、Redis等安装或存放路径禁止包含中文、空格、特殊字符等

## 一 项目结构

```text
linzen_scheduletask
    ├── linzen-scheduletask-client - 调度客户端配置模块
    ├── linzen-scheduletask-model- 实体模型模块
    ├── xxl-job-admin - 调度服务端
    └── xxl-job-core - 调度服务端核心模块
```

## 二 环境要求

### 2.1 开发环境

| 类目 | 版本或建议 | 
| --- | --- |
| 电脑配置 | 建议开发电脑I3及以上CPU，内存32G及以上 |
| 操作系统 | Windows 10/11，MacOS |
| JDK | 建议使用1.8.0_281及以上版本，可使用Eclipse Temurin JDK 8、Alibaba Dragonwell 8、BiSheng JDK 8等 |
| Maven | 3.6.3及以上版本 |
| 开发工具 | IDEA2020及以上版本或 Eclipse、 Spring Tool Suite等IDE工具 |

### 2.2 运行环境

> 服务端运行环境，适用于测试或生产环境

| 类目 | 版本或建议 |
| --- | --- |
| 服务器配置 | 最低配置要求：8c/32G/50G |
| 操作系统 | 推荐使用Ubuntu 18.0.4及以上版本，兼容统信UOS，OpenEuler，麒麟服务器版等信创环境 |
| JRE | 建议使用1.8.0_281及以上版本，如Eclipse Temurin JRE 8/11/17、Alibaba Dragonwell 8/11/17、BiSheng JRE 8/11/17 |
| Redis | 4.0.x+ |
| 数据库 | 可选MySQL 5.7.x/8.0.x(默认)、SQLServer 2012+、Oracle 11g、PostgreSQL 12+、达梦数据库(DM8)、人大金仓数据库(KingbaseES_V8R6) |

## 三 关联项目

> 为以下项目提供基础依赖

| 项目 | 分支 | 分支(Coding) | 说明 |
| --- | --- | --- | --- |
| linzen-common | v0.0.x | v0.0.x-stable | java基础依赖项目源码 |
| linzen-java-boot | v0.0.x | v0.0.x-stable | java-boot单体后端项目源码 |
| linzen-java-cloud | v0.0.x | v0.0.x-stable | java-cloud微服务后端项目源码 |

## 四 使用方式

> 本项目为任务调度的基础依赖和服务端，<br/>作为客户端依赖时需要上传到私服或使用本地安装的方式引用该项目，<br/>作为服务端时需要单独部署

### 4.1 作为客户端依赖

#### 4.1.1 前置条件

##### 4.1.1.1 本地安装linzen-core

IDEA中打开 `linzen-common` 项目， 双击右侧 `Maven` 中 `linzen-common` > `linzen-boot-common` > `linzen-common-core` > `Lifecycle` > `install`，将 `linzen-common-core` 包安装至本地

##### 4.1.1.2 本地安装dependencies

IDEA中打开 `linzen-common` 项目，双击右侧 `Maven` 中 `linzen-common` > `linzen-dependencies` > `Lifecycle` > `install`，将 `linzen-dependencies` 包安装至本地

#### 4.1.2 本地安装

在IDEA中，双击右侧 `Maven` 中 `linzen-scheduletask-starter` > `Lifecycle` > `install`，将 `linzen-scheduletask-client` 包安装至本地

#### 4.1.3 私服发布

##### 4.1.3.1 配置Maven

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

##### 4.1.3.3 发布到私服

在IDEA中，双击右侧 `Maven` 中 `linzen-scheduletask-starter` > `Lifecycle` > `deploy` 发布至私服。

### 4.2 作为服务端

#### 4.2.1 数据源配置

打开 `xxl-job-admin/src/main/resources/application-dev.yml` 修改数据源，配置示例如下：

##### 4.2.1.1 MySQL数据库

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/linzen_xxjob_init?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false
    username: dbuser
    password: dbpasswd
    driver-class-name: com.mysql.cj.jdbc.Driver
```

##### 4.2.1.2 SQLServer数据库

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://127.0.0.1:1433;SelectMethod=cursor;Databasename=linzen_xxjob_init
    username: dbuser
    password: dbpasswd
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

##### 4.2.1.3 Oracle数据库

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@127.0.0.1:1521:orcl
    username: dbuser
    password: dbpasswd
    driver-class-name: oracle.jdbc.OracleDriver
```

##### 4.2.1.4 PostgreSQL数据库

```yaml
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5432/linzen_xxjob_init
    username: dbuser
    password: dbpasswd
    driver-class-name: org.postgresql.Driver
```

##### 4.2.1.5 人大金仓KingbaseES数据库

```yaml
spring:
  datasource:
    url: jdbc:kingbase8://127.0.0.1:54321/linzen_xxjob_init
    username: dbuser
    password: dbpasswd
    driver-class-name: com.kingbase8.Driver
```

##### 4.2.1.6 达梦dm8数据库

```yaml
spring:
  datasource:
    url: jdbc:dm://127.0.0.1:5236/linzen_xxjob_init?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8
    username: dbuser
    password: dbpasswd
    driver-class-name: dm.jdbc.driver.DmDriver
```

#### 4.2.2 打包部署

在IDEA中，在左侧 `Project` 中，右击 `linzen-scheduletask` > `xxl-job-admin` > `pom.xml` 并选择 `Add as Maven Project` 将 `xxl-job-admin` 转为 Maven 项目，然后双击右侧 `Maven` 中 `xxl-job-admin` > `Lifecycle` > `package`， 将 `/xxl-job-admin/target/xxl-job-admin-3.5.0-RELEASE.jar` 上传至服务器部署即可。

#### 4.2.3 关联项目配置

配置如下所示

```yaml
# ===================== 任务调度配置 =====================
xxl:
  job:
    accessToken: ''
    i18n: zh_CN
    logretentiondays: 30
    triggerpool:
      fast:
        max: 200
      slow:
        max: 100
    admin:
      # xxl-job服务端地址
      addresses: http://127.0.0.1:30020/xxl-job-admin/
    executor:
      address: ''
      appname: xxl-job-executor-sample1
      ip: ''
      logpath: /data/applogs/xxl-job/jobhandler
      logretentiondays: 30
      port: 9999
```

##### 4.2.3.1 linzen-java-boot项目

IDEA打开 `linzen-java-boot` 项目, 编辑 `linzen-admin/src/main/resources/application-x.yml` 文件( `application-x.yml` 为环境配置，如 `application-dev.yml` )

##### 4.2.3.2 linzen-java-cloud项目

登录 `Nacos` 控制台，依次点击 `配置管理` >  `配置列表` > `develop`，编辑 `datasource-scheduletask.yaml`。
