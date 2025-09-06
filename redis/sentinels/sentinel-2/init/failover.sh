#!/bin/bash
# $1: Event Type (+failover-end)
# $2: Master Name
# $3: Previous Master IP
# $4: Previous Master PORT
# $5: New Master IP
# $6: New Master PORT

if [ "$1" == "+failover-end" ]; then
    echo '+failover-end EventListener Invoked'
    curl -s 192.168.45.214:8080/failover > /dev/null 2>&1
    exit 0
fi


#if [ "$1" != "+failover-end" ]; then
#    echo "이벤트 타입: $1. +failover-end 아니므로 종료합니다."
#    exit 0
#fi
#
#MASTER_INFO=$(redis-cli -p 26379 --raw SENTINEL get-master-addr-by-name mymaster)
#if [ -z "$MASTER_INFO" ]; then
#    echo "마스터 정보 가져오기 실패. 종료합니다."
#    exit 0
#fi
#
#MASTER_IP=$(echo "$MASTER_INFO" | sed -n '1p')
#MASTER_PORT=$(echo "$MASTER_INFO" | sed -n '2p')
#
#LOCK_KEY="failover-lock-mymaster"
#LOCK_ACQUIRED=$(redis-cli -h "$MASTER_IP" -p "$MASTER_PORT" --raw SET "$LOCK_KEY" "locked" NX EX 10)
#
#if [ -z "$LOCK_ACQUIRED" ]; then
#    echo "락 획득 실패. 종료합니다."
#    exit 0
#fi
#
#echo "락 획득 성공. 파일에 기록합니다."
#echo "$1, $2, $3, $4, $5, $6" > /usr/local/etc/result.txt
#redis-cli -h "$MASTER_IP" -p "$MASTER_PORT" DEL "$LOCK_KEY"
#exit 1
