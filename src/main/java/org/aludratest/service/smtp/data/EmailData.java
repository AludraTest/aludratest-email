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

package org.aludratest.service.smtp.data;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.dict.Data;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;

/**
 * Data class that represents an email.
 * @author Volker Bergmann
 */

public class EmailData extends Data {
	
	private static final String LF = SystemInfo.getLineSeparator();

	private String sender;
	private List<String> recipients;
	private String subject;
	private String text;
	private List<AttachmentData> attachments;
	
	
	// constructors ------------------------------------------------------------
	
	public EmailData() {
		this(null, null, null, null);
	}
	
	public EmailData(String sender, String[] recipients, String subject, String text) {
		this.sender = sender;
		this.text = StringUtil.nullToEmpty(text);
		this.subject = StringUtil.nullToEmpty(subject);
		this.recipients = (recipients != null ? CollectionUtil.toList(recipients) : new ArrayList<String>());
		this.attachments = new ArrayList<AttachmentData>();
	}
	
	
	// properties --------------------------------------------------------------
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public void addRecipient(String recipient) {
		this.recipients.add(recipient);
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<AttachmentData> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<AttachmentData> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(AttachmentData attachment) {
		this.attachments.add(attachment);
	}
	
	
	// java.lang.Object overrides ----------------------------------------------
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("From:" ).append(sender).append(LF);
		if (recipients.size() > 0) {
			builder.append("To:").append(recipients.get(0));
			for (int i = 1; i < recipients.size(); i++) {
				builder.append(", ").append(recipients.get(i));
			}
		}
		builder.append("Subject:" ).append(subject).append(LF).append(LF);
		builder.append(text);
		// TODO list attachments
		return builder.toString();
	}

}
