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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.Test;

public class MessageTypeTest {

    @Test
    public void shouldGetMessageType() {
        assertThat(MessageType.getByName("log").getType()).isEqualTo("log");
        assertThat(MessageType.getByName("monitor").getType()).isEqualTo("monitor");
        assertThat(MessageType.getByName("health").getType()).isEqualTo("health");
        assertThat(MessageType.getByName("request").getType()).isEqualTo("request");
        assertThat(MessageType.getByName("REquest").getType()).isEqualTo("request");
    }

    @Test
    public void shouldRaiseExceptionWhenGetIllegalTypeName() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> MessageType.getByName("foo"))
            .withMessage(String.format("Unsupported reportable type [%s]", "foo"));
    }
}
