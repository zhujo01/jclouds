/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.tmrk.enterprisecloud;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.features.BaseTerremarkEnterpriseCloudAsyncClientTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkEnterpriseCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TerremarkEnterpriseCloudAsyncClientTest")
public class TerremarkEnterpriseCloudAsyncClientTest extends
      BaseTerremarkEnterpriseCloudAsyncClientTest<TerremarkEnterpriseCloudAsyncClient> {

   private TerremarkEnterpriseCloudAsyncClient asyncClient;
   private TerremarkEnterpriseCloudClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getTaskClient() != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getTaskClient() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TerremarkEnterpriseCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TerremarkEnterpriseCloudAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(TerremarkEnterpriseCloudAsyncClient.class);
      syncClient = injector.getInstance(TerremarkEnterpriseCloudClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
