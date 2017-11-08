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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

/**
 * Kafka producer reporter configuration.
 *
 * @author Florian MERIAUX
 * @author Leansys Team
 */
public class KafkaConfiguration {

	@Autowired
	private Environment environment;


	@Value("${reporters.kafka.topics.requests")
	private String requestsTopic;

	@Value("${reporters.kafka.topics.status")
	private String statusTopic;

    @Value("${reporters.kafka.topics.monitor")
    private String monitorTopic;

    @Value("${reporters.kafka.topics.servers")
    private String servers;

    public String getRequestsTopic() {
        return requestsTopic;
    }

    public String getStatusTopic() {
        return statusTopic;
    }

    public String getMonitorTopic() {
        return monitorTopic;
    }

    public String getServers() {
        return servers;
    }
}
