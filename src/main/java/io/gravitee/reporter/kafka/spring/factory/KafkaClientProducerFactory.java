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
package io.gravitee.reporter.kafka.spring.factory;

import io.gravitee.reporter.kafka.config.KafkaConfiguration;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class KafkaClientProducerFactory extends AbstractFactoryBean<KafkaProducer> {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaClientProducerFactory.class);

    private KafkaConfiguration kafkaConfiguration;

    private Vertx vertx;

    public KafkaClientProducerFactory(KafkaConfiguration kafkaConfiguration, Vertx vertx) {
        this.kafkaConfiguration = kafkaConfiguration;
        this.vertx = vertx;
    }

    @Override
    public Class<KafkaProducer> getObjectType() {
        return KafkaProducer.class;
    }

    @Override
    protected KafkaProducer createInstance() throws Exception {
        KafkaProducer<String, JsonObject> producer = KafkaProducer.create(vertx, kafkaConfiguration.getKafkaConfigMap());
        producer.exceptionHandler(e -> {
            LOGGER.error(e.getMessage(), e.getCause());
        });
        return producer;
    }
}
