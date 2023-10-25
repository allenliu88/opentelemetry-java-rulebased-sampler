/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.contrib.sampler;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurationException;
import io.opentelemetry.sdk.autoconfigure.spi.traces.ConfigurableSamplerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuleBasedRoutingSamplerProvider implements ConfigurableSamplerProvider {
  private static final Logger LOGGER =
      Logger.getLogger(RuleBasedRoutingSamplerProvider.class.getName());

  @Override
  public Sampler createSampler(ConfigProperties config) {
    Sampler fallbackSampler = Sampler.alwaysOn();

    String spanKindString = config.getString("otel.traces.sampler.span_kind", "SERVER");
    SpanKind spanKind;
    try {
      spanKind = SpanKind.valueOf(spanKindString);
    } catch (NoSuchElementException ex) {
      throw new ConfigurationException("Invalid span_kind: " + spanKindString, ex);
    }

    RuleBasedRoutingSamplerBuilder builder =
        RuleBasedRoutingSampler.builder(spanKind, fallbackSampler);

    String rules =
        config.getString(
            "otel.traces.sampler.arg", "http.target:/actuator.*,*/health*;http.target:/foo");

    // 最外层分号分隔不同的规则
    for (String rule : rules.split(";")) {
      // 单个规则中冒号分隔属性及规则值
      String[] pair = rule.split(":");
      // 规则第一段标识属性
      String attribute = pair[0].trim();

      // 规则第二段标识规则值，多个规则值之间通过逗号分隔
      for (String value : pair[1].split(",")) {
        String pattern = value.trim();
        LOGGER.log(
            Level.INFO,
            String.format("======> attribute %s, and pattern %s <======", attribute, value));
        if (attribute == null || pattern == null) {
          throw new ConfigurationException(
              "drop_rule entries require attribute and pattern fields, eg. -Dotel.traces.sampler.arg=http.target:/actuator.*,*/health*;http.target:/foo");
        }
        builder.drop(AttributeKey.stringKey(attribute), pattern);
      }
    }

    return builder.build();
  }

  @Override
  public String getName() {
    return "rule_based_routing_sampler";
  }
}
