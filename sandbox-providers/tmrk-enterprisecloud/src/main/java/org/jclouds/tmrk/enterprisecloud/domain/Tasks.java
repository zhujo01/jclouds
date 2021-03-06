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
package org.jclouds.tmrk.enterprisecloud.domain;

import com.google.common.collect.Sets;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual Task elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="Tasks">
 * @author Jason King
 */
@XmlRootElement(name = "Tasks")
public class Tasks {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTasks(this);
   }

   public static class Builder {

       private Set<Task> tasks = Sets.newLinkedHashSet();

       /**
        * @see Tasks#getTasks
        */
       public Builder tasks(Set<Task> tasks) {
          this.tasks = Sets.newLinkedHashSet(checkNotNull(tasks, "tasks"));
          return this;
       }

       public Builder addTask(Task task) {
          tasks.add(checkNotNull(task,"task"));
          return this;
       }

       public Tasks build() {
           return new Tasks(tasks);
       }

       public Builder fromTasks(Tasks in) {
         return tasks(in.getTasks());
       }
   }

   private Tasks() {
      //For JAXB and builder use
   }

   private Tasks(Set<Task> tasks) {
      this.tasks = Sets.newLinkedHashSet(tasks);
   }

   //TODO: There is a total count field

   @XmlElement(name = "Task")
   private LinkedHashSet<Task> tasks = Sets.newLinkedHashSet();

   public Set<Task> getTasks() {
      return Collections.unmodifiableSet(tasks);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Tasks tasks1 = (Tasks) o;

      if (!tasks.equals(tasks1.tasks)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      return tasks.hashCode();
   }

   public String toString() {
      return "["+tasks.toString()+"]";
   }
}
