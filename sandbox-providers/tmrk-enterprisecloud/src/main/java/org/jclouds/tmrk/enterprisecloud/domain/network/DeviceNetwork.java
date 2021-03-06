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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.jclouds.tmrk.enterprisecloud.domain.internal.Resource;

import javax.xml.bind.annotation.XmlElement;

/**
 * Container for DeviceIps (ipAddresses)
 *  <xs:complexType name="DeviceNetwork">
 * @author Jason King
 */
public class DeviceNetwork extends Resource<DeviceNetwork> {

   //TODO: Builder

   @XmlElement(name = "IpAddresses")
   private DeviceIps ipAddresses = new DeviceIps();

   private DeviceNetwork() {
       //For JAXB
   }

   public DeviceIps getIpAddresses() {
      return ipAddresses;
   }

   @Override
   public String string() {
      return super.string()+"ipAddresses="+ ipAddresses;
   }
}