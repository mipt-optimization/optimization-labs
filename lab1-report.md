A small utility script:

```shell
equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ cat measure.sh 
#! /usr/bin/env bash
curl -o response.txt -s -w 'time_total: %{time_total}s\n' http://localhost:8080/lab1/weather?days=$1
```

Some performance numbers for the original version:

```shell
equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 5
time_total: 1.547330s

equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 10
time_total: 3.016477s

equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 50
time_total: 11.988562s
```

And for the modified one:

```shell
equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 5
time_total: 1.388640s

equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 10
time_total: 1.254255s

equocredite@equocredite:~/mipt/msc/term3/opt/mipt-java-opt-labs$ ./measure.sh 50
time_total: 3.211614s
```
