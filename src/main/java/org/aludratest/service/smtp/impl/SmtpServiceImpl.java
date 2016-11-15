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

package org.aludratest.service.smtp.impl;

import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.smtp.SmtpCondition;
import org.aludratest.service.smtp.SmtpInteraction;
import org.aludratest.service.smtp.SmtpService;
import org.aludratest.service.smtp.SmtpVerification;

/**
 * Default implementation of the {@link SmtpService} using the JavaMail API.
 * @author Volker Bergmann
 */

public class SmtpServiceImpl extends AbstractConfigurableAludraService implements SmtpService {

	private String host;
	private int port;
	private String user;
	private String password;
	private boolean auth;
	private boolean startTlsEnable;
	private boolean tls;
	private boolean sslCheckServeridentity;
	
	private SmtpAction action;

	@Override
	public String getDescription() {
		return "SMTP @ " + host + ":" + port;
	}

	@Override
	public String getPropertiesBaseName() {
		return "smtp";
	}

	@Override
	public void configure(Preferences preferences) {
		ValidatingPreferencesWrapper prefs = new ValidatingPreferencesWrapper(preferences);
		this.host = prefs.getRequiredStringValue("host");
		this.port = prefs.getRequiredIntValue("port");
		this.user = prefs.getStringValue("user");
		this.password = prefs.getRequiredStringValue("password");
		this.auth = prefs.getBooleanValue("auth", true);
		this.startTlsEnable = prefs.getBooleanValue("startTlsEnable", true);
		this.tls = prefs.getBooleanValue("tls", true);
		this.sslCheckServeridentity = prefs.getBooleanValue("sslCheckServeridentity", true);
	}
	
	@Override
	public void initService() {
		this.action = new SmtpAction(host, port, user, password, auth, startTlsEnable, tls, sslCheckServeridentity);
	}
	
	@Override
	public SmtpInteraction perform() {
		return action;
	}
	
	@Override
	public SmtpCondition check() {
		return action;
	}
	
	@Override
	public SmtpVerification verify() {
		return action;
	}
	
	@Override
	public void close() {
		// nothing to do
	}
	
}
