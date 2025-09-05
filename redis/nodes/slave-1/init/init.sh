#!/bin/bash

# Config Environment
HOST_IP=$1

# Config Slave redis.conf
sed -E -i -e "s/^(replicaof[[:space:]]+)[^[:space:]]+/\\1${HOST_IP}/" -e "s/^(replica-announce-ip[[:space:]]+).*/\\1${HOST_IP}/" /usr/local/etc/redis.conf &&
cp /usr/local/etc/redis.conf /data/redis.conf &&

# Execute Redis Slave
redis-server /data/redis.conf
