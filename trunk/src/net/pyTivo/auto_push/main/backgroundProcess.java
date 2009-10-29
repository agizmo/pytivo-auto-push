package net.pyTivo.auto_push.main;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.TimeoutException;

public class backgroundProcess {
   private Process proc;
   private ChildDataHandler stderrHandler;
   private ChildDataHandler stdoutHandler;
   private Stack<String> stderr = new Stack<String>();
   private Stack<String> stdout = new Stack<String>();
   private String[] command;
   
   public Boolean run(Stack<String> command) {
      this.command = new String[command.size()];
      for (int i=0; i<command.size(); i++) {
         this.command[i] = command.get(i);
      }
      try {
         proc = Runtime.getRuntime().exec(this.command);
         stderrHandler = new ChildDataHandler(proc.getErrorStream(), stderr);
         stdoutHandler = new ChildDataHandler(proc.getInputStream(), stdout);

         // Capture stdout/stderr to string stacks
         stderrHandler.start();
         stdoutHandler.start();
      } catch (IOException e) {
         stderr.add(e.getMessage());
         return false;
      } catch (NullPointerException e) {
         stderr.add(e.getMessage());
         return false;
      }
      return true;
   }
   
   // Wait with no timeout
   public int Wait() {
      try {
         int r = proc.waitFor();
         stdoutHandler.close();
         stderrHandler.close();
         return r;
      } catch (InterruptedException e) {
         return -1;
      }
   }
   
   // Wait with timeout
   public int Wait(long timeout) throws IOException, InterruptedException, TimeoutException {
      
      class Worker extends Thread {
         private final Process process;
         private Integer exit;
         private Worker(Process process) {
           this.process = process;
         }
         public void run() {
           try { 
             exit = process.waitFor();
           } catch (InterruptedException ignore) {
             return;
           }
         }  
      }
      
      Worker worker = new Worker(proc);
      worker.start();
      try {
        worker.join(timeout);
        if (worker.exit != null)
          return worker.exit;
        else
          throw new TimeoutException();
      } catch(InterruptedException ex) {
        worker.interrupt();
        Thread.currentThread().interrupt();
        throw ex;
      } finally {
        proc.destroy();
        stdoutHandler.close();
        stderrHandler.close();
      }
    }
   
   public Boolean isRunning() {
      try {
         proc.exitValue();
         return true;
      }
      catch (IllegalThreadStateException i) {
         return false;
      }
   }
   
   // Return -1 if still running, exit code otherwise
   public int exitStatus() {
      try {
         int v = proc.exitValue();
         return v;
      }
      catch (IllegalThreadStateException i) {
         return -1;
      }
   }
   
   public void kill() {
      proc.destroy();
   }
   
   public Stack<String> getStderr() {
      return stderr;
   }
   
   public Stack<String> getStdout() {
      return stdout;
   }
   
   public String getStderrLast() {
      try {
         return stderr.lastElement();
      }
      catch (NoSuchElementException n) {
         return "";
      }
   }
   
   public String getStdoutLast() {
      try {
         return stdout.lastElement();
      }
      catch (NoSuchElementException n) {
         return "";
      }
   }

   // NOTE: This is used by taskInfo
   public void setStdoutWatch(Stack<String> s) {
      stdoutHandler.watch = s;
   }

   // NOTE: This is used by taskInfo
   public void setStderrWatch(Stack<String> s) {
      stderrHandler.watch = s;
   }
   
   public void printStderr() {
      log.error(stderr);
   }
   
   public void printStdout() {
      log.print(stdout);
   }
   
   public String toString() {
      String c = "";
      for (int i=0; i<command.length; ++i) {
         if (command[i].matches("^.*\\s+.*$")) {
            c += "\"" + command[i] + "\" ";
         } else {
            c += command[i] + " ";
         }
      }
      return c;
   }

}
