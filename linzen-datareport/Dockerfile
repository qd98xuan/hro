# 基础镜像
FROM eclipse-temurin:8u392-b08-jre-centos7
LABEL maintainer=linzen-team

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
	&& echo 'Asia/Shanghai' >/etc/timezone

# 指定运行时的工作目录
WORKDIR /wwwroot/linzensoft/linzen-server/linzen-datareport

# 将构建产物jar包拷贝到运行时目录中
COPY ureport2-console/target/*.jar ./linzen-datareport.jar

# 指定容器内运行端口
EXPOSE 30007

# 指定容器启动时要运行的命令
ENTRYPOINT ["/bin/sh","-c","java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom -jar linzen-datareport.jar"]