package org.jclouds.trmk.enterprisecloud.domain;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.enterprisecloud.domain.internal.BaseResource;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class Task extends BaseResource<Task> {
   public static enum Status {
      /**
       * the task is queued for execution.
       */
      QUEUED,
      /**
       * the task is running.
       */
      RUNNING,
      /**
       * the task failed.
       */
      FAILED,
      /**
       * the task completed successfully.
       */
      SUCCESS,
      /**
       * the task failed with an error.
       */
      ERROR,
      /**
       * Status was not parsed by jclouds.
       */
      UNRECOGNIZED;

      public String value() {
         return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromTask(this);
   }

   public static class Builder extends BaseResource.Builder<Task> {
      protected String operation;
      protected Status status;
      protected NamedResource impactedItem;
      protected Date startTime;
      protected Date completedTime;
      protected String notes;
      protected String errorMessage;
      protected NamedResource initiatedBy;

      /**
       * @see Task#getOperation
       */
      public Builder operation(String operation) {
         this.operation = operation;
         return this;
      }

      /**
       * @see Task#getStatus
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see Task#getImpactedItem
       */
      public Builder impactedItem(NamedResource impactedItem) {
         this.impactedItem = impactedItem;
         return this;
      }

      /**
       * @see Task#getStartTime
       */
      public Builder startTime(Date startTime) {
         this.startTime = startTime;
         return this;
      }

      /**
       * @see Task#getCompletedTime
       */
      public Builder completedTime(Date completedTime) {
         this.completedTime = completedTime;
         return this;
      }

      /**
       * @see Task#getNotes
       */
      public Builder notes(String notes) {
         this.notes = notes;
         return this;
      }

      /**
       * @see Task#getErrorMessage
       */
      public Builder errorMessage(String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      /**
       * @see Task#getInitiatedBy
       */
      public Builder initiatedBy(NamedResource initiatedBy) {
         this.initiatedBy = initiatedBy;
         return this;
      }

      @Override
      public Task build() {
         return new Task(href, type, operation, status, impactedItem, startTime, completedTime, notes, errorMessage,
               initiatedBy);
      }

      public Builder fromTask(Task in) {
         return fromResource(in).operation(in.getOperation()).status(in.getStatus()).impactedItem(in.getImpactedItem())
               .startTime(in.getStartTime()).completedTime(in.getCompletedTime()).notes(in.getNotes())
               .errorMessage(in.getErrorMessage()).initiatedBy(in.getInitiatedBy());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<Task> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

   }

   protected final String operation;
   protected final Status status;
   protected final NamedResource impactedItem;
   protected final Date startTime;
   protected final Date completedTime;
   protected final String notes;
   protected final String errorMessage;
   protected final NamedResource initiatedBy;

   public Task(URI href, String type, String operation, Status status, NamedResource impactedItem, Date startTime,
         @Nullable Date completedTime, @Nullable String notes, @Nullable String errorMessage, NamedResource initiatedBy) {
      super(href, type);
      this.operation = checkNotNull(operation, "operation");
      this.status = checkNotNull(status, "status");
      this.impactedItem = checkNotNull(impactedItem, "impactedItem");
      this.startTime = checkNotNull(startTime, "startTime");
      this.completedTime = completedTime;// null if Queued or Running
      this.notes = notes;
      this.errorMessage = errorMessage;
      this.initiatedBy = checkNotNull(initiatedBy, "initiatedBy");
   }

   /**
    * 
    * 
    * @return name of action performed
    */
   public String getOperation() {
      return operation;
   }

   /**
    * 
    * 
    * @return the status of the task
    */
   public Status getStatus() {
      return status;
   }

   /**
    * 
    * @return the item acted upon
    */
   public NamedResource getImpactedItem() {
      return impactedItem;
   }

   /**
    * 
    * @return time action started
    */
   public Date getStartTime() {
      return startTime;
   }

   /**
    * 
    * @return time action completed, or null if Queued or Running
    */
   @Nullable
   public Date getCompletedTime() {
      return completedTime;
   }

   /**
    * @return notes on action
    */
   public String getNotes() {
      return notes;
   }

   /**
    * @return error message
    */
   public String getErrorMessage() {
      return errorMessage;
   }

   /**
    * 
    * @return the item acted upon
    */
   public NamedResource getInitiatedBy() {
      return initiatedBy;
   }

   @Override
   public String toString() {
      return "[type=" + type + ", href=" + href + ", operation=" + operation + ", status=" + status + ", impactedItem="
            + impactedItem + ", startTime=" + startTime + ", completedTime=" + completedTime + ", notes=" + notes
            + ", errorMessage=" + errorMessage + ", initiatedBy=" + initiatedBy + "]";
   }

}