 package me.solar.apolloLibrary.collection.expiringmap;
 
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
 
   
   public Thread newThread(Runnable r) {
/* 26 */     Thread thread = new Thread(r, String.format(this.nameFormat, new Object[] { Integer.valueOf(this.threadNumber.getAndIncrement()) }));
/* 27 */     thread.setDaemon(true);
/* 28 */     return thread;
   }
 }


/* Location:              E:\Users\Solar\.m2\repo\org\sparkblock\sparky\Sparky-Paper\1.0.0-SNAPSHOT\Sparky-Paper-1.0.0-SNAPSHOT.jar!\org\sparkblock\net\sparkypaper\collection\expiringmap\NamedThreadFactory.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */