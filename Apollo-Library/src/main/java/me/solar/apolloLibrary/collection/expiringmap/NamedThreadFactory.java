 package me.solar.apolloLibrary.collection.expiringmap;
 
 import org.jetbrains.annotations.NotNull;

 import java.util.concurrent.ThreadFactory;
 import java.util.concurrent.atomic.AtomicInteger;
 
 
 public final class NamedThreadFactory
   implements ThreadFactory
 {
/* 10 */   private final AtomicInteger threadNumber = new AtomicInteger(1);
 
 
 
   
   private final String nameFormat;
 
 
 
   
   public NamedThreadFactory(String nameFormat) {
/* 21 */     this.nameFormat = nameFormat;
   }
 
   
   public @NotNull Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r, String.format(this.nameFormat, this.threadNumber.getAndIncrement()));
        thread.setDaemon(true);
        return thread;
   }
 }