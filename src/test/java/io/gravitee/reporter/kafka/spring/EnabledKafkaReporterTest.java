/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.reporter.kafka.spring;

import org.junit.Test;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnabledKafkaReporterTest {

    @Test
    public void shouldEnableWhenPropertyIsTrue() {
        EnabledKafkaReporter condition = new EnabledKafkaReporter();

        ConditionContext conditionContext = mock(ConditionContext.class);
        Environment env = mock(Environment.class);

        when(conditionContext.getEnvironment()).thenReturn(env);
        when(env.getProperty("reporters.kafka.enabled")).thenReturn("true", "True", "other");

        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isTrue();
        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isTrue();
        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isTrue();
    }

    @Test
    public void shouldEnableWhenPropertyIsNotDefined() {
        EnabledKafkaReporter condition = new EnabledKafkaReporter();

        ConditionContext conditionContext = mock(ConditionContext.class);
        Environment env = mock(Environment.class);

        when(conditionContext.getEnvironment()).thenReturn(env);

        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isTrue();
        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isTrue();
    }

    @Test
    public void shouldDisableWhenPropertyIsFalse() {
        EnabledKafkaReporter condition = new EnabledKafkaReporter();

        ConditionContext conditionContext = mock(ConditionContext.class);
        Environment env = mock(Environment.class);

        when(conditionContext.getEnvironment()).thenReturn(env);
        when(env.getProperty("reporters.kafka.enabled")).thenReturn("false", "False");

        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isFalse();
        assertThat(condition.matches(conditionContext, mock(AnnotatedTypeMetadata.class))).isFalse();
    }
}
