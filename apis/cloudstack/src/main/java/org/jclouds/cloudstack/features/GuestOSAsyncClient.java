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
package org.jclouds.cloudstack.features;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.functions.ParseIdToNameEntryFromHttpResponse;
import org.jclouds.cloudstack.functions.ParseIdToNameFromHttpResponse;
import org.jclouds.cloudstack.options.ListOSTypesOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see AsyncJobClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface GuestOSAsyncClient {

   /**
    * @see GuestOSClient#listOSTypes
    */
   @GET
   @QueryParams(keys = "command", values = "listOsTypes")
   @SelectJson("ostype")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<OSType>> listOSTypes(ListOSTypesOptions... options);

   /**
    * @see OSTypeClient#getOSType
    */
   @GET
   @QueryParams(keys = "command", values = "listOsTypes")
   @SelectJson("ostype")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<OSType> getOSType(@QueryParam("id") long id);

   /**
    * @see GuestOSClient#listOSCategories
    */
   @GET
   @QueryParams(keys = "command", values = "listOsCategories")
   @ResponseParser(ParseIdToNameFromHttpResponse.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Map<Long, String>> listOSCategories();

   /**
    * @see GuestOSClient#getOSCategory
    */
   @GET
   @QueryParams(keys = "command", values = "listOsCategories")
   @ResponseParser(ParseIdToNameEntryFromHttpResponse.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Map.Entry<Long, String>> getOSCategory(@QueryParam("id") long id);

}
