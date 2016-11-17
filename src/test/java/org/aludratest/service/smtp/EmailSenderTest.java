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

import org.aludratest.service.smtp.data.AttachmentData;
import org.aludratest.service.smtp.data.EmailData;
import org.junit.Test;

/**
 * Tests the {@link EmailSender} AWL.
 * @author Volker Bergmann
 */

public class EmailSenderTest extends AbstractSmtpTest {
	
	@Test
	public void testPlainTextMessage() throws Exception {
		// compose mail
		EmailData email = createBasicEmail();
		// send mail
		new EmailSender(service).send(email);
		// verify delivery
		verifyPlainTextEmail();
	}

	@Test
	public void testMessageWithAttachment() throws Exception {
		// compose mail
		EmailData email = createBasicEmail();
		AttachmentData attachment = new AttachmentData();
		attachment.setUri(XML_FILE);
		attachment.setMimeType(XML_MIME_TYPE);
		email.addAttachment(attachment);
		// send mail
		new EmailSender(service).send(email);
		// verify delivery
		verifyEmailWithAttachments();
	}

	private static EmailData createBasicEmail() {
		EmailData email = new EmailData();
		email.setSender(SENDER);
		email.addRecipient(RECIPIENT_1);
		email.setSubject(SUBJECT);
		email.setText(TEXT);
		return email;
	}

}
