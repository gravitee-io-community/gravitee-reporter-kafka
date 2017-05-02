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
import io.gravitee.reporter.api.Reportable;
import io.gravitee.reporter.api.Reporter;
import io.gravitee.reporter.kafka.engine.ReportEngine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Florian Meriaux
 * @author Leansys Team
 */
public class KafkaReporter extends AbstractService implements Reporter {
	  
	@Autowired
	private ReportEngine reportEngine;
    
	@Override
	public void report(Reportable reportable) {
		reportEngine.report(reportable);
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();

		reportEngine.start();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();

		reportEngine.stop();
	}
}