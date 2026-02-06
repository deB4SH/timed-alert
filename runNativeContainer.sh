#!/usr/bin/env bash
if [ -x "$(command -v podman)" ]; then
    cli_cmd="podman"
elif [ -x "$(command -v docker)" ]; then
    cli_cmd="docker"
else
    echo "No container cli tool found! Aborting."
    exit -1
fi
${cli_cmd} run -i --rm -p 8080:8080 -e startHour=13 -e startMinute=0 -e endHour=15 -e endMinute=0 -e dayOfAlert=FRIDAY quarkus/timed-alert