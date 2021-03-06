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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
public class BaseNamedResource<T extends BaseNamedResource<T>> extends BaseResource<T> {

   public static <T extends BaseNamedResource<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromNamedResource(this);
   }

   public static class Builder<T extends BaseNamedResource<T>> extends BaseResource.Builder<T> {

      protected String name;

      /**
       * @see BaseNamedResource#getName
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      public BaseNamedResource<T> build() {
         return new BaseNamedResource<T>(href, type, name);
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromBaseResource(BaseResource<T> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      public Builder<T> fromNamedResource(BaseNamedResource<T> in) {
         return fromBaseResource(in).name(in.getName());
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public Builder<T> fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes)).name(attributes.get("name"));
      }
   }

   @XmlAttribute
   protected String name;

   protected BaseNamedResource(URI href, String type, String name) {
      super(href, type);
      this.name = checkNotNull(name, "name");
   }

   protected BaseNamedResource() {
      //For JAXB
   }

   public String getName() {
      return name;
   }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BaseNamedResource that = (BaseNamedResource) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String string() {
       return super.string()+", name="+name;
    }
}