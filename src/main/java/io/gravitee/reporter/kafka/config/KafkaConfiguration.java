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

import io.gravitee.common.util.EnvironmentUtils;
import io.gravitee.reporter.kafka.model.HostAddress;
import io.gravitee.reporter.kafka.model.MessageType;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KafkaConfiguration {

    private static final String PORT_SEPARATOR = ":";

    private ConfigurableEnvironment environment;

    @Value("${reporters.kafka.topic}")
    private String kafkaTopic;

    @Value("${reporters.kafka.java.security.krb5.conf}")
    private String krb5_conf;

    public KafkaConfiguration(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    private List<MessageType> messageTypes;

    public List<MessageType> getMessageTypes() {
        if (messageTypes == null) {
            messageTypes = buildMessageTypes();
        }
        return messageTypes;
    }

    private List<MessageType> buildMessageTypes() {
        String key = String.format("reporters.kafka.type[%s]", 0);
        List<MessageType> res = new ArrayList<>();
        while (environment.containsProperty(key)) {
            res.add(MessageType.getByName(environment.getProperty(key)));
            key = String.format("reporters.kafka.type[%s]", res.size());
        }
        return res;
    }

    /**
     * Kafka broker hosts
     */
    private List<HostAddress> hostsAddresses;

    public List<HostAddress> getHostsAddresses() {
        if (hostsAddresses == null) {
            hostsAddresses = buildHostsAddresses();
        }
        return hostsAddresses;
    }

    private Map<String, String> kafkaConfigMap;

    public Map<String, String> getKafkaConfigMap() {
        if (kafkaConfigMap == null) {
            kafkaConfigMap = buildKafkaProducerConfig();
        }
        return kafkaConfigMap;
    }

    public String getKrb5_conf() {
        return krb5_conf;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    private List<HostAddress> buildHostsAddresses() {
        String key = String.format("reporters.kafka.hosts[%s]", 0);
        List<HostAddress> res = new ArrayList<>();

        while (environment.containsProperty(key)) {
            res.add(buildHostAddress(environment.getProperty(key)));
            key = String.format("reporters.kafka.hosts[%s]", res.size());
        }
        if (res.isEmpty()) {
            throw new IllegalArgumentException("At least one kafka broker node must be specified");
        }
        return res;
    }

    private HostAddress buildHostAddress(String serializedHost) {
        Assert.isTrue(serializedHost.contains(KafkaConfiguration.PORT_SEPARATOR), "Kafka broker node does not respect hostname:port syntax");
        String[] hostParts = serializedHost.split(KafkaConfiguration.PORT_SEPARATOR);
        String hostname = hostParts[0].toLowerCase();
        Integer port = Integer.parseInt(hostParts[1].trim());
        return new HostAddress(hostname, port);
    }

    private Map<String, String> buildKafkaProducerConfig() {
        Map<String, String> configProducer = new HashMap<>();
        configProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HostAddress.stringifyHostAddresses(getHostsAddresses()));
        configProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonObjectSerializer.class.getCanonicalName());
        configProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonObjectSerializer.class.getCanonicalName());

        Map<String, Object> settings = EnvironmentUtils.getPropertiesStartingWith(environment, "reporters.kafka.settings");
        configProducer.putAll(settings.entrySet().stream().collect(Collectors.toMap(e -> buildkafkaKey(e.getKey()), e -> e.getValue().toString())));

        if (!StringUtils.isEmpty(krb5_conf)) {
            System.setProperty("java.security.krb5.conf", krb5_conf);
        }
        return configProducer;
    }

    private String buildkafkaKey(String key) {
        return key
                .replaceAll("reporters.kafka.settings.", "")
                .replaceAll("_",".");
    }

}
