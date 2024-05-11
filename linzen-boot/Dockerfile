# 基础镜像
FROM eclipse-temurin:8u392-b08-jre-centos7
LABEL maintainer=linzen-team

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
	&& echo 'Asia/Shanghai' >/etc/timezone

# 解决连接SQLServer安全错误
# 如您使用SQLServer数据库，把以下注释取消
# COPY security/java.security /opt/java/openjdk/lib/security

# 指定运行时的工作目录
WORKDIR /wwwroot/linzensoft/linzen-server/linzen-boot

# 将构建产物jar包拷贝到运行时目录中
COPY linzen-admin/target/*.jar ./linzen-admin.jar

# 指定容器内运行端口
EXPOSE 30000

# 指定容器启动时要运行的命令
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom -jar linzen-admin.jar"]
