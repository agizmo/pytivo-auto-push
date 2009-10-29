package net.pyTivo.auto_push.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.pyTivo.auto_push.gui.gui;
import net.pyTivo.auto_push.gui.monitor;

public class main {
   private static Timer timer;
   private static Boolean gui_mode = true;
   
   public static void main(String[] argv) {
      
      // Handle any uncaught exceptions
      Thread.setDefaultUncaughtExceptionHandler(new myExceptionHandler());
      
      // Register a shutdown thread
      Runtime.getRuntime().addShutdownHook(new Thread() {
          // This method is called during shutdown
          public void run() {
             // Shutdown message if in non-GUI mode
             if ( config.gui == null ) {
                log.print("SHUTTING DOWN");
             } else {
                config.save();
             }
          }
      });
      
      // Always do this
      getopt(argv);
      config.defineDefaults();
      config.load();
      
      // GUI mode
      if (gui_mode) {
         config.gui = new gui();
         config.gui.getJFrame().setVisible(true);               
         config.gui.update();
         
         config.push = new auto_push(config.watchList);
         
         // Invoke a 300ms period timer for doing the auto pushes in gui mode
         timer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               monitor.process(timer);
            }    
         });
         timer.start();
      } else {      
         // Batch mode infinite loop
         config.push = new auto_push(config.watchList);
         config.push.main_loop();
      }
   }
   
   private static void getopt(String[] argv) {
      int i=0;
      String arg;
      while ( i<argv.length ) {
         arg = argv[i++];
         if (arg.equals("-b")) {
            gui_mode = false;
         }
         else if (arg.equals("-h")) {
            useage();
         }
      }                
   }
   
   // Print available command line options and exit
   private static void useage() {
      System.out.println("-h => Print this help message\n");
      System.out.println("-b => Run in batch mode - loop forever\n");
      System.out.println("With no options => GUI mode\n");
      System.exit(0);
   }   

}
