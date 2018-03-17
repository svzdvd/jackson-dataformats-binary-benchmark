#### JMH micro benchmarks for Jackson ObjectMapper data formats:

* Json
* CBOR
* Smile
* MessagePack

#### Usage:

`./gradlew`

#### Results for 100KB Json file:

| Serialization to String | Score (ops/s) | ± Error |
| ----------------------- | ------------: | ------: |
| Json                    | 2741.493      | 293.055 |
| Smile                   | 2694.873      | 203.067 |
| CBOR                    | 2364.787      | 358.112 |
| MessagePack             | 1006.618      | 109.385 |

| Serialization to byte[] | Score (ops/s) | ± Error |
| ----------------------- | ------------: | ------: |
| Smile                   | 4955.446      | 672.059 |
| CBOR                    | 4806.494      | 286.930 |
| MessagePack             | 1437.121      | 255.366 |

| Deserialization from String | Score (ops/s) | ± Error |
| --------------------------- | ------------: | ------: |
| Smile                       | 1722.397      | 62.417  |
| Json                        | 1596.759      | 280.759 |
| CBOR                        | 1512.627      | 137.054 |
| MessagePack                 | 1105.320      | 37.860  |

| Deserialization from byte[] | Score (ops/s) | ± Error |
| --------------------------- | ------------: | ------: |
| Smile                       | 3655.229      | 446.840 |
| CBOR                        | 3066.597      | 495.526 |
| MessagePack                 | 1687.267      | 90.730  |
