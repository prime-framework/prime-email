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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.primeframework.email.EmailTestHelper;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.PreviewResult;
import org.primeframework.email.domain.RawEmailTemplates;
import org.primeframework.email.domain.SendResult;
import org.primeframework.email.domain.ValidateResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the FreeMarker email service.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class DefaultEmailServiceTest {
  public Configuration config;

  private Path templatePath = Paths.get("src/test/resources/templates");

  @BeforeClass
  public void beforeClass() {
    EmailTestHelper.setup();
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    DefaultObjectWrapper wrapper = new DefaultObjectWrapper(Configuration.VERSION_2_3_23);
    wrapper.setExposeFields(true);
    config = new Configuration(Configuration.VERSION_2_3_23);
    config.setObjectWrapper(wrapper);
    config.setTemplateLoader(new FileTemplateLoader(new File("src/test/resources")));

    EmailTestHelper.reset();
  }

  @Test
  public void preview_existing_badParse() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    PreviewResult result = service.preview("bad-parse-template", singletonList(Locale.US))
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .go();
    assertFalse(result.wasSuccessful());
    assertNotNull(result.parseErrors.get("text"));
    assertNotNull(result.parseErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void preview_existing_badRender() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    PreviewResult result = service.preview("bad-render-template", singletonList(Locale.US))
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .go();
    assertFalse(result.wasSuccessful());
    assertNotNull(result.renderErrors.get("text"));
    assertNotNull(result.renderErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void preview_partBad_partGood() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    RawEmailTemplates rawEmailTemplates = loadRaw(templatePath.resolve("bad-render-template-text.ftl"), templatePath.resolve("test-template-html.ftl"));
    PreviewResult result = service.preview(rawEmailTemplates)
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .withTemplateParameter("key1", "value1")
                                  .go();
    assertFalse(result.wasSuccessful());
    assertEquals(result.email.html, "HTML value1");
    assertNotNull(result.renderErrors.get("text"));
    assertNull(result.renderErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void preview_raw_badParse() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    RawEmailTemplates rawEmailTemplates = loadRaw(templatePath.resolve("bad-parse-template-text.ftl"), templatePath.resolve("bad-parse-template-html.ftl"));
    PreviewResult result = service.preview(rawEmailTemplates)
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .go();
    assertFalse(result.wasSuccessful());
    assertNotNull(result.parseErrors.get("text"));
    assertNotNull(result.parseErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void preview_raw_badRender() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    RawEmailTemplates rawEmailTemplates = loadRaw(templatePath.resolve("bad-render-template-text.ftl"), templatePath.resolve("bad-render-template-html.ftl"));
    PreviewResult result = service.preview(rawEmailTemplates)
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .go();
    assertFalse(result.wasSuccessful());
    assertNotNull(result.renderErrors.get("text"));
    assertNotNull(result.renderErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void render() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    PreviewResult result = service.preview("test-template", singletonList(Locale.US))
                                  .cc(new EmailAddress("from@example.com"))
                                  .bcc(new EmailAddress("from@example.com"))
                                  .withSubject("test subject")
                                  .from(new EmailAddress("from@example.com"))
                                  .to(new EmailAddress("to@example.com"))
                                  .withTemplateParameter("key1", "value1")
                                  .go();
    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
    assertTrue(result.wasSuccessful());
    assertEquals(result.email.subject, "test subject");
    assertEquals(result.email.from.address, "from@example.com");
    assertEquals(result.email.to.get(0).address, "to@example.com");
    assertEquals(result.email.text, "Text value1");
    assertEquals(result.email.html, "HTML value1");
  }

  @Test
  public void sendEmailClassPath() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    SendResult result = service.send("test-template", singletonList(Locale.US))
                               .cc(new EmailAddress("from@example.com"))
                               .bcc(new EmailAddress("from@example.com"))
                               .withSubject("test subject")
                               .from(new EmailAddress("from@example.com"))
                               .to(new EmailAddress("to@example.com"))
                               .withTemplateParameter("key1", "value1")
                               .now();
    assertTrue(result.wasSuccessful());

    Email email = EmailTestHelper.getEmailResults().poll();
    assertEquals(email.subject, "test subject");
    assertEquals(email.from.address, "from@example.com");
    assertEquals(email.to.get(0).address, "to@example.com");
    assertEquals(email.text, "Text value1");
    assertEquals(email.html, "HTML value1");
  }

  @Test
  public void sendTemplatedEmail() throws Exception {
    Bean bean = new Bean();
    bean.name = "frank";
    bean.bean2 = new Bean2();
    bean.bean2.hobby = "fishing";

    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    SendResult result = service.send("test-template-with-bean", singletonList(Locale.US))
                               .cc(new EmailAddress("from@example.com"))
                               .bcc(new EmailAddress("from@example.com"))
                               .withSubject("test subject")
                               .from(new EmailAddress("from@example.com"))
                               .to(new EmailAddress("to@example.com"))
                               .withTemplateParameter("bean", bean)
                               .now();
    assertTrue(result.wasSuccessful());

    Email email = EmailTestHelper.getEmailResults().poll();
    assertEquals(email.subject, "test subject");
    assertEquals(email.from.address, "from@example.com");
    assertEquals(email.to.get(0).address, "to@example.com");
    assertEquals(email.text, "Text frank likes fishing");
    assertEquals(email.html, "HTML frank likes fishing");
  }

  @Test
  public void send_later_badParse() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    SendResult result = service.send("bad-parse-template", singletonList(Locale.US))
                               .cc(new EmailAddress("from@example.com"))
                               .bcc(new EmailAddress("from@example.com"))
                               .withSubject("test subject")
                               .from(new EmailAddress("from@example.com"))
                               .to(new EmailAddress("to@example.com"))
                               .withTemplateParameter("key1", "value1")
                               .later();
    assertFalse(result.wasSuccessful());
    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void send_now_badParse() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    SendResult result = service.send("bad-parse-template", singletonList(Locale.US))
                               .cc(new EmailAddress("from@example.com"))
                               .bcc(new EmailAddress("from@example.com"))
                               .withSubject("test subject")
                               .from(new EmailAddress("from@example.com"))
                               .to(new EmailAddress("to@example.com"))
                               .withTemplateParameter("key1", "value1")
                               .now();
    assertFalse(result.wasSuccessful());
    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void validate_badParse() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    RawEmailTemplates rawEmailTemplates = loadRaw(templatePath.resolve("bad-parse-template-text.ftl"), templatePath.resolve("bad-parse-template-html.ftl"));
    ValidateResult result = service.validate(rawEmailTemplates, emptyMap());
    assertFalse(result.wasSuccessful());
    assertNotNull(result.parseErrors.get("text"));
    assertNotNull(result.parseErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  @Test
  public void validate_badRender() throws Exception {
    DefaultEmailService service = new DefaultEmailService(new FreeMarkerEmailRenderer(), new FileSystemEmailTemplateLoader(new TestEmailConfiguration(), config), EmailTestHelper.getService());
    RawEmailTemplates rawEmailTemplates = loadRaw(templatePath.resolve("bad-render-template-text.ftl"), templatePath.resolve("bad-render-template-html.ftl"));
    ValidateResult result = service.validate(rawEmailTemplates, emptyMap());
    assertFalse(result.wasSuccessful());
    assertNotNull(result.renderErrors.get("text"));
    assertNotNull(result.renderErrors.get("html"));

    assertTrue(EmailTestHelper.getEmailResults().isEmpty());
  }

  private RawEmailTemplates loadRaw(Path textPath, Path htmlPath) throws IOException {
    RawEmailTemplates rawEmailTemplates = new RawEmailTemplates();
    rawEmailTemplates.text = new String(Files.readAllBytes(textPath));
    rawEmailTemplates.html = new String(Files.readAllBytes(htmlPath));
    return rawEmailTemplates;
  }

  public static class TestEmailConfiguration implements EmailConfiguration {
    @Override
    public String templateLocation() {
      return "templates";
    }
  }
}