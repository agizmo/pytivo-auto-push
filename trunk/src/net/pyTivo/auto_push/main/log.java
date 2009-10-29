package net.pyTivo.auto_push.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class log {
   private static String n = "\r\n";
   private static String message;
   
   public static void print(String s) {
      if (s != null && s.length() > 0) {
         String time = getDetailedTime();
         message = time + " " + s + n;
         if (config.gui != null) {
            config.gui.print(s);
         } else {
            try {
               BufferedWriter ofp = new BufferedWriter(new FileWriter(config.logfile, true));
               System.out.print(message);
               ofp.write(message);
               ofp.close();
            } catch (IOException ex) {
               System.out.print(message);
            }
         }
      }
   }
   
   public static void error(String s) {
      if (s != null && s.length() > 0) {
         String time = getDetailedTime();
         message = time + " ERROR: " + s + n;
         if (config.gui != null) {
            config.gui.error(s);
         } else {
            try {
               BufferedWriter ofp = new BufferedWriter(new FileWriter(config.logfile, true));
               System.out.print(message);
               ofp.write(message);
               ofp.close();
            } catch (IOException ex) {
               System.out.print(message);
            }
         }
      }
   }
   
   public static void print(Stack<String> s) {
      if (s != null && s.size() > 0) {
         for (int i=0; i<s.size(); ++i) {
            print(s.get(i));
         }
      }
   }
   
   public static void error(Stack<String> s) {
      if (s != null && s.size() > 0) {
         for (int i=0; i<s.size(); ++i) {
            error(s.get(i));
         }
      }
   }
   
   private static String getDetailedTime() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
      return sdf.format(new Date());
   }

}
