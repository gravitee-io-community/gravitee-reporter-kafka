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

import io.gravitee.reporter.api.health.HealthStatus;
import io.gravitee.reporter.api.http.RequestMetrics;
import io.gravitee.reporter.api.monitor.Monitor;
import io.gravitee.reporter.kafka.config.KafkaConfiguration;
import io.gravitee.reporter.kafka.engine.ReportEngine;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AbstractKafkaReportEngine implements ReportEngine {
    @Autowired
    private KafkaConfiguration configuration;

    int BUFFER_SIZE = 100;

    String getServers() {
        return configuration.getServers();
    }

    String getRequestsTopic() {
        return configuration.getRequestsTopic();
    }

    String getStatusTopic() {
        return configuration.getStatusTopic();
    }

    String getMonitorTopic() {
        return configuration.getMonitorTopic();
    }

    String serialize(RequestMetrics metrics) {
        return new JSONObject()
                .put("id", metrics.getRequestId())
                .put("transaction", metrics.getTransactionId())
                .put("uri", metrics.getRequestUri())
                .put("path", metrics.getRequestPath())
                .put("status", metrics.getResponseHttpStatus())
                .put("method", metrics.getRequestHttpMethod().toString())
                .put("request-content-type", metrics.getRequestContentType())
                .put("response-time", metrics.getProxyResponseTimeMs())
                .put("api-response-time", metrics.getApiResponseTimeMs() >= 0 ? metrics.getApiResponseTimeMs() : null)
                .put("proxy-latency", metrics.getProxyLatencyMs() >= 0 ? metrics.getProxyLatencyMs() : null)
                .put("response-content-type", metrics.getResponseContentType())
                .put("request-content-length", metrics.getRequestContentLength() >= 0 ? metrics.getRequestContentLength() : null)
                .put("response-content-length", metrics.getResponseContentLength() >= 0 ? metrics.getResponseContentLength() : null)
                .put("api-key", metrics.getApiKey())
                .put("user", metrics.getUserId())
                .put("plan", metrics.getPlan())
                .put("api", metrics.getApi())
                .put("application", metrics.getApplication())
                .put("local-address", metrics.getRequestLocalAddress())
                .put("remote-address", metrics.getRequestRemoteAddress())
                .put("endpoint", metrics.getEndpoint())
                .put("tenant", metrics.getTenant())
                .toString();
    }

    String serialize(HealthStatus healthStatus) {
        return new JSONObject()
                .put("api", healthStatus.getApi())
                .put("status", healthStatus.getStatus())
                .put("url", healthStatus.getUrl())
                .put("method", healthStatus.getMethod())
                .put("success", healthStatus.isSuccess())
                .put("state", healthStatus.getState())
                .put("message", healthStatus.getMessage())
                .toString();
    }

    String serialize(Monitor monitor) {
        return new JSONObject()
                //TODO Add fields.
                .toString();
    }
}