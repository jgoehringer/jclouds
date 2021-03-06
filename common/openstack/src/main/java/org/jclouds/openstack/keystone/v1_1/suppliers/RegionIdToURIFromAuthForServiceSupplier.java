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
package org.jclouds.openstack.keystone.v1_1.suppliers;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.domain.Endpoint;
import org.jclouds.openstack.keystone.v1_1.functions.EndpointToRegion;
import org.jclouds.openstack.keystone.v1_1.functions.EndpointToSupplierURI;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class RegionIdToURIFromAuthForServiceSupplier implements RegionIdToURISupplier {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Supplier<Auth> auth;
   private final EndpointToSupplierURI endpointToSupplierURI;
   private final EndpointToRegion endpointToRegion;
   private final String apiType;
   private final String apiVersion;

   @Inject
   public RegionIdToURIFromAuthForServiceSupplier(Supplier<Auth> auth, EndpointToSupplierURI endpointToSupplierURI,
            EndpointToRegion endpointToRegion, @Assisted("apiType") String apiType,
            @Assisted("apiVersion") String apiVersion) {
      this.auth = auth;
      this.endpointToSupplierURI = endpointToSupplierURI;
      this.endpointToRegion = endpointToRegion;
      this.apiType = apiType;
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      logger.trace("current version of keystone doesn't allow us to validate the apiVersion %s", apiVersion);
      Auth authResponse = auth.get();
      Collection<Endpoint> endpointsForService = authResponse.getServiceCatalog().get(apiType);
      Map<String, Endpoint> regionIdToEndpoint = Maps.uniqueIndex(endpointsForService, endpointToRegion);
      return Maps.transformValues(regionIdToEndpoint, endpointToSupplierURI);
   }

   @Override
   public String toString() {
      return "getPublicURLForService(" + apiType + ")";
   }
}