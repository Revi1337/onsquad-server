#!/bin/bash

# Config Environment
HOST_IP=$1

# Download CURL in Background for Sentinel Notification
(apt-get update && apt-get install -y curl) &

# Config failover script
chmod +x /usr/local/etc/failover.sh &&
sed -E -i -e "s#(curl -s )[^:]+#\1${HOST_IP}#" /usr/local/etc/failover.sh &&

# Config sentinel.conf
sed -E -i -e "s/^(sentinel monitor mymaster[[:space:]]+)[^[:space:]]+/\1${HOST_IP}/" -e "s/^(sentinel announce-ip[[:space:]]+).*/\\1${HOST_IP}/" /usr/local/etc/sentinel.conf &&
mkdir -p /etc/redis && cat /usr/local/etc/sentinel.conf > /etc/redis/sentinel.conf &&

# Execute Redis Sentinel
redis-sentinel /etc/redis/sentinel.conf
