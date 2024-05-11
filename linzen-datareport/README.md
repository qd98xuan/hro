# linzen-datareport

> 特别说明：源码、JDK、MySQL、Redis等存放路径禁止包含中文、空格、特殊字符等

## 环境要求

> 官方建议： JDK版本不低于 `1.8.0_281`版本，可使用`OpenJDK 8`、`Alibaba Dragonwell 8`、`BiShengJDK 8`

项目  | 推荐版本                              | 说明
-----|-----------------------------------| -------------
JDK  | 1.8.0_281            | JAVA环境依赖(需配置环境变量)
Maven  | 3.6.3                             | 项目构建(需配置环境变量)
Redis  | 3.2.100(Windows)/6.0.x(Linux,Mac) |
MySQL  | 5.7.x+                            | 数据库任选一(默认)
SQLServer  | 2012+                             | 数据库任选一
Oracle  | 11g+                              | 数据库任选一
PostgreSQL  | 12+                               | 数据库任选一
达梦数据库 | DM8                               | 数据库任选一
人大金库 | KingbaseES V8 R6                  | 数据库任选一

## 工具推荐
> 为防止无法正常下载Maven以来，请使用以下IDE版本

IDEA版本  | Maven版本
-----|-------- | 
IDEA2020及以上版本  | Maven 3.6.3及以上版本 |

## 关联项目

| 项目 | 分支 | 分支(Coding) | 说明 |
| --- | --- | --- | --- |
| linzen-java-boot | v3.6.x | v3.6.x-stable | Java单体后端项目源码 |
| linzen-java-cloud | v3.6.x | v3.6.x-stable | Java微服务后端项目源码 |
| linzen-dotnet | v3.6.x | v3.6.x-stable | .NET单体后端项目源码 |
| linzen-dotnet-cloud | v3.6.x | v3.6.x-stable | .NET微服务后端项目源码 |

## 使用说明

## Maven私服配置

> 通过官方私服下载依赖完成后，由于IDEA的缓存可能会出现部分报红，重启IDEA即可

#### LINZEN官方Maven私服与阿里云Maven私服、Maven官方的包区别

- com.sqlserver:sqljdbc4:4.0
- com.oracle:ojdbc6:11.2.0
- com.dm:DmJdbcDriver18:1.8.0
- com.kingbase8:kingbase8-jdbc:1.0
- dingtalk-sdk-java:taobao-sdk-java-source:1.0
- dingtalk-sdk-java:taobao-sdk-java:1.0
- yozo:signclient:3.0.1

1、打开`maven`下`conf/settings.xml`文件

2、 在`<servers></servers>`中添加

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
```

3、在`<mirrors></mirrors>`中添加

```xml
<mirror>
  <id>nexus</id>
  <name>nexus maven</name>
  <url>http://127.0.0.1:8081/repository/maven-public/</url>
  <mirrorOf>*</mirrorOf>
</mirror>
```

#### 环境配置

- 打开`ureport2-console/src/main/resources`中的`application.yml`
- 修改配置
  - 端口配置
  - `数据库`配置和`Redis`配置
  - 是否开启多租户  
- 打开`ureport2-console/src/main/java/com.bstek.ureport.console/DataReportApplication`运行

# 数据集条件用法

例子

```sql
${
"select * from base_user where 1=1" +(emptyparam("F_Gender")==true?"":" and F_Gender=:F_Gender") +(emptyparam("F_RealName")==true?"":" and F_RealName like :F_RealName") +(emptyparam("F_QuickQuery")==true?"":" and F_QuickQuery like :F_QuickQuery")
}
```
对应参数填写

参数名  | 数据类型  | 默认值
-----|-------- | -------------
F_RealName  | String |
F_QuickQuery  | String |
F_Gender  | Integer |
