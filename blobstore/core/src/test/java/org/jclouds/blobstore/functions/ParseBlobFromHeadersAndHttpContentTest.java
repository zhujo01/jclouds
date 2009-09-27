/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContentTest {

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = NullPointerException.class)
   public void testCall() throws HttpException {
      ParseBlobMetadataFromHeaders<BlobMetadata> metadataParser = createMock(ParseBlobMetadataFromHeaders.class);
      ParseBlobFromHeadersAndHttpContent.BlobFactory<BlobMetadata, Blob<BlobMetadata>> objectFactory = createMock(ParseBlobFromHeadersAndHttpContent.BlobFactory.class);
      ParseBlobFromHeadersAndHttpContent<BlobMetadata, Blob<BlobMetadata>> callable = new ParseBlobFromHeadersAndHttpContent(
               metadataParser, objectFactory);
      HttpResponse response = createMock(HttpResponse.class);
      expect(response.getFirstHeaderOrNull("Content-Length")).andReturn("100");
      expect(response.getFirstHeaderOrNull("Content-Range")).andReturn(null);
      expect(response.getHeaders()).andReturn(ImmutableMultimap.of("Content-Length", "100"));
      expect(response.getContent()).andReturn(null);
      replay(response);
      callable.apply(response);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testParseContentLengthWhenContentRangeSet() throws HttpException {
      ParseBlobMetadataFromHeaders<BlobMetadata> metadataParser = createMock(ParseBlobMetadataFromHeaders.class);
      ParseBlobFromHeadersAndHttpContent.BlobFactory<BlobMetadata, Blob<BlobMetadata>> objectFactory = new ParseBlobFromHeadersAndHttpContent.BlobFactory<BlobMetadata, Blob<BlobMetadata>>() {

         public Blob<BlobMetadata> create(BlobMetadata metadata) {
            return new Blob<BlobMetadata>(metadata);
         }

      };
      ParseBlobFromHeadersAndHttpContent<BlobMetadata, Blob<BlobMetadata>> callable = new ParseBlobFromHeadersAndHttpContent(
               metadataParser, objectFactory);
      HttpResponse response = createMock(HttpResponse.class);
      BlobMetadata meta = createMock(BlobMetadata.class);
      expect(metadataParser.apply(response)).andReturn(meta);
      expect(meta.getSize()).andReturn(-1l);
      meta.setSize(-1l);
      expect(response.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH)).andReturn("10485760")
               .atLeastOnce();
      expect(response.getFirstHeaderOrNull("Content-Range")).andReturn("0-10485759/20232760")
               .atLeastOnce();
      expect(response.getHeaders()).andReturn(
               ImmutableMultimap.of("Content-Length", "10485760", "Content-Range",
                        "0-10485759/20232760"));
      meta.setSize(20232760l);
      expect(meta.getSize()).andReturn(20232760l);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getContent()).andReturn(IOUtils.toInputStream("test"));
      replay(response);
      replay(metadataParser);
      replay(meta);

      Blob<BlobMetadata> object = callable.apply(response);
      assertEquals(object.getContentLength(), 10485760);
      assertEquals(object.getMetadata().getSize(), 20232760);
      assertEquals(object.getContentRange(), "0-10485759/20232760");

   }

}