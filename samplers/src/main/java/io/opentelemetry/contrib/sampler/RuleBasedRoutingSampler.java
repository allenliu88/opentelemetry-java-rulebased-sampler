/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.contrib.sampler;

import static io.opentelemetry.semconv.SemanticAttributes.THREAD_NAME;
import static java.util.Objects.requireNonNull;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This sampler accepts a list of {@link SamplingRule}s and tries to match every proposed span
 * against those rules. Every rule describes a span's attribute, a pattern against which to match
 * attribute's value, and a sampler that will make a decision about given span if match was
 * successful.
 *
 * <p>Matching is performed by {@link java.util.regex.Pattern}.
 *
 * <p>Provided span kind is checked first and if differs from the one given to {@link
 * #builder(SpanKind, Sampler)}, the default fallback sampler will make a decision.
 *
 * <p>Note that only attributes that were set on {@link io.opentelemetry.api.trace.SpanBuilder} will
 * be taken into account, attributes set after the span has been started are not used
 *
 * <p>If none of the rules matched, the default fallback sampler will make a decision.
 */
public final class RuleBasedRoutingSampler implements Sampler {
  private static final Logger LOGGER = Logger.getLogger(RuleBasedRoutingSampler.class.getName());
  private static final ThreadLocal<Set<String>> dropHolder =
      ThreadLocal.withInitial(() -> new HashSet<>());
  // 采样规则，其内包含了匹配后的Sampler
  private final List<SamplingRule> rules;
  private final SpanKind kind;
  private final Sampler fallback;

  RuleBasedRoutingSampler(List<SamplingRule> rules, SpanKind kind, Sampler fallback) {
    this.kind = requireNonNull(kind);
    this.fallback = requireNonNull(fallback);
    this.rules = requireNonNull(rules);
  }

  public static RuleBasedRoutingSamplerBuilder builder(SpanKind kind, Sampler fallback) {
    return new RuleBasedRoutingSamplerBuilder(
        requireNonNull(kind, "span kind must not be null"),
        requireNonNull(fallback, "fallback sampler must not be null"));
  }

  @Override
  public SamplingResult shouldSample(
      Context parentContext,
      String traceId,
      String name,
      SpanKind spanKind,
      Attributes attributes,
      List<LinkData> parentLinks) {
    LOGGER.log(
        Level.INFO,
        String.format(
            "======> traceId=%s, name=%s, spanKind=%s, attributes=%s, parentContext=%s, rootContext=%s",
            traceId, name, spanKind, attributes, parentContext, Context.root()));

    // 如果某一条链路存在屏蔽规则，则屏蔽整条链路上的所有Span
    if (dropHolder.get().contains(traceId)) {
      LOGGER.log(
          Level.INFO,
          String.format(
              "current span name is %s which belongs to traceId=%s will be dropped.",
              name, traceId));
      return Sampler.alwaysOff()
          .shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
    }

    // 仅处理特定SpanKind，例如，SERVER，其他走fallback逻辑
    if (kind != spanKind) {
      return fallback.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
    }
    for (SamplingRule samplingRule : rules) {
      String attributeValue;
      if (samplingRule.attributeKey.getKey().equals(THREAD_NAME.getKey())) {
        attributeValue = Thread.currentThread().getName();
      } else {
        attributeValue = attributes.get(samplingRule.attributeKey);
      }
      // 跳过未指定属性值的规则
      if (attributeValue == null) {
        continue;
      }

      if (samplingRule.pattern.matcher(attributeValue).find()) {
        if (Sampler.alwaysOff().equals(samplingRule.delegate)) {
          // 记录被丢弃的链路ID，后续该TraceID的所有Span均丢弃
          dropHolder.get().add(traceId);
        }

        // 匹配后，通过代理Sampler来执行，例如，Sampler.alwaysOff()
        return samplingRule.delegate.shouldSample(
            parentContext, traceId, name, spanKind, attributes, parentLinks);
      }
    }

    // 默认Fallback逻辑，一般都是Sampler.alwaysOn()
    return fallback.shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
  }

  @Override
  public String getDescription() {
    return "RuleBasedRoutingSampler{"
        + "rules="
        + rules
        + ", kind="
        + kind
        + ", fallback="
        + fallback
        + '}';
  }

  @Override
  public String toString() {
    return getDescription();
  }
}
