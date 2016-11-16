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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.AttachParameter;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.smtp.SmtpCondition;
import org.aludratest.service.smtp.SmtpInteraction;
import org.aludratest.service.smtp.SmtpVerification;
import org.aludratest.service.smtp.data.AttachmentData;
import org.aludratest.service.smtp.data.EmailData;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.testcase.event.attachment.StringAttachment;
import org.databene.commons.Assert;
import org.databene.commons.IOUtil;

/**
 * Implements {@link SmtpInteraction}, {@link SmtpVerification} and {@link SmtpCondition}.
 * @author Volker Bergmann
 */

public class SmtpAction implements SmtpInteraction, SmtpVerification, SmtpCondition {

	private Session session;
	
	private EmailData recentEmail;

	public SmtpAction(String host, int port, final String user, final String password, 
			boolean auth, boolean startTlsEnable, boolean tls, boolean sslCheckServeridentity) {
		
		final Properties props = new Properties();
		props.put("mail.transport.protocol","smtp");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.user", user);
		props.put("mail.smtp.auth", String.valueOf(auth));
		props.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnable));
		props.put("mail.smtp.tls", String.valueOf(tls));
		props.put("mail.smtp.ssl.checkserveridentity", String.valueOf(sslCheckServeridentity));

		final javax.mail.Authenticator authenticator = new javax.mail.Authenticator() {
		   @Override
		   public PasswordAuthentication getPasswordAuthentication() {
		      return new PasswordAuthentication(user, password);
		   }
		};
		
		this.session = Session.getInstance(props, authenticator);
	}
	
	
	// Action interface implementation -----------------------------------------

	@Override
	public List<Attachment> createDebugAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (this.recentEmail != null) {
            attachments.add(new StringAttachment("email", this.recentEmail.toString(), "txt"));
        }
        return attachments;
	}

	@Override
	public List<Attachment> createAttachments(Object object, String title) {
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		list.add(new StringAttachment(title, String.valueOf(object), "txt"));
		return list;
	}

	@Override
	public void setSystemConnector(SystemConnector systemConnector) {
		// not supported
	}
	
	
	// SmtpInteraction interface implementation --------------------------------
	
	@Override
	public void sendMail(String sender, String[] recipients, String subject, @AttachParameter("Message text") String text,
			String[] attachmentUris, String[] mimeTypes) {
		// check preconditions
		Assert.notEmpty(text, "No sender specified");
		Assert.notEmpty(recipients, "No recipient specified");
		for (String recipient : recipients) {
			Assert.notEmpty(recipient, "Empty recipient information");
		}
		int uriCount = (attachmentUris != null ? attachmentUris.length : 0);
		int mimeTypeCount = (mimeTypes != null ? mimeTypes.length : 0);
		Assert.equals(uriCount, mimeTypeCount, "The numbers of attachment URIs and mime types do not match");
		
		// send the email
		if (uriCount > 0) {
			sendMailWithAttachments(sender, recipients, subject, text, attachmentUris, mimeTypes);
		} else {
			sendPlainTextMail(sender, recipients, subject, text);
		}
	}


	// private helpers ---------------------------------------------------------
	
	private void sendPlainTextMail(String sender, String[] recipients, String subject, String text) {
		try {
			Message message = createMessage(sender, recipients, subject);
			message.setText(text);
			this.recentEmail = new EmailData(sender, recipients, subject, text);
			saveChangesAndSendMessage(message);
		} catch (MessagingException e) {
			throw new TechnicalException("Sending email failed", e);
		}
	}

	private void sendMailWithAttachments(String sender, String[] recipients, String subject, String text,
			String[] attachmentUris, String[] mimeTypes) {
		try {
			// create the message
			Message message = createMessage(sender, recipients, subject);
			this.recentEmail = new EmailData(sender, recipients, subject, text);
			
			// Create text body part
			Multipart multipart = new MimeMultipart();
			addTextBodyPart(text, multipart);
			
			// create attachment parts
			int uriCount = (attachmentUris != null ? attachmentUris.length : 0);
			if (uriCount > 0) {
				for (int i = 0; i < uriCount; i++) {
					addAttachmentBodyPart(attachmentUris[i], mimeTypes[i], multipart);
					message.setContent(multipart);
					this.recentEmail.addAttachment(new AttachmentData(attachmentUris[i], mimeTypes[i]));
				}
			}
			
			// finally save changes and send the email
			saveChangesAndSendMessage(message);
			
		} catch (MessagingException e) {
			throw new TechnicalException("Sending email failed", e);
		} catch (IOException e) {
			throw new AutomationException("Reading attachment file failed", e);
		}
	}

	private Message createMessage(String sender, String[] recipients, String subject) throws MessagingException {
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(sender));
		} catch (AddressException e) {
			throw new AutomationException("Failed to set email sender address", e);
		}
		try {
			for (String recipient : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}
		} catch (AddressException e) {
			throw new AutomationException("Failed to set email recipient address", e);
		}
		message.setSubject(subject);
		return message;
	}

	private static byte[] addAttachmentBodyPart(String attachmentUri, String mimeType, Multipart multipart)
			throws IOException, MessagingException {
		BodyPart attachmentBodyPart = new MimeBodyPart();
		byte[] bytes = IOUtil.getBinaryContentOfUri(attachmentUri);
		DataSource source = new ByteArrayDataSource(bytes, mimeType);
		attachmentBodyPart.setDataHandler(new DataHandler(source));
		attachmentBodyPart.setFileName(attachmentUri);
		multipart.addBodyPart(attachmentBodyPart);
		return bytes;
	}

	private static void addTextBodyPart(String text, Multipart multipart) throws MessagingException {
		BodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setText(text);
		multipart.addBodyPart(textBodyPart);
	}
	
	private static void saveChangesAndSendMessage(Message message) throws MessagingException {
		message.saveChanges();
		Transport.send(message);
	}
	
}
