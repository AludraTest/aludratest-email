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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testing.service.AbstractAludraServiceTest;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * Parent class for {@link SmtpService} tests.
 * @author Volker Bergmann
 */

public class AbstractSmtpTest extends AbstractAludraServiceTest {
	
	protected static final String SENDER = "tester@test.zzz";
	protected static final String RECIPIENT_1 = "test@test.xyz";
	protected static final String SUBJECT = "The subject";
	protected static final String TEXT = "The message text";
	protected static final String XML_FILE = "org/aludratest/service/smtp/attachment.xml";
	protected static final String XML_MIME_TYPE = "text/xml";
	
	protected Wiser wiser;

	protected SmtpService service;

    @Before
    public void startSmtpServiceAndServer() {
        this.service = getLoggingSmtpService();
        int port = 2525;
    	startSmtpServer(port);
    }

	@After
    public void stopSmtpServiceAndServer() {
    	IOUtil.close(service);
    	this.wiser.stop();
    }

	protected void startSmtpServer(int port) {
		this.wiser = new Wiser();
		wiser.setPort(port);
		wiser.start();
	}
    
    protected SmtpService getLoggingSmtpService() {
        return newLoggingService(SmtpService.class, "smtpTest");
    }

    @SuppressWarnings("unchecked")
    protected <T extends AludraService, U extends T> U newLoggingService(Class<T> interfaceClass, String moduleName) {
        return (U) framework.getServiceManager().createAndConfigureService(ComponentId.create(interfaceClass, moduleName), context, true);
    }
    
	protected List<WiserMessage> getMessages() {
		return wiser.getMessages();
	}

	protected void verifyPlainTextEmail() throws Exception {
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		List<WiserMessage> messages = getMessages();
		assertEquals(1, messages.size());
		MimeMessage message = messages.get(0).getMimeMessage();
		assertEquals(new InternetAddress(SENDER), message.getFrom()[0]);
		assertEquals(1, message.getAllRecipients().length);
		assertEquals(new InternetAddress(RECIPIENT_1), message.getAllRecipients()[0]);
		assertEquals(SUBJECT, message.getSubject());
		assertEquals(TEXT, String.valueOf(message.getContent()).trim());
	}

	protected void verifyEmailWithAttachments() throws MessagingException, AddressException, IOException {
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		List<WiserMessage> messages = getMessages();
		assertEquals(1, messages.size());
		MimeMessage message = messages.get(0).getMimeMessage();
		assertEquals(new InternetAddress(SENDER), message.getFrom()[0]);
		assertEquals(1, message.getAllRecipients().length);
		assertEquals(new InternetAddress(RECIPIENT_1), message.getAllRecipients()[0]);
		assertEquals(SUBJECT, message.getSubject());
		MimeMultipart content = (MimeMultipart) message.getContent();
		assertEquals(2, content.getCount());
		assertEquals(TEXT, ((MimeBodyPart) content.getBodyPart(0)).getContent());
		String xml = StringUtil.normalizeSpace(IOUtil.getContentOfURI(XML_FILE));
		String x = StringUtil.normalizeSpace((String) ((MimeBodyPart) content.getBodyPart(1)).getContent());
		assertEquals(xml, x);
	}

}
