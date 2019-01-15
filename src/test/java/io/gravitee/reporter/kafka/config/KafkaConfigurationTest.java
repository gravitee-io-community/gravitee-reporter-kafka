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
package io.gravitee.reporter.kafka.config;

import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.node.container.spring.env.EnvironmentConfiguration;
import io.gravitee.reporter.kafka.model.MessageType;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {EnvironmentConfiguration.class,KafkaConfiguration.class, GatewayConfiguration.class})
public class KafkaConfigurationTest {

    @Inject
    KafkaConfiguration kafkaConfiguration;

    @BeforeClass
    public static void setUp() throws FileNotFoundException {
        File graviteeConf = ResourceUtils.getFile("classpath:gravitee.yml");
        System.setProperty("gravitee.conf", graviteeConf.getAbsolutePath());
        System.setProperty("reporters_kafka_settings_security_protocol2", "SASL_SSL2");
    }

    @Test
    public void shouldLoadProperties() throws IOException {
        assertThat(kafkaConfiguration.getKrb5_conf()).isEqualTo("krb5.conf");
        assertThat(kafkaConfiguration.getKafkaTopic()).isEqualTo("gateway_log_topic");
        assertThat(kafkaConfiguration.getHostsAddresses()).isNotNull();
        assertThat(kafkaConfiguration.getHostsAddresses()).extracting("hostname", "port").containsExactly(tuple("node1", 6062), tuple("node2", 6062));
        assertThat(kafkaConfiguration.getKafkaConfigMap()).isNotNull();
        assertThat(kafkaConfiguration.getMessageTypes()).isNotNull();
        assertThat(kafkaConfiguration.getMessageTypes()).containsExactly(MessageType.getByName("log"), MessageType.getByName("monitor"));
        assertThat(kafkaConfiguration.getKafkaConfigMap().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("node1:6062,node2:6062");
        assertThat(kafkaConfiguration.getKafkaConfigMap().get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)).isEqualTo(JsonObjectSerializer.class.getCanonicalName());
        assertThat(kafkaConfiguration.getKafkaConfigMap().get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)).isEqualTo(JsonObjectSerializer.class.getCanonicalName());
        assertThat(kafkaConfiguration.getKafkaConfigMap().get("acks")).isEqualTo("1");
        assertThat(kafkaConfiguration.getKafkaConfigMap().get("security.protocol")).isEqualTo("SASL_SSL");
        assertThat(kafkaConfiguration.getKafkaConfigMap().get("sasl.jaas.config")).isEqualTo("com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true refreshKrb5Config=true storeKey=true serviceName=\"kafka\" keyTab=\"key.keytab\" principal=\"foo@DOMAIN.COM\";");
        assertThat(System.getProperty("java.security.krb5.conf")).isEqualTo("krb5.conf");
    }
    @Test
    public void shouldLoadSettingsWithUnderscore()  {
         assertThat(kafkaConfiguration.getKafkaConfigMap().get("security.protocol2")).isEqualTo("SASL_SSL2");
    }
    @Test
    public void shouldRaiseExceptionWhenNoKafkaBroker() {
        ConfigurableEnvironment env = mock(ConfigurableEnvironment.class);
        KafkaConfiguration config = new KafkaConfiguration(env);

        when(env.containsProperty("reporters.kafka.hosts[0]")).thenReturn(false);

        try {
            config.getHostsAddresses();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isNotEmpty();
        }

    }

    @Test
    public void shouldRaiseExceptionWhenKafkaBrokerDoesNotRespectSyntax() {
        ConfigurableEnvironment env = mock(ConfigurableEnvironment.class);
        KafkaConfiguration config = new KafkaConfiguration(env);

        when(env.containsProperty("reporters.kafka.hosts[0]")).thenReturn(true);
        when(env.getProperty("reporters.kafka.hosts[0]")).thenReturn("node1");

        try {
            config.getHostsAddresses();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isNotEmpty();
        }

    }

}
