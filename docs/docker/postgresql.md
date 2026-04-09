开发环境专用网络
```shell
  docker network create d-now-network
```

## 1. 拉取镜像

```shell
  docker pull postgres:17.6
```

## 2. 创建持久化数据卷

```shell
  docker volume create d-now-pgdata
```

## 3. 执行创建容器脚本

```shell
  docker run -d \  # 后台启动容器
   --name d-now-postgres \ # 容器名称 d-开发环境 now-项目名称
   --network d-now-network \ # 容器专用网络
   -p 55432:5432 \ # 容器端口
   -e POSTGRES_USER=now \ # 数据库登录用户
   -e POSTGRES_PASSWORD=now1105 \ # 管理员密码
   -e POSTGRES_DB =now_admin \ 
   -e TZ=Asia/Shanghai \ # 时区
   -e POSTGRES_INITDB_ARGS="--data-checksums" \
   -v pgdata:/var/lib/postgresql/data \
   -v /etc/localtime:/etc/localtime:ro \
   --restart=unless=stopped \
   postgres:17.6
   
   docker run -d \
   --name d-now-postgres \
   --network d-now-network \
   -p 55432:5432 \
   -e POSTGRES_USER=now \
   -e POSTGRES_PASSWORD=now1105 \
   -e POSTGRES_DB=now_admin \
   -e TZ=Asia/Shanghai \
   -v d-now-pgdata:/var/lib/postgresql/data \
   -v /etc/localtime:/etc/localtime:ro \
   --restart=unless-stopped \
   postgres:17.6
   
   ```