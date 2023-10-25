# Changelog

## Unreleased

## Version 1.31.0 (2023-10-24)

### Samplers

- Allow providing a custom sampler as an option for the RuleBasedRoutingSampler
  ([#710](https://github.com/open-telemetry/opentelemetry-java-contrib/pull/710))
- 固定依赖版本[dependencyManagement/build.gradle.kts](dependencyManagement/build.gradle.kts)为`1.31.0`，注意如下两个依赖都需要，否则构建将会出错，参考[1.31.0 opentelemetry-java-instrumentation/examples/extension/build.gradle](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/v1.31.0/examples/extension/build.gradle)：
  - `io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.31.0`
  - `io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:1.31.0-alpha`
- 固定项目版本[version.gradle.kts](version.gradle.kts)为`1.31.0`
