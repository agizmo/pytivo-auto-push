package net.pyTivo.auto_push.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import net.pyTivo.auto_push.main.config;
import net.pyTivo.auto_push.main.file;
import net.pyTivo.auto_push.main.log;
import net.pyTivo.auto_push.main.service;

public class gui {
   private JFrame jFrame = null;
   private JPanel jContentPane = null;
   private JMenuBar jJMenuBar = null;
   private JMenu fileMenu = null;
   private JMenu serviceMenu = null;
   private JMenuItem saveMenuItem = null;
   private JMenuItem exitMenuItem = null;
   private JMenuItem serviceStatusMenuItem = null;
   private JMenuItem serviceInstallMenuItem = null;
   private JMenuItem serviceStartMenuItem = null;
   private JMenuItem serviceStopMenuItem = null;
   private JMenuItem serviceRemoveMenuItem = null;
   private JMenuItem serviceViewLogMenuItem = null;
   private JTextField pyTivo_config = null;
   private JCheckBox auto_push = null;
   private JComboBox tivos = null;
   private JTable table = null;
   private JScrollPane table_scroll = null;
   private JTextPane text = null;
   private JScrollPane text_scroll = null;
   private Boolean AutoPushListenerEnabled = true;
   private JFileChooser Browser = null;
   private ToolTipManager toolTips = null;
   
   public JFrame getJFrame() {
      if (jFrame == null) {
         jFrame = new JFrame();
         jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         jFrame.setJMenuBar(getJJMenuBar());
         jFrame.setContentPane(getJContentPane());
         jFrame.pack();
         jFrame.setTitle(config.auto_push);
 
         if (config.toolTips) {
            toolTips = ToolTipManager.sharedInstance();
            toolTips.setDismissDelay(config.timeout_tooltips*1000);
            toolTips.setInitialDelay(500);
            setToolTips();
            toolTips.setEnabled(true);
         }

      }
      return jFrame;
   }
   
