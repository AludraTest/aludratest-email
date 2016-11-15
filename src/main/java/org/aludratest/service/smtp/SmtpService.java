/*
 * Copyright (C) 2016 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aludratest.service.smtp;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;

/**
 * Service interface for accessing a SMTP server.
 * @author Volker Bergmann
 */

@ConfigProperties({
	@ConfigProperty(name = "host", description = "The host name or IP address of the SMTP server.", 
			type = String.class, required = true),
	@ConfigProperty(name = "port", description = "The port on which the SMTP server listens for messages.", 
			type = int.class, required = true),
	@ConfigProperty(name = "user", description = "The user name to use when connecting to a mail server.", 
			type = int.class, required = false),
	@ConfigProperty(name = "password", description = "The password name to use when connecting to a mail server.", 
			type = int.class, required = false),
	@ConfigProperty(name = "auth", description = "If true, attempt to authenticate the user using the " +
			"AUTH command. Defaults to false.", 
			type = boolean.class, required = false, defaultValue = "false"),
	@ConfigProperty(name = "starttlsEnable", description = "enables the use of the STARTTLS command (if " +
			"supported by the server) to switch the connection to a TLS-protected connection before issuing " +
			"any login commands. Defaults to false.", 
			type = boolean.class, required = false, defaultValue = "false"),
	@ConfigProperty(name = "sslCheckserveridentity", description = "If set to true, checks the server identity " +
			"as specified by RFC 2595. Defaults to false.", 
			type = boolean.class, required = false, defaultValue = "false") })
public interface SmtpService extends AludraService {

	@Override
	SmtpInteraction perform();

	@Override
	SmtpCondition check();

	@Override
	SmtpVerification verify();

}
