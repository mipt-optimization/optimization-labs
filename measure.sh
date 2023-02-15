#! /usr/bin/env bash
curl -o response.txt -s -w 'time_total: %{time_total}s\n' http://localhost:8080/lab1/weather?days=$1
