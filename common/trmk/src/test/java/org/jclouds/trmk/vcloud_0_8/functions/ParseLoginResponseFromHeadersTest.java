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
package org.jclouds.trmk.vcloud_0_8.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.io.Payloads;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.functions.ParseLoginResponseFromHeaders;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code ParseLoginResponseFromHeaders}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ParseLoginResponseFromHeadersTest")
public class ParseLoginResponseFromHeadersTest extends BaseHandlerTest {

   private ParseLoginResponseFromHeaders parser;

   @BeforeTest
   void setUp() {
      parser = injector.getInstance(ParseLoginResponseFromHeaders.class);
   }

   @Test
   public void testApply() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> of("x-vcloud-authorization",
               "vcloud-token=9er4d061-4bff-48fa-84b1-5da7166764d2; path=/"));
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      VCloudSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "9er4d061-4bff-48fa-84b1-5da7166764d2");
      assertEquals(reply.getOrgs(), ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               TerremarkVCloudMediaType.ORG_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));

   }

   @Test
   public void testApplyBlueLock() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> of("x-vcloud-authorization",
               "MUKOJ2HoAfoMmLnHRp4esNb2MtWscCLLhVysnsIsCG0="));
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      VCloudSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "MUKOJ2HoAfoMmLnHRp4esNb2MtWscCLLhVysnsIsCG0=");
      assertEquals(reply.getOrgs(), ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               TerremarkVCloudMediaType.ORG_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));

   }

   @Test
   public void testApplyTerremark() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> of("Set-Cookie",
               "vcloud-token=37ce2715-9aba-4f48-8e45-2db8a8da702d; path=/"));
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      VCloudSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "37ce2715-9aba-4f48-8e45-2db8a8da702d");
      assertEquals(reply.getOrgs(), ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               TerremarkVCloudMediaType.ORG_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));

   }

   @Test
   public void testApplyTerremarkMultipleCookies() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> builder().put("Set-Cookie",
               "NSC_ESUO_21654_72.46.239.132_443=fooo;expires=Thu, 02-Jun-2011 17:19:26 GMT;path=/;secure;httponly")
               .put("Set-Cookie", "vcloud-token=37ce2715-9aba-4f48-8e45-2db8a8da702d; path=/").build());
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      VCloudSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "37ce2715-9aba-4f48-8e45-2db8a8da702d");
      assertEquals(reply.getOrgs(), ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               TerremarkVCloudMediaType.ORG_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));

   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testUnmatchedCookieThrowsHttpResponseException() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> builder().put("Set-Cookie",
               "NSC_ESUO_21654_72.46.239.132_443=fooo;expires=Thu, 02-Jun-2011 17:19:26 GMT;path=/;secure;httponly")
               .build());
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      parser.apply(response);
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testNoThrowsHttpResponseException() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> of());
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      parser.apply(response);
   }

   @Test
   public void testApplyVirtacore() {
      HttpResponse response = new HttpResponse(200, "OK", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream("/orglist.xml")), ImmutableMultimap.<String, String> of("x-vcloud-authorization",
               "vcloud-token=IPy0w7UGD4lwtdWAK/ZVzfuLK+dztxGRqsOhWqV0i48="));
      response.getPayload().getContentMetadata().setContentType("Content-Type: application/xml; charset=utf-8");
      response.getPayload().getContentMetadata().setContentLength(307l);

      VCloudSession reply = parser.apply(response);
      assertEquals(reply.getVCloudToken(), "IPy0w7UGD4lwtdWAK/ZVzfuLK+dztxGRqsOhWqV0i48=");
      assertEquals(reply.getOrgs(), ImmutableMap.of("adrian@jclouds.org", new ReferenceTypeImpl("adrian@jclouds.org",
               TerremarkVCloudMediaType.ORG_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));

   }
}
