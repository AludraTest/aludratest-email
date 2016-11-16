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

import org.aludratest.service.AttachParameter;
import org.aludratest.service.Interaction;

/**
 * Implementation of the {@link Interaction} interface for the SmtpService.
 * @author Volker Bergmann
 */
public interface SmtpInteraction extends Interaction {
	
	/** Sends a mail using a SMTP Server.
	 *  @param sender the email address of the sender
	 *  @param recipients an array of string with the recipients' email addresses
	 *  @param subject the email subject
	 *  @param text the email text
	 *  @param filesToAttach an array of file URIs (Databene-style) to attach to the email 
	 *  @param mimeTypes the mime types to use for the files listed in the <code>filesToAttach</code> parameter 
	 */
	public void sendMail(
			String sender, 
			String[] recipients, 
			String subject, 
			@AttachParameter("Message text") String text,
			String[] filesToAttach, 
			String[] mimeTypes);

}
