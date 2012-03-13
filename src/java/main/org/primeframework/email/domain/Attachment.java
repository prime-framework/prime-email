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
package org.primeframework.email.domain;

/**
 * This class is a simple attachment with a byte array, name and MIME type.
 *
 * @author  Brian Pontarelli
 */
public class Attachment {
    public String name;
    public String mime;
    public byte[] attachment;

    public Attachment() {
    }

    public Attachment(String name, String mime, byte[] attachment) {
        this.name = name;
        this.mime = mime;
        this.attachment = attachment;
    }
}