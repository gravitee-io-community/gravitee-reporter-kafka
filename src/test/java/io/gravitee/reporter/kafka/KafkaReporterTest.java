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
package io.gravitee.reporter.kafka;

import io.gravitee.reporter.api.http.Metrics;
import io.gravitee.reporter.api.log.Log;
import io.gravitee.reporter.kafka.config.KafkaConfiguration;
import io.gravitee.reporter.kafka.model.MessageType;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class KafkaReporterTest {

    @InjectMocks
    private KafkaReporter reporter;

    @Mock
    private KafkaConfiguration configuration;

    @Mock
    private ConfigurableEnvironment env;

    @Mock
    private KafkaProducer kafkaProducer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldHandleSpecifiedTypeOnly() {
        List<MessageType> messageTypes = new ArrayList<>();
        messageTypes.add(MessageType.LOG);
        when(configuration.getMessageTypes()).thenReturn(messageTypes);

        Assertions.assertThat(reporter.canHandle(new Log(1L))).isTrue();
        Assertions.assertThat(reporter.canHandle(Metrics.on(1L).build())).isFalse();
    }

    @Test
    public void shouldHandleAllWhenNoTypeDefined() {
        when(configuration.getMessageTypes()).thenReturn(new ArrayList<>());
        Assertions.assertThat(reporter.canHandle(new Log(1L))).isTrue();
        Assertions.assertThat(reporter.canHandle(Metrics.on(1L).build())).isTrue();
    }
}
