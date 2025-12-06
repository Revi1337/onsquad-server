#!/bin/bash

# Config Environment
HOST_IP=$1

# Config Master redis.conf
sed -E -i "s/^(replica-announce-ip[[:space:]]+).*/\\1${HOST_IP}/" /usr/local/etc/redis.conf &&
cp /usr/local/etc/redis.conf /data/redis.conf &&

# Execute Redis Master
redis-server /data/redis.conf
