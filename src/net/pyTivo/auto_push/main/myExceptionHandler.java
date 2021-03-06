package net.pyTivo.auto_push.main;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.SwingUtilities;

public class myExceptionHandler implements Thread.UncaughtExceptionHandler {

   public void uncaughtException(final Thread t, final Throwable e) {
       if (SwingUtilities.isEventDispatchThread()) {
           showException(t, e);
       } else {
           SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   showException(t, e);
               }
           });
       }
   }

   private void showException(Thread t, Throwable e) {
      StringWriter sw =  new StringWriter();
      PrintWriter pw = new PrintWriter(sw,true);
      String detailMessage;
      try {
         e.printStackTrace(pw);
         detailMessage = sw.getBuffer().toString();
      } catch (Exception ee) {
         detailMessage = ee.getMessage();
      }
      log.error(detailMessage);
   }
}