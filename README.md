****# OpenTelemetry Java Rule Based Sampler

This project is intended to provide helpful libraries and standalone OpenTelemetry-based utilities that don't fit
the express scope of the [OpenTelemetry Java](https://github.com/open-telemetry/opentelemetry-java) or
[Java Instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation) projects.  If you need an
easier way to bring observability to remote JVM-based applications and workflows that isn't easily satisfied by an SDK
feature or via instrumentation, this project is hopefully for you.

## Getting Started

```bash
# Apply formatting
$ ./gradlew spotlessApply

# Build the complete project
$ ./gradlew build

# Run integration tests
$ ./gradlew integrationTest

# Clean artifacts
$ ./gradlew clean
```

## Provided Libraries

### Samplers

- 编译构建：`./gradlew clean && ./gradlew spotlessApply && ./gradlew build`
- 手工拷贝：`samplers/build/libs/opentelemetry-samplers-1.31.0-alpha.jar`至目标位置，并更名为`opentelemetry-samplers.jar`
- 增加如下环境变量：
  - `OTEL_TRACES_SAMPLER=rule_based_routing_sampler`：指定当前Sampler名称
  - `OTEL_TRACES_SAMPLER_ARG=http.target:/actuator.*,/health.*;http.target:/foo`：指定规则，模板为`<AttributeKey>:<AttributeValuePattern1>,<AttributeValuePattern2>;<AttributeKey>:<AttributeValuePattern1>,<AttributeValuePattern2>`
  - `OTEL_JAVAAGENT_EXTENSIONS=/agent/opentelemetry-samplers.jar`：指定当前扩展库路径，本例中是将如上名称调整成了更简洁的`opentelemetry-samplers.jar`

## Contributing
