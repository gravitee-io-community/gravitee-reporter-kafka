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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class HostAddressTest {

    @Test
    public void shouldRaiseExceptionForNullHostList() {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            HostAddress.stringifyHostAddresses(null);
        }).withMessage("Host Address argument must not be Null");
    }

    @Test
    public void shouldStringifySingleHostAddress() {
        List<HostAddress> hostAddressList = new ArrayList<HostAddress>();
        hostAddressList.add(new HostAddress("node1", 6062));
        String str = HostAddress.stringifyHostAddresses(hostAddressList);
        assertThat("node1:6062").isEqualTo(str);
    }

    @Test
    public void shouldStringifyHostAddressList() {
        List<HostAddress> hostAddressList = new ArrayList<HostAddress>();
        hostAddressList.add(new HostAddress("node1", 6062));
        hostAddressList.add(new HostAddress("node2", 6063));
        String str = HostAddress.stringifyHostAddresses(hostAddressList);
        assertThat("node1:6062,node2:6063").isEqualTo(str);
    }

}
