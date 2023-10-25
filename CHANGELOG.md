# Changelog

## Unreleased

## Version 1.31.0 (2023-10-24)

### Samplers

- Support custom rules based sampler, eg. drop all spans of a trace which some span's `http.target` attribute value match the pattern `/health.*`.
- Fixed the dependency version in [dependencyManagement/build.gradle.kts](dependencyManagement/build.gradle.kts) to `1.31.0`, the following two is required, otherwise the building will be failed, Ref. [1.31.0 opentelemetry-java-instrumentation/examples/extension/build.gradle](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/v1.31.0/examples/extension/build.gradle):
  - `io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.31.0`
  - `io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom-alpha:1.31.0-alpha`
- First release, the version is aligned with opentelemetry-java-instrumentation, eg. [version.gradle.kts](version.gradle.kts) is `1.31.0`.
