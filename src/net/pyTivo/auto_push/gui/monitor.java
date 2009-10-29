package net.pyTivo.auto_push.gui;

import java.util.Date;

import javax.swing.Timer;

import net.pyTivo.auto_push.main.config;
import net.pyTivo.auto_push.main.log;
import net.pyTivo.auto_push.main.mdns;

public class monitor {
   private static long last_date = 0;
   private static mdns m = null;
   public static boolean thread_running = false;
   
   // In GUI mode this runs on a timer every few msecs and is main workhorse
   public static void process(Timer timer) {      
      if (config.gui == null && timer != null) {
         timer.stop();
         return;
      }

      if (config.detectTivos) {
         // Detect Tivos using mdns enabled
         if (m == null) {
            m = new mdns();
         }
         if ( m.getTivos() ) {
            // New TiVo detected, so update GUI choices
            config.gui.SetTivos(config.TIVOS);
         }
      }
      
      // Execute one_loop every config.wait seconds in a separate thread.
      // thread_running used to limit to one thread at a time
      long now = new Date().getTime();
      if (! thread_running && now-last_date > config.wait*1000 && config.numActiveShares() > 0) {
         class AutoThread implements Runnable {
            AutoThread() {}       
            public void run () {
               log.print("Processing...");
               config.push.one_loop();
            }
         }
         thread_running = true;
         AutoThread t = new AutoThread();
         new Thread(t).start();
         last_date = now;
      }            
   }
}
