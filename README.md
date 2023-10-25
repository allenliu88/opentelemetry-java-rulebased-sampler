# OpenTelemetry Java Rule Based Sampler

This project was initially based on [opentelemetry-java-contrib/samplers](https://github.com/open-telemetry/opentelemetry-java-contrib/tree/main/samplers), we mainly improve the RuleBasedRoutingSampler and it's Provider.

## Build

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

## Getting Started

- Compile and Build: `./gradlew clean && ./gradlew spotlessApply && ./gradlew build`
- Copy and Rename: Copy `samplers/build/libs/opentelemetry-samplers-1.31.0-alpha.jar` and rename it to `opentelemetry-samplers.jar`
- Add following Environment Variables:
  - `OTEL_TRACES_SAMPLER=rule_based_routing_sampler`: the name of the sampler
  - `OTEL_TRACES_SAMPLER_ARG=http.target:/actuator.*,/health.*;http.target:/foo`: the custom rules, eg. `<AttributeKey>:<AttributeValuePattern1>,<AttributeValuePattern2>;<AttributeKey>:<AttributeValuePattern1>,<AttributeValuePattern2>`
  - `OTEL_JAVAAGENT_EXTENSIONS=/agent/opentelemetry-samplers.jar`: the sampler extension jar path, eg. `opentelemetry-samplers.jar`

## Features

- Support custom rules based sampler, eg. drop all spans of a trace which some span's `http.target` attribute value match the pattern `/health.*`.

## Contributing

- [Allen Liu](https://github.com/allenliu88)
