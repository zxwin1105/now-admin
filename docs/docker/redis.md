```shell
docker run -d \
  --name d-now-redis \
  --network d-now-network \
  -p 56379:6379 \
  -e TZ=Asia/Shanghai \
  -v d-now-redisdata:/data \
  --restart unless-stopped \
  redis:7.4.1 redis-server --appendonly yes --requirepass now1105
``