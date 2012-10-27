/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.primeframework.email.service;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.primeframework.email.EmailException;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * his class implements the {@link EmailService} interface and generates emails using FreeMarker. This service is thread
 * safe from the stand point that it doesn't store anything that is not thread safe.
 * <p/>
 * This class takes a configuration instance that can be used to configure the handling of the FreeMarker templates that
 * will be used to generate the emails. Be default this class looks in the web applications <b>/WEB-INF/emails</b>
 * directory for the templates, but this can be configured. The template name passed in is assumed to be the same as the
 * name of the FreeMarker file without the <b>ftl</b> file extension. For example, if you have a FreeMarker template
 * named <b>foo.ftl</b>, you would pass into this class the string <b>foo</b>. This makes it easier to assign custom
 * configuration to a specific template. The configuration for each template is documented later.
 * <p/>
 * After the templates are found, this class constructs an {@link Email} instance by using any information passed into
 * the class as well as any information from the configuration. This makes it easy to set the default <b>from</b> field
 * and other attributes of the email. Here are the configuration paramters that can be set. Note that the &lt;template>
 * is replaced by the template name passed to this class. Also note that this class first uses values passed in and then
 * the values from the configuration.
 * <p/>
 * <table> <tr><th>Name</th><th>Description</th><th>Optional</th><th>Default if optional</th></tr>
 * <tr><td>jcatapult.email.&lt;template>.from</td><td>This specifies from address for the email.</td>
 * <td>Yes</td><td>The value passed in</td></tr> <tr><td>jcatapult.email.&lt;template>.to</td><td>This specifies to
 * address for the email.</td> <td>Yes</td><td>The value passed in</td></tr> <tr><td>jcatapult.email.&lt;template>.subject</td><td>This
 * specifies subject for the email.</td> <td>Yes</td><td>The value passed in</td></tr> </table>
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailService implements EmailService {
  private final static Logger logger = LoggerFactory.getLogger(FreeMarkerEmailService.class);
  private final EmailTransportService emailTransportService;
  private final freemarker.template.Configuration freeMarkerConfiguration;
  private final Locale locale;
  private final String templatesLocation;

  /**
   * Constructs a FreeMarkerEmailService. The transport given is used to send the emails and the configuration given is
   * used to control FreeMarker behavior.
   *
   * @param emailTransportService Used to send emails.
   * @param configuration         Used to control FreeMarker.
   * @param emailConfiguration    The email configuration.
   * @param locale                The locale.
   */
  @Inject
  public FreeMarkerEmailService(EmailTransportService emailTransportService, Configuration configuration,
                                EmailConfiguration emailConfiguration, Locale locale) {
    this.emailTransportService = emailTransportService;
    this.freeMarkerConfiguration = configuration;
    this.templatesLocation = emailConfiguration.templateLocation();
    this.locale = locale;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand sendEmail(String template) {
    return new FreeMarkerEmailCommand(template, this, new Email());
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param template The template to execute.
   * @param email    The email to add the text to and send.
   * @param params   The params that are sent to the template.
   * @return The future from the transport.
   */
  Future<Email> sendEmail(String template, Email email, Map<String, Object> params) {
    renderEmail(template, email, params);
    return emailTransportService.sendEmail(email);
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param template The template to execute.
   * @param email    The email to add the text to and send.
   * @param params   The params that are sent to the template.
   */
  void sendEmailLater(String template, Email email, Map<String, Object> params) {
    renderEmail(template, email, params);
    emailTransportService.sendEmailLater(email);
  }

  /**
   * Renders the FTL templates to construct the body of the email.
   *
   * @param template The template to invoke.
   * @param email    The email to populate.
   * @param params   Parameters passed to the template.
   */
  private void renderEmail(String template, Email email, Map<String, Object> params) {
    // Get the text version if there is a template
    String text = callTemplate(template + "-text.ftl", params);
    if (text != null) {
      email.text = text;
    }

    // Handle the HTML version if there is one
    String html = callTemplate(template + "-html.ftl", params);
    if (html != null) {
      email.html = html;
    }

    if (text == null && html == null) {
      throw new EmailException("Missing email template for [" + template +
        "]. Either add a file named [" + template + "-text.ftl] or a file named [" +
        template + "-html.ftl] to the [" + templatesLocation + "] directory.");
    }
  }

  /**
   * Processes the FreeMarker template.
   *
   * @param templateName The name of the template file to process.
   * @param parameters   The parameters that are passed to the template.
   * @return The String result of the processing the template.
   * @throws EmailException If the template couldn't be processed.
   */
  protected String callTemplate(String templateName, Map<String, Object> parameters) {
    if (logger.isDebugEnabled()) {
      logger.debug("Invoking FreeMarker email template [" + templateName + "] with parameters " + parameters);
    }

    StringWriter writer = new StringWriter();
    try {
      Template template = freeMarkerConfiguration.getTemplate(templatesLocation + "/" + templateName, locale);
      template.process(parameters, writer);
    } catch (FileNotFoundException fnfe) {
      return null;
    } catch (Exception e) {
      throw new EmailException(e);
    }

    return writer.toString();
  }
}