   private Container getJContentPane() {
      if (jContentPane == null) {
         jContentPane = new JPanel(new GridBagLayout());
         
         GridBagConstraints c = new GridBagConstraints();
         c.insets = new Insets(0, 2, 0, 2);
         c.ipadx = 0;
         c.ipady = 0;
         c.weighty = 0.0;  // default to no vertical stretch
         c.weightx = 0.0;  // default to no horizontal stretch
         c.gridwidth = 1;
         c.gridheight = 1;
         c.anchor = GridBagConstraints.CENTER;
         c.fill = GridBagConstraints.HORIZONTAL;

         int gx=0, gy=0;
         
         // pyTivo_config text field
         JLabel config_label = new JLabel("pyTivo config file");
         pyTivo_config = new JTextField();
         pyTivo_config.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               log.print("Reading config file: " + pyTivo_config.getText());
               config.pyTivoConf = config.removeLeadingTrailingSpaces(pyTivo_config.getText());
               config.pyTivoConfUpdate();
               update();
            }
         });
         pyTivo_config.addMouseListener(
               new MouseAdapter() {
                  public void mouseClicked(MouseEvent e) {
                     if(e.getClickCount() == 2) {
                        log.print("Bringing up file browser...");
                        if (Browser == null) {
                           class TextFilter extends FileFilter {
                              public boolean accept(File f) {
                                if (f.isDirectory())
                                  return true;
                                String s = f.getName().toLowerCase();
                                if ( s.endsWith(".conf") ) {
                                   return true;
                                }
                                return false;
                              }
                              public String getDescription() {
                                return "conf";
                              }
                            }
                           Browser = new JFileChooser(config.programDir);
                           Browser.setMultiSelectionEnabled(false);
                           Browser.addChoosableFileFilter(new TextFilter());
                           Browser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        }
                        int result = Browser.showDialog(pyTivo_config, "Choose File");
                        if (result == JFileChooser.APPROVE_OPTION) {
                           pyTivo_config.setText(Browser.getSelectedFile().getPath());
                        }
                     }
                  }
               }
            );
         c.gridx = gx;
         c.gridy = gy;
         jContentPane.add(config_label, c);
         gx++;
         c.gridx = gx;
         c.weightx = 1;
         c.gridwidth = 7;
         jContentPane.add(pyTivo_config, c);
         
         // auto_push check box
         auto_push = new JCheckBox("Auto Push", false);
         auto_push.addItemListener(
            new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                   if (AutoPushListenerEnabled) {
                      String tivoName = "";
                      if (auto_push.isSelected()) {
                         tivoName = (String)tivos.getSelectedItem();
                      }
                      int[] rows = getTableSelectedRows();
                      if (rows != null) {
                         for (int i=0; i<rows.length; ++i) {                         
                            setTableTivoValue(rows[i], tivoName);
                         }
                      }
                   }
                }
            }
         );

         gx = 0;
         gy++;
         c.gridx = gx;
         c.gridy = gy;
         c.weightx = 0;
         c.gridwidth = 1;
         jContentPane.add(auto_push, c);
         
         // tivo combo box
         JLabel tivos_label = new JLabel("Tivo");
         gx++;
         c.gridx = gx;
         c.gridy = gy;
         jContentPane.add(tivos_label, c);
         
         tivos = new JComboBox();
         // If selection changes & auto_push enabled and table rows selected
         // then update table rows to selected item
         tivos.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                  if (auto_push.isSelected()) {
                     int[] rows = getTableSelectedRows();
                     if (rows != null) {
                        String tivoName = (String)tivos.getSelectedItem();
                        for (int i=0; i<rows.length; ++i) {
                           setTableTivoValue(rows[i], tivoName);
                        }
                     }
                  }
               }
            }
         });
         gx++;
         c.gridx = gx;
         c.gridy = gy;
         jContentPane.add(tivos, c);
         
         // table
         Object[][] data = {};
         String[] headers = {"SHARE", "PATH", "AUTO PUSH"};
         table = new JTable(data, headers);
         TableModel myModel = new MyTableModel(data, headers);
         table.setModel(myModel);
         table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         table_scroll = new JScrollPane(table);
         table_scroll.setPreferredSize(new Dimension(500, 100));
         // Define selection listener to update dialog fields according
         // to selected row
         table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
               if (e.getValueIsAdjusting()) return;
               TableRowSelected(table.getSelectedRow());
            }
         });
         gx = 0;
         gy++;
         c.gridx = gx;
         c.gridy = gy;
         c.weightx = 1;
         c.weighty = 0.7;
         c.gridwidth = 8;
         c.fill = GridBagConstraints.BOTH;
         jContentPane.add(table_scroll, c);
         
         // Message area
         text = new JTextPane();
         text.setEditable(false);
         text_scroll = new JScrollPane(text);
         gy++;
         c.gridx = gx;
         c.gridy = gy;
         c.weighty = 1;
         jContentPane.add(text_scroll, c);
         text_scroll.setPreferredSize(new Dimension(500,100));
         
         // Pack table columns when content pane resized
         jContentPane.addHierarchyBoundsListener(new HierarchyBoundsListener() {
            public void ancestorMoved(HierarchyEvent arg0) {
               // Don't care about movement
            }
            public void ancestorResized(HierarchyEvent arg0) {
               packColumns(table, 2);
            }
         });
      }
      return jContentPane;
   }

   
   private JMenuBar getJJMenuBar() {
      if (jJMenuBar == null) {
         jJMenuBar = new JMenuBar();
         jJMenuBar.add(getFileMenu());
         if (config.OS.equals("windows"))
            jJMenuBar.add(getServiceMenu());

      }
      return jJMenuBar;
   }
   
   private JMenu getFileMenu() {
      if (fileMenu == null) {
         fileMenu = new JMenu();
         fileMenu.setText("File");
         fileMenu.add(getSaveMenuItem());
         fileMenu.add(getExitMenuItem());
      }
      return fileMenu;
   }
   
   private JMenuItem getSaveMenuItem() {
      if (saveMenuItem == null) {
         saveMenuItem = new JMenuItem();
         saveMenuItem.setText("Save");
         saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
               Event.CTRL_MASK, true));
         saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               log.print("Saving configuration...");
               config.save();
            }
         });
      }
      return saveMenuItem;
   }
   
   private JMenuItem getExitMenuItem() {
      if (exitMenuItem == null) {
         exitMenuItem = new JMenuItem();
         exitMenuItem.setText("Exit");
         exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
               Event.CTRL_MASK, true));
         exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               System.exit(0);
            }
         });
      }
      return exitMenuItem;
   }
   
   private JMenu getServiceMenu() {
      if (serviceMenu == null) {
         serviceMenu = new JMenu();
         serviceMenu.setText("Service");
         serviceMenu.add(getServiceStatusMenuItem());
         serviceMenu.add(getServiceInstallMenuItem());
         serviceMenu.add(getServiceStartMenuItem());
         serviceMenu.add(getServiceStopMenuItem());
         serviceMenu.add(getServiceRemoveMenuItem());
         serviceMenu.add(getViewLogMenuItem());
      }
      return serviceMenu;
   }

   private JMenuItem getServiceStatusMenuItem() {
      if (serviceStatusMenuItem == null) {
         serviceStatusMenuItem = new JMenuItem();
         serviceStatusMenuItem.setText("Status");
         serviceStatusMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String query = service.serviceStatus();
               if (query != null) {
                  log.print(query);
               }
            }
         });
      }
      return serviceStatusMenuItem;
   }

   private JMenuItem getServiceInstallMenuItem() {
      if (serviceInstallMenuItem == null) {
         serviceInstallMenuItem = new JMenuItem();
         serviceInstallMenuItem.setText("Install");
         serviceInstallMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String query = service.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+STATUS.+$")) {
                     log.print("auto_push service already installed");
                     return;
                  }
                  service.serviceCreate();
               }
            }
         });
      }
      return serviceInstallMenuItem;
   }

   private JMenuItem getServiceStartMenuItem() {
      if (serviceStartMenuItem == null) {
         serviceStartMenuItem = new JMenuItem();
         serviceStartMenuItem.setText("Start");
         serviceStartMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String query = service.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+RUNNING$")) {
                     log.print("auto_push service already running");
                     return;
                  }
                  // Save current config to make sure it is up to date
                  config.save();
                  service.serviceStart();
               }
            }
         });
      }
      return serviceStartMenuItem;
   }

   private JMenuItem getServiceStopMenuItem() {
      if (serviceStopMenuItem == null) {
         serviceStopMenuItem = new JMenuItem();
         serviceStopMenuItem.setText("Stop");
         serviceStopMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String query = service.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+STOPPED$")) {
                     log.print("auto_push service already stopped");
                     return;
                  }
                  service.serviceStop();
               }
            }
         });
      }
      return serviceStopMenuItem;
   }

   private JMenuItem getServiceRemoveMenuItem() {
      if (serviceRemoveMenuItem == null) {
         serviceRemoveMenuItem = new JMenuItem();
         serviceRemoveMenuItem.setText("Remove");
         serviceRemoveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               String query = service.serviceStatus();
               if (query != null) {
                  if (query.matches("^.+not been created.+$")) {
                     log.print("auto_push service not installed");
                     return;
                  }
                  service.serviceDelete();
               }
            }
         });
      }
      return serviceRemoveMenuItem;
   }

   private JMenuItem getViewLogMenuItem() {
      if (serviceViewLogMenuItem == null) {
         serviceViewLogMenuItem = new JMenuItem();
         serviceViewLogMenuItem.setText("View Log");
         serviceViewLogMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (file.isFile(config.logfile)) {
                  new autoLogView(jFrame, config.logfile);
               } else {
                  log.print("Log file does not exist: " + config.logfile);
               }
            }
         });
      }
      return serviceViewLogMenuItem;
   }
   
   // Set encoding combobox choices
   public void SetTivos(Stack<String> values) {
      
      // Get existing setting in combobox
      String current = null;
      if (tivos.getComponentCount() > 0) {
         current = (String)tivos.getSelectedItem();
      }
      tivos.removeAllItems();
      for (int i=0; i<values.size(); ++i) {
         tivos.addItem(values.get(i));
      }
      if (current != null)
         tivos.setSelectedItem(current);
   }   
   
   private void addTableRow(Hashtable<String,String> h) {
      Object[] data = new Object[3];
      data[0] = h.get("share");
      data[1] = h.get("path");
      if (h.containsKey("tivo")) {
         data[2] = h.get("tivo");
      } else {
         data[2] = "";
      }
      TableModel model = table.getModel();
      ((DefaultTableModel) model).addRow(data);
      packColumns(table,2);
   }
   
   /*private void removeTableRow(int row) {
      DefaultTableModel dm = (DefaultTableModel)table.getModel();
      dm.removeRow(row);
      packColumns(table,2);
   }*/
   
   private void clearTable() {
      DefaultTableModel model = (DefaultTableModel)table.getModel(); 
      model.setNumRows(0);
      packColumns(table,2);
   }
   
   private int[] getTableSelectedRows() {
      int[] rows = table.getSelectedRows();
      if (rows.length <= 0)
         return null;
      return rows;
   }   
   
   public void update() {
      pyTivo_config.setText(config.pyTivoConf);
      SetTivos(config.TIVOS);
      Hashtable<String,String> h;
      clearTable();
      if (config.watchList != null) {
         for (int i=0; i<config.watchList.size(); ++i) {
            h = config.watchList.get(i);
            addTableRow(h);
         }
      }
   }
   
   // This will update component settings according to selected row data
   private void TableRowSelected(int row) {
      if (row == -1) return;
      String tivoName = (String)table.getValueAt(row, getColumnIndex("AUTO PUSH"));
      if (tivoName.length() > 0) {
         setAutoPushValue(true);
         tivos.setSelectedItem(tivoName);
      } else {
         setAutoPushValue(false);
      }
   }
   
   private void setTableTivoValue(int row, String tivoName) {
      if (row == -1) return;
      table.setValueAt(tivoName, row, getColumnIndex("AUTO PUSH"));
      // Update related config watchList hash
      String share = (String)table.getValueAt(row, getColumnIndex("SHARE"));
      Hashtable<String,String> h = config.getShareHash(share);
      if (h == null) {
         log.error("No config entry found for this table entry");
      } else {
         if (tivoName.length() > 0) {
            h.put("tivo", tivoName);
         } else {
            if (h.containsKey("tivo"))
               h.remove("tivo");
         }
         packColumns(table,2);
      }
   }
   
   private int getColumnIndex(String name) {
      String cname;
      for (int i=0; i<table.getColumnCount(); i++) {
         cname = (String)table.getColumnModel().getColumn(i).getHeaderValue();
         if (cname.equals(name)) return i;
      }
      return -1;
   }

   private void setAutoPushValue(Boolean value) {
      AutoPushListenerEnabled = false;
      auto_push.setSelected(value);
      AutoPushListenerEnabled = true;
   }
      
   // Override some default table model actions
   class MyTableModel extends DefaultTableModel {
      private static final long serialVersionUID = 1L;

      public MyTableModel(Object[][] data, Object[] columnNames) {
         super(data, columnNames);
      }
      
      // Set all cells uneditable
      public boolean isCellEditable(int row, int column) {        
         return false;
      }
   }
   
   // Pack all table columns to fit widest cell element
   private void packColumns(JTable table, int margin) {
      for (int c=0; c<table.getColumnCount(); c++) {
          packColumn(table, c, 2);
      }
   }
   
   // Sets the preferred width of the visible column specified by vColIndex. The column
   // will be just wide enough to show the column head and the widest cell in the column.
   // margin pixels are added to the left and right
   // (resulting in an additional width of 2*margin pixels).
   private void packColumn(JTable table, int vColIndex, int margin) {
       DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
       TableColumn col = colModel.getColumn(vColIndex);
       int width = 0;
   
       // Get width of column header
       TableCellRenderer renderer = col.getHeaderRenderer();
       if (renderer == null) {
           renderer = table.getTableHeader().getDefaultRenderer();
       }
       Component comp = renderer.getTableCellRendererComponent(
           table, col.getHeaderValue(), false, false, 0, 0);
       width = comp.getPreferredSize().width;
   
       // Get maximum width of column data
       for (int r=0; r<table.getRowCount(); r++) {
           renderer = table.getCellRenderer(r, vColIndex);
           comp = renderer.getTableCellRendererComponent(
               table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
           width = Math.max(width, comp.getPreferredSize().width);
       }
   
       // Add margin
       width += 2*margin;
              
       // Set the width
       col.setPreferredWidth(width);
       
       // Adjust PATH column to fit available
       int last = getColumnIndex("PATH");
       if (vColIndex == last) {
          int twidth = table.getPreferredSize().width;
          int awidth = getJContentPane().getWidth();
          int offset = table_scroll.getVerticalScrollBar().getPreferredSize().width+4*margin;
          if ((awidth-offset) > twidth) {
             width += awidth-offset-twidth;
             col.setPreferredWidth(width);
          }
       }
   }
   
   // text pane functions
   public void print(String s) {
      appendText(Color.black, s + "\n");
   }
   
   public void warn(String s) {
      appendText(Color.blue, s + "\n");
   }
   
   public void error(String s) {
      appendText(Color.red, s + "\n");
      java.awt.Toolkit.getDefaultToolkit().beep();
   }
   
   public void print(Stack<String> s) {
      for (int i=0; i<s.size(); ++i)
         appendText(Color.black, s.get(i) + "\n");
   }
   
   public void warn(Stack<String> s) {
      for (int i=0; i<s.size(); ++i)
         appendText(Color.blue, s.get(i) + "\n");
   }
   
   public void error(Stack<String> s) {
      for (int i=0; i<s.size(); ++i)
         appendText(Color.red, s.get(i) + "\n");
      java.awt.Toolkit.getDefaultToolkit().beep();
   }
   
   public void appendText(Color c, String s) {
      text.setEditable(true);
      StyleContext sc = StyleContext.getDefaultStyleContext();
      AttributeSet aset = sc.addAttribute(
         SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c
      );

      int len = text.getDocument().getLength();
      text.setCaretPosition(len);
      text.setCharacterAttributes(aset, false);
      text.replaceSelection(s);
      text.setEditable(false);
   }

   // Component tooltip setup
   public void setToolTips() {
      pyTivo_config.setToolTipText(getToolTip("pyTivo_config"));
      auto_push.setToolTipText(getToolTip("auto_push"));
      tivos.setToolTipText(getToolTip("tivos"));
   }
   
   public String getToolTip(String component) {
      String text = "";
      if (component.equals("pyTivo_config")) {
         text =  "<b>pyTivo config file</b><br>";
         text += "Double click in text field to bring up browser to find and set full path<br>";
         text += "to your <b>pyTivo.conf</b> file. This is where information on available pyTivo shares<br>";
         text += "is contained as well as port and ffmpeg settings used by this program.<br>";
         text += "Once you enter the proper file name press <b>Return</b> to apply it.";
      }
      else if (component.equals("auto_push")) {
         text =  "<b>Auto Push</b><br>";
         text += "Select a share in table below and select TiVo you want to auto push videos<br>";
         text += "to from that share. This toggles auto push on/off for this share.<br>";
         text += "An empty TIVO column means auto push to that share is currently disabled.";
      }
      else if (component.equals("tivos")) {
         text =  "<b>TiVos</b><br>";
         text += "These are detected TiVos on your home network. Select a TiVo here you want to<br>";
         text += "push to along with a share in the table below, then toggle <b>Auto Push</b> on/off<br>";
         text += "as desired to enable/disable auto push of videos in that share to that TiVo.";
      }
      if (text.length() > 0) {
         text = "<html>" + text + "</html>";
      }
      return text;
   }

}
