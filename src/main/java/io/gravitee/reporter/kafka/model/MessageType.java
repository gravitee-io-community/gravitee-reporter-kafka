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
package io.gravitee.reporter.kafka.model;

import java.util.Arrays;

public enum MessageType {

    LOG("log"), MONITOR("monitor"), HEALTH("health"), REQUEST("request");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public static MessageType getByName(final String value){
        return Arrays.stream(values())
                .filter(m -> m.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported reportable type [%s]", value)));

    }

    public String getType() {
        return type;
    }
}
