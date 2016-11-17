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
import org.databene.commons.BeanUtil;
import org.databene.commons.CollectionUtil;

/**
 * AWL for sending an email.
 * @author Volker Bergmann
 */

public class EmailSender {
	
	private final SmtpService service;
	
	public EmailSender(SmtpService service) {
		this.service = service;
	}

	public EmailSender send(EmailData email) {
		AttachmentData[] attachments = CollectionUtil.toArray(email.getAttachments(), AttachmentData.class);
		String[] uris = BeanUtil.extractProperties(attachments, "uri", String.class);
		String[] mimeTypes = BeanUtil.extractProperties(attachments, "mimeType", String.class);
		this.service.perform().sendMail(email.getSender(), CollectionUtil.toArray(email.getRecipients()), 
				email.getSubject(), email.getText(), uris, mimeTypes);
		return this;
	}
	
}
