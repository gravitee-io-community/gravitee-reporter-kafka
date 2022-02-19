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

import io.gravitee.reporter.kafka.ContextTestConfiguration;
import io.gravitee.reporter.kafka.config.KafkaConfiguration;
import io.vertx.core.Vertx;
import java.io.File;
import java.io.FileNotFoundException;
import javax.inject.Inject;
import net.manub.embeddedkafka.EmbeddedKafka$;
import net.manub.embeddedkafka.EmbeddedKafkaConfigImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ContextTestConfiguration.class })
public class KafkaClientProducerFactoryIT {

    static EmbeddedKafkaConfigImpl conf = new EmbeddedKafkaConfigImpl(
        6001,
        6000,
        new scala.collection.immutable.HashMap<String, String>(),
        new scala.collection.immutable.HashMap<String, String>(),
        new scala.collection.immutable.HashMap<String, String>()
    );

    static EmbeddedKafka$ kafkaUnitServer = EmbeddedKafka$.MODULE$;

    @Inject
    private KafkaConfiguration kafkaConfiguration;

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException {
        File graviteeConf = ResourceUtils.getFile("classpath:gravitee-embedded.yml");
        System.setProperty("gravitee.conf", graviteeConf.getAbsolutePath());

        kafkaUnitServer.start(conf);
    }

    @AfterClass
    public static void after() {
        kafkaUnitServer.stop();
    }

    @Test
    public void shouldCreateInstance() throws Exception {
        KafkaClientProducerFactory factory = new KafkaClientProducerFactory(kafkaConfiguration, Vertx.vertx());
        factory.createInstance();
    }

    @Test(expected = Throwable.class)
    public void shouldRaiseExceptionOnProducerDefault() throws Exception {
        KafkaClientProducerFactory factory = new KafkaClientProducerFactory(null, Vertx.vertx());
        factory.createInstance();
    }
}
