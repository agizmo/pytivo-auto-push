package net.pyTivo.auto_push.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.pyTivo.auto_push.main.config;
import net.pyTivo.auto_push.main.file;
import net.pyTivo.auto_push.main.log;

public class autoLogView {
   private JDialog dialog = null;
   private JTextArea text = null;
   private String logfile = null;
   
   public autoLogView(JFrame frame, String logfile) {
      this.logfile = logfile;
      
      if ( ! file.isFile(logfile)) {
         log.error("Auto log file not found: " + logfile);
         return;
      }
      
      JPanel content;      
      
      // Define content for dialog window
      content = new JPanel(new GridBagLayout());
      content.setLayout(new GridBagLayout());
      
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      
      // text area
      text = new JTextArea();
      text.setEditable(false);
      text.setLineWrap(true);
      JScrollPane s1 = new JScrollPane(text);
      if (view()) {
         c.ipady = 0;
         c.weighty = 1.0;  // default to vertical stretch
         c.weightx = 1.0;  // default to horizontal stretch
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.anchor = GridBagConstraints.NORTH;
         c.fill = GridBagConstraints.BOTH;
         content.add(s1, c);
        
         // create and display dialog window
         dialog = new JDialog(frame, false); // non-modal dialog
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Destroy when closed
         dialog.setTitle(logfile);
         dialog.setContentPane(content);
         dialog.pack();
         dialog.setSize(600,400);
         dialog.setLocationRelativeTo(config.gui.getJFrame().getJMenuBar().getComponent(0));
         dialog.setVisible(true);
      } else {
         // Deallocate resources and return
         s1 = null;
         text = null;
         c = null;
         content = null;
         return;
      }
   }
     
   // Update text with auto log file contents
   private Boolean view() {
      try {
         BufferedReader log = new BufferedReader(new FileReader(logfile));
         String line = null;
         text.setEditable(true);
         while (( line = log.readLine()) != null) {
            text.append(line + "\n");
         }
         log.close();
         text.setEditable(false);
      }         
      catch (IOException ex) {
         log.error("Log file cannot be read: " + logfile);
         return false;
      }
      return true;
   }

}
