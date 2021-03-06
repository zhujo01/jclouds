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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.common.collect.Iterables;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.HardwareConfiguration;
import org.jclouds.tmrk.enterprisecloud.domain.network.AssignedIpAddresses;
import org.jclouds.tmrk.enterprisecloud.domain.network.DeviceNetwork;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineConfigurationOptions;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachines;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code VirtualMachineClient}
 * TODO: don't hard-code uri's it should be possible to determine them but that means chaining the tests potentially.
 * @author Jason King
 */
@Test(groups = "live", testName = "VirtualMachineClientLiveTest")
public class VirtualMachineClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getVirtualMachineClient();
   }

   private VirtualMachineClient client;

   public void testGetVirtualMachines() throws Exception {
       VirtualMachines virtualMachines = client.getVirtualMachines(new URI("/cloudapi/ecloud/virtualMachines/computePools/89"));
       for( VirtualMachine vm : virtualMachines.getVirtualMachines()) {
           VirtualMachine virtualMachine = client.getVirtualMachine(vm.getHref());
           assertNotNull(virtualMachine);
           assertEquals(virtualMachine.getStatus(),VirtualMachine.VirtualMachineStatus.DEPLOYED);
       }
   }

   public void testGetVirtualMachinesWhenMissing() throws Exception {
       VirtualMachines result = client.getVirtualMachines(new URI("/cloudapi/ecloud/virtualMachines/computePools/-1"));
       assertEquals(result, VirtualMachines.builder().build());
   }

   public void testGetVirtualMachine() throws Exception {
       VirtualMachine virtualMachine = client.getVirtualMachine(new URI("/cloudapi/ecloud/virtualMachines/5504"));
       assertNotNull(virtualMachine,"virtualMachine should not be null");
       assertEquals(virtualMachine.getStatus(), VirtualMachine.VirtualMachineStatus.DEPLOYED);
   }

   public void testGetVirtualMachineWhenMissing() throws Exception {
       VirtualMachine virtualMachine = client.getVirtualMachine(new URI("/cloudapi/ecloud/virtualMachines/-1"));
       assertNull(virtualMachine);
   }

   public void testGetAssignedIpAddresses() throws Exception {
        AssignedIpAddresses assignedIpAddresses = client.getAssignedIpAddresses(new URI("/cloudapi/ecloud/virtualMachines/5504/assignedips"));
        assertNotNull(assignedIpAddresses);
        DeviceNetwork network = Iterables.getOnlyElement(assignedIpAddresses.getNetworks().getDeviceNetworks());
        Set<String> ipAddresses = network.getIpAddresses().getIpAddresses();
        assertTrue(ipAddresses.size()>0, "vm has no assigned ip addresses");
   }

   public void testGetAssignedIpAddressesWhenMissing() throws Exception {
        AssignedIpAddresses assignedIpAddresses = client.getAssignedIpAddresses(new URI("/cloudapi/ecloud/virtualMachines/-1/assignedips"));
        assertNull(assignedIpAddresses);
   }

   public void testGetConfigurationOptions() throws Exception {
      VirtualMachineConfigurationOptions configurationOptions = client.getConfigurationOptions(new URI("/cloudapi/ecloud/virtualmachines/5504/configurationoptions"));
      assertNotNull(configurationOptions);
   }

   public void testGetHardwareConfiguration() throws Exception {
      HardwareConfiguration hardwareConfiguration = client.getHardwareConfiguration(new URI("/cloudapi/ecloud/virtualmachines/5504/hardwareconfiguration"));
      assertNotNull(hardwareConfiguration);
   }

   public void testGetHardwareConfigurationWhenMissing() throws Exception {
      HardwareConfiguration result = client.getHardwareConfiguration(new URI("/cloudapi/ecloud/virtualmachines/-1/hardwareconfiguration"));
      assertNull(result);
   }

}
