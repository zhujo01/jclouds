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
package org.jclouds.aws.s3.decorators;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.decorators.AddBlobEntity;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;

public class AddS3ObjectEntity extends AddBlobEntity {
   @Inject
   public AddS3ObjectEntity(@Named(PROPERTY_USER_METADATA_PREFIX) String metadataPrefix) {
      super(metadataPrefix);
   }

   public HttpRequest decorateRequest(HttpRequest request, Object entity) {
      Blob<?> object = (Blob<?>) entity;
      checkArgument(object.getMetadata().getSize() >= 0, "size must be set");
      checkArgument(object.getContentLength() <= 5 * 1024 * 1024 * 1024,
               "maximum size for put object is 5GB");
      if (object instanceof S3Object) {
         S3Object s3Object = (S3Object) object;
         if (s3Object.getMetadata().getCacheControl() != null) {
            request.getHeaders().put(HttpHeaders.CACHE_CONTROL,
                     s3Object.getMetadata().getCacheControl());
         }

         if (s3Object.getMetadata().getContentDisposition() != null) {
            request.getHeaders().put("Content-Disposition",
                     s3Object.getMetadata().getContentDisposition());
         }

         if (s3Object.getMetadata().getContentEncoding() != null) {
            request.getHeaders().put(HttpHeaders.CONTENT_ENCODING,
                     s3Object.getMetadata().getContentEncoding());
         }
      }
      return super.decorateRequest(request, entity);
   }
}