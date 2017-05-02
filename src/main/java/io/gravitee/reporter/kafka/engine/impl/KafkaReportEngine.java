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

package io.gravitee.reporter.kafka.engine.impl;

import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.*;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.SourceQueueWithComplete;
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.health.HealthStatus;
import io.gravitee.reporter.api.http.RequestMetrics;
import io.gravitee.reporter.api.monitor.Monitor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {{{
 *  _____                        www.leansys.fr
 * |     |_.-----.---.-.-----.-----.--.--.-----.
 * |       |  -__|  _  |     |__ --|  |  |__ --|
 * |_______|_____|___._|__|__|_____|___  |_____|
 *    florian.meriaux@leansys.fr   |_____|
 * }}}
 */

public final class KafkaReportEngine extends AbstractKafkaReportEngine {
	private final Logger LOGGER = LoggerFactory.getLogger(KafkaReportEngine.class);

	// AKKA Modules
	private final ActorSystem system = ActorSystem.create("KafkaReportEngine");
	private final Materializer materializer = ActorMaterializer.create(system);
    private final SharedKillSwitch killSwitch = KillSwitches.shared("reporter-kill-switch");

	private final ProducerSettings<String, String> producerSettings = ProducerSettings
			.create(system, new StringSerializer(), new StringSerializer())
			.withBootstrapServers(getServers());

    private final KafkaProducer<String, String> kafkaProducer = producerSettings.createKafkaProducer();

	private Source<Reportable, SourceQueueWithComplete<Reportable>> requests = Source.queue(BUFFER_SIZE, OverflowStrategy.backpressure());
	private Source<Reportable, SourceQueueWithComplete<Reportable>> status = Source.queue(BUFFER_SIZE, OverflowStrategy.backpressure());
	private Source<Reportable, SourceQueueWithComplete<Reportable>> monitor = Source.queue(BUFFER_SIZE, OverflowStrategy.backpressure());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void report(Reportable reportable) {
	    // Determine the sending topic based on the instance type.
	    //TODO Add an error handler when delivered to the source.
        if (reportable instanceof RequestMetrics) {
            requests.mapMaterializedValue((Function<SourceQueueWithComplete<Reportable>, Object>) param -> param.offer(reportable));
        } else if (reportable instanceof HealthStatus) {
            status.mapMaterializedValue((Function<SourceQueueWithComplete<Reportable>, Object>) param -> param.offer(reportable));
        } else if (reportable instanceof Monitor) {
            monitor.mapMaterializedValue((Function<SourceQueueWithComplete<Reportable>, Object>) param -> param.offer(reportable));
        } else {
            LOGGER.error("Reportable {} unknown, ignoring...", reportable);
        }
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		LOGGER.info("Starting Kafka reporter engine...");

		// Configure streams for each source with a kill switch for a gracefully shutdown.
        // TODO Simplify by working with graphs, so as to merge the 3 flows before the kill switch.
		requests.map(reportable -> serialize((RequestMetrics) reportable)).map(elem -> new ProducerRecord<String, String>(getRequestsTopic(), elem))
                .viaMat(killSwitch.flow(), Keep.right())
                .runWith(Producer.plainSink(producerSettings, kafkaProducer), materializer);

        status.map(reportable -> serialize((HealthStatus) reportable)).map(elem -> new ProducerRecord<String, String>(getStatusTopic(), elem))
                .viaMat(killSwitch.flow(), Keep.right())
                .runWith(Producer.plainSink(producerSettings, kafkaProducer), materializer);

        monitor.map(reportable -> serialize((Monitor) reportable)).map(elem -> new ProducerRecord<String, String>(getMonitorTopic(), elem))
                .viaMat(killSwitch.flow(), Keep.right())
                .runWith(Producer.plainSink(producerSettings, kafkaProducer), materializer);

		LOGGER.info("Starting Kafka reporter engine... DONE");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		LOGGER.info("Stopping Kafka reporter engine...");

		// Gracefully shutdown.
        killSwitch.shutdown();
		system.shutdown();

		LOGGER.info("Stopping Kafka reporter engine... DONE");
	}
}
