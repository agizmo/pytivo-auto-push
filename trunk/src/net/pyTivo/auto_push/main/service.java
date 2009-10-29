package net.pyTivo.auto_push.main;

import java.util.Stack;

public class service {
   public static String sname = "auto_push";
   
   // Windows only: Queries service using "sc query sname"
   public static String serviceStatus() {
      Stack<String> command = new Stack<String>();
      command.add("cmd");
      command.add("/c");
      command.add("sc");
      command.add("query");
      command.add(sname);
      backgroundProcess process = new backgroundProcess();
      if ( process.run(command) ) {
         process.Wait();
         Stack<String> result = process.getStdout();
         if (result.size() > 0) {
            Boolean created = false;
            String status = "undetermined";
            for (int i=0; i<result.size(); ++i) {
               if (result.get(i).matches("^SERVICE_NAME.+$"))
                  created = true;
               if (result.get(i).matches("^\\s+STATE.+$")) {
                  status = result.get(i);
                  String[] l = status.split("\\s+");
                  status = l[l.length-1];
               }
            }
            if (created) {
               return sname + " service is installed: STATUS=" + status;
            } else {
               return sname + " service has not been installed";
            }
         }
      } else {
         log.error("Command failed: " + process.toString());
         log.error(process.getStderr());
      }
      return null;
   }
   
   // Windows only: Starts service using "install-service.bat" script
   public static Boolean serviceCreate() {
      Stack<String> command = new Stack<String>();
      String script = config.programDir + "\\service\\win32\\install-service.bat";
      if (! file.isFile(script) ) {
         script = config.programDir + "\\release\\service\\win32\\install-service.bat";
      }
      command.add("cmd");
      command.add("/c");
      command.add(script);
      backgroundProcess process = new backgroundProcess();
      if ( process.run(command) ) {
         process.Wait();
         Stack<String> result = process.getStdout();
         if (result.size() > 0) {
            Boolean good = false;
            // Look for "sname installed"
            for (int i=0; i<result.size(); ++i) {
               if (result.get(i).matches("^.+" + sname + " installed.+$")) {
                  good = true;
               }
            }
            if (good) {
               log.print(sname + " service installed successfully");
               return true;
            } else {
               log.error("There was a problem installing " + sname + " service");
               log.error(process.getStdout());
            }
         } else {
            log.error("Problem running command: " + process.toString());
            log.error(process.getStderr());
         }
      } else {
         log.error("Command failed: " + process.toString());
         log.error(process.getStderr());
      }
      return false;
   }      
   // Windows only: Starts service using "sc start sname"
   public static Boolean serviceStart() {
      Stack<String> command = new Stack<String>();
      command.add("cmd");
      command.add("/c");
      command.add("sc");
      command.add("start");
      command.add(sname);
      backgroundProcess process = new backgroundProcess();
      if ( process.run(command) ) {
         process.Wait();
         Stack<String> result = process.getStdout();
         if (result.size() > 0) {
            // Look for FAILED
            for (int i=0; i<result.size(); ++i) {
               if (result.get(i).matches("^.+FAILED.+$")) {
                  log.error(result);
                  return false;
               }
            }
            // Seemed to work so sleep for a couple of seconds and print status
            try {
               Thread.sleep(2000);
               log.print(serviceStatus());
               return true;
            } catch (InterruptedException e) {
               log.error(e.getMessage());
               return false;
            }               
         }
      } else {
         log.error("Command failed: " + process.toString());
         log.error(process.getStderr());
      }
      return false;
   }
   
   // Windows only: Stops service using "sc stop sname"
   public static Boolean serviceStop() {
      Stack<String> command = new Stack<String>();
      command.add("cmd");
      command.add("/c");
      command.add("sc");
      command.add("stop");
      command.add(sname);
      backgroundProcess process = new backgroundProcess();
      if ( process.run(command) ) {
         process.Wait();
         Stack<String> result = process.getStdout();
         if (result.size() > 0) {
            // Look for FAILED
            for (int i=0; i<result.size(); ++i) {
               if (result.get(i).matches("^.+FAILED.+$")) {
                  log.error(result);
                  return false;
               }
            }
            // Seemed to work so sleep for a couple of seconds and print status
            try {
               Thread.sleep(2000);
               log.print(serviceStatus());
               return true;
            } catch (InterruptedException e) {
               log.error(e.getMessage());
               return false;
            }               
         }
      } else {
         log.error("Command failed: " + process.toString());
         log.error(process.getStderr());
      }
      return false;
   }
   
   // Windows only: Deletes service using "sc delete sname"
   public static Boolean serviceDelete() {
      Stack<String> command = new Stack<String>();
      command.add("cmd");
      command.add("/c");
      command.add("sc");
      command.add("delete");
      command.add(sname);
      backgroundProcess process = new backgroundProcess();
      if ( process.run(command) ) {
         process.Wait();
         Stack<String> result = process.getStdout();
         if (result.size() > 0) {
            // Look for SUCCESS
            for (int i=0; i<result.size(); ++i) {
               if (result.get(i).matches("^.+SUCCESS.*$")) {
                  log.print("Successfully removed " + sname + " service");
                  return true;
               }
            }
            // Did not seem to work
            log.error(result);
            return false;
         }
      } else {
         log.error("Command failed: " + process.toString());
         log.error(process.getStderr());
      }
      return false;
   }

}
