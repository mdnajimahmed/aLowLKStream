
- kafka-topics --bootstrap-server=localhost:9093 --create --topic alowl --partitions 1

- kafka-topics --bootstrap-server=localhost:9093 --list

- kafka-console-producer --bootstrap-server=localhost:9093 --topic alowl

- kafka-console-consumer --bootstrap-server=localhost:9093 --topic alowl --from-beginning
- kafka-console-consumer --bootstrap-server=localhost:9093 --topic alowlOut --from-beginning

- kafka-topics --bootstrap-server=localhost:9093 --delete --topic alowl