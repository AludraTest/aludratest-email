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

import org.junit.Test;

/**
 * Tests the {@link SmtpService} generically with the configured implementation.
 * @author Volker Bergmann
 */

public class SmtpServiceIntegrationTest extends AbstractSmtpTest {

	@Test
	public void testPlainTextEmail() throws Exception {
		service.perform().sendMail(SENDER, new String[] { RECIPIENT_1 }, SUBJECT, TEXT, new String[0], new String[0]);
		verifyPlainTextEmail();
	}

	@Test
	public void testMessageWithAttachment() throws Exception {
		service.perform().sendMail(SENDER, new String[] { RECIPIENT_1 }, SUBJECT, TEXT, new String[] { XML_FILE }, new String[] { XML_MIME_TYPE });
		verifyEmailWithAttachments();
	}

}
