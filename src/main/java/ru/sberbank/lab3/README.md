## Lab 3

Результаты запуска

```
Benchmark                                     Mode  Cnt    Score    Error  Units
TestSerializers.testDeserializeJSON      avgt   10  823,242 ± 32,427  ns/op
TestSerializers.testDeserializeProtobuf  avgt   10  437,387 ± 17,752  ns/op
TestSerializers.testSerializeJSON        avgt   10  438,923 ± 42,028  ns/op
TestSerializers.testSerializeProtobuf    avgt   10    2,088 ±  0,114  ns/op
```

- При сериализации и десериализации Protobuf оказался значительно быстрее, чем JSON;
- Тогда Protobuf лучше использовать, когда необходимо де/сериализовывать за минимальное время;
- Также так как объект после Protobuf сериализации занимает меньше места, чем объект после JSON сериализации, то при ограничении числа передаваемых данных Protobuf будет лучше;
- JSON лучше взять, когда нет жетского ограничения на время де/сериализации;
- Также так как формат JSON является человекочитаемым, то его лушче будет использовать при необходимости последющего чтения данных человеком.
