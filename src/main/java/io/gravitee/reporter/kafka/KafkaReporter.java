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

import io.gravitee.common.service.AbstractService;
import io.gravitee.node.api.monitor.Monitor;
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.Reporter;
import io.gravitee.reporter.api.health.EndpointStatus;
import io.gravitee.reporter.api.http.Metrics;
import io.gravitee.reporter.api.log.Log;
import io.gravitee.reporter.kafka.config.KafkaConfiguration;
import io.gravitee.reporter.kafka.model.MessageType;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaReporter extends AbstractService implements Reporter {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaReporter.class);

    @Autowired(required = false)
    private KafkaProducer<String, JsonObject> kafkaProducer;

    @Autowired(required = false)
    private KafkaConfiguration kafkaConfiguration;

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (kafkaProducer != null) {
            kafkaProducer.close(res -> {
                if (res.succeeded()) {
                    LOGGER.info("Kafka producer closed");
                } else {
                    LOGGER.error("Fail to close Kafka producer");
                }
            });
        }
    }

    @Override
    public void report(Reportable reportable) {
        if (kafkaProducer != null) {
            KafkaProducerRecord<String, JsonObject> record = KafkaProducerRecord.create(
                kafkaConfiguration.getKafkaTopic(),
                JsonObject.mapFrom(reportable)
            );
            kafkaProducer.write(
                record,
                done -> {
                    String message;
                    if (done.succeeded()) {
                        RecordMetadata recordMetadata = done.result();
                        message =
                            String.format(
                                "Topic=%s partition=%s offset=%s message %s",
                                record.value(),
                                recordMetadata.getTopic(),
                                recordMetadata.getPartition(),
                                recordMetadata.getOffset()
                            );
                    } else {
                        message = String.format("Message %s not written on topic=%s", record.value(), kafkaConfiguration.getKafkaTopic());
                    }
                    LOGGER.info(message);
                }
            );
        }
    }

    @Override
    public boolean canHandle(Reportable reportable) {
        if (kafkaConfiguration != null) {
            MessageType messageType;
            if (kafkaConfiguration.getMessageTypes().isEmpty()) {
                return true;
            } else if (reportable instanceof Metrics) {
                messageType = MessageType.REQUEST;
            } else if (reportable instanceof EndpointStatus) {
                messageType = MessageType.HEALTH;
            } else if (reportable instanceof Monitor) {
                messageType = MessageType.MONITOR;
            } else if (reportable instanceof Log) {
                messageType = MessageType.LOG;
            } else {
                return false;
            }
            return kafkaConfiguration.getMessageTypes().contains(messageType);
        }
        return false;
    }
}
