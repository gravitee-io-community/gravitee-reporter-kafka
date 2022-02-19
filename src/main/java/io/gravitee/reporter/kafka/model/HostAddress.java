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

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.Assert;

public class HostAddress {

    public String hostname;

    public int port;

    public HostAddress(String hostname, int port) {
        Assert.notNull(hostname);
        Assert.notNull(port);

        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return String.join(":", hostname, Integer.toString(port));
    }

    public static String stringifyHostAddresses(List<HostAddress> hostAddressList) {
        Assert.notNull(hostAddressList, "Host Address argument must not be Null");
        return hostAddressList.stream().map(s -> s.toString()).collect(Collectors.joining(","));
    }
}
