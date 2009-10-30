package net.pyTivo.auto_push.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Stack;

import net.pyTivo.auto_push.gui.gui;

public class config {
   public static String auto_push = "pyTivo auto push v0.1a";
   public static Stack<Hashtable<String,String>> watchList = null;
   public static Hashtable<String,String> pushed = new Hashtable<String,String>();
   public static Stack<String> TIVOS = new Stack<String>();
   public static String pyTivoConf = "";
   public static String ffmpeg = "ffmpeg";
   public static String programDir = ".";
   public static String configIni = "config.ini";
   public static String logfile = "auto_push.log";
   public static String trackingFile = "auto_push.txt";
   public static String host = "localhost";
   public static String port = "9032";
   public static int wait = 20; // loop time in seconds to wait to check files again
   public static int timeout_http = 10; // http connection timeout in seconds
   public static int timeout_ffmpeg = 10; // ffmpeg execution timeout in seconds
   public static Boolean toolTips = true;
   public static int timeout_tooltips = 30;
   public static Boolean detectTivos = true;
   public static gui gui = null;
   public static auto_push push = null;
   public static String username = null;
   public static String password = null;
   public static String OS = "other";

   public static Stack<Hashtable<String,String>> getPyTivoShares(String conf) {
      Stack<Hashtable<String,String>> shares = config.parsePyTivoConf(conf);
      if (shares == null) {
         log.error("No video shares found for file: " + conf);
         return null;
      } else {
         return shares;
      }
   }
   
   public static Stack<Hashtable<String,String>> parsePyTivoConf(String config) {
      Stack<Hashtable<String,String>> s = new Stack<Hashtable<String,String>>();
      username = null;
      password = null;
      
      try {
         BufferedReader ifp = new BufferedReader(new FileReader(config));
         String line = null;
         String key = null;
         Hashtable<String,String> h = new Hashtable<String,String>();
         while (( line = ifp.readLine()) != null) {
            // Get rid of leading and trailing white space
            line = line.replaceFirst("^\\s*(.*$)", "$1");
            line = line.replaceFirst("^(.*)\\s*$", "$1");
            if (line.length() == 0) continue; // skip empty lines
            if (line.matches("^#.+")) continue; // skip comment lines
            if (line.matches("^\\[.+\\]")) {
               key = line.replaceFirst("\\[", "");
               key = key.replaceFirst("\\]", "");
               if ( ! h.isEmpty() ) {
                  if (h.containsKey("share") && h.containsKey("path")) {
                     s.add(h);
                  }
                  h = new Hashtable<String,String>();
               }
               continue;               
            }
            if (key == null) continue;
            
            if (key.equalsIgnoreCase("server")) {
               if (line.matches("(?i)^ffmpeg\\s*=.+")) {
                  String[] l = line.split("=");
                  if (l.length > 1) {
                     ffmpeg = removeLeadingTrailingSpaces(l[1]);
                  }
               }
               if (line.matches("(?i)^port\\s*=.+")) {
                  String[] l = line.split("=");
                  if (l.length > 1) {
                     port = removeLeadingTrailingSpaces(l[1]);
                  }
               }
               if (line.matches("(?i)^tivo_username\\s*=.+")) {
                  String[] l = line.split("=");
                  if (l.length > 1) {
                     username = removeLeadingTrailingSpaces(l[1]);
                  }
               }
               if (line.matches("(?i)^tivo_password\\s*=.+")) {
                  String[] l = line.split("=");
                  if (l.length > 1) {
                     password = removeLeadingTrailingSpaces(l[1]);
                  }
               }
               continue;
            }
            if (line.matches("(?i)^type\\s*=.+")) {
               if (line.matches("(?i)^.+=\\s*video.*")) {
                  if ( ! h.containsKey("share") ) {
                     h.put("share", key);
                  }
               }
               continue;
            }
            if (line.matches("(?i)^path\\s*=.+")) {
               String[] l = line.split("=");
               if (l.length > 1) {
                  h.put("path", removeLeadingTrailingSpaces(l[1]));
               }
            }
         }
         ifp.close();
         if ( ! h.isEmpty() ) {
            if (h.containsKey("share") && h.containsKey("path")) {
               s.add(h);
            }
         }
         
         // tivo_username & tivo_password are required for pushes to work
         if (username == null) {
            log.error("Required 'tivo_username' is not set in config file: " + config);
         }
         if (password == null) {
            log.error("Required 'tivo_password' is not set in config file: " + config);
         }

      }
      catch (Exception ex) {
         log.error("Problem parsing config file: " + config);
         log.error(ex.toString());
         return null;
      }
      
      return s;
   }
   
   public static void addTivo(String tivoName) {
      config.TIVOS.add(tivoName);
   }
     
   public static Stack<String> getTivoNames() {
      return config.TIVOS;     
   }
   
   public static Hashtable<String,String> getShareHash(String share) {
      Hashtable<String,String> h;
      for (int i=0; i<config.watchList.size(); i++) {
         h = config.watchList.get(i);
         if (h.containsKey("share")) {
            if (h.get("share").equals(share))
               return h;
         }
      }
      return null;
   }
   
   public static String removeLeadingTrailingSpaces(String s) {
      // Remove leading & traling spaces from name
      s = s.replaceFirst("^\\s*", "");
      s = s.replaceFirst("\\s*$", "");
      return s;
   }

   private static String urlDecode(String s) {
      try {
         return(URLDecoder.decode(s, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
         log.error(e.getMessage());
         return s;
      }
   }

   public static void defineDefaults() {
      String s = File.separator;

      if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
         config.OS = "windows";
      }

      // Define programDir based on location of jar file
      programDir = new File(
         config.class.getProtectionDomain().getCodeSource().getLocation().getPath()
      ).getParent();
      programDir = urlDecode(programDir);
      
      configIni = programDir + s + "config.ini";
      logfile   = programDir + s + "auto_push.log";
   }
   
   // Save current settings in memory to configIni
   public static Boolean save() {
      try {
         String eol = "\r";
         BufferedWriter ofp = new BufferedWriter(new FileWriter(configIni));
         ofp.write("# auto_push config.ini file\n");
         ofp.write("<host>" + eol);
         ofp.write(host + eol);
         ofp.write("<pyTivo.conf>" + eol);
         ofp.write(pyTivoConf + eol);
         ofp.write("<ffmpeg>" + eol);
         ofp.write(ffmpeg + eol);
         ofp.write("<wait>" + eol);
         ofp.write(wait + eol);
         ofp.write("<timeout_http>" + eol);
         ofp.write(timeout_http + eol);
         ofp.write("<timeout_ffmpeg>" + eol);
         ofp.write(timeout_ffmpeg + eol);
         ofp.write("<trackingFile>" + eol);
         ofp.write(trackingFile + eol);
         ofp.write("<tivos>" + eol);
         for (int i=0; i<TIVOS.size(); ++i) {
            ofp.write(TIVOS.get(i) + eol);
         }
         ofp.write("<watchList>" + eol);
         for (int i=0; i<watchList.size(); ++i) {
            ofp.write(watchList.get(i).get("share"));
            ofp.write("," + watchList.get(i).get("path"));
            ofp.write(",");
            if (watchList.get(i).containsKey("tivo")) {
               ofp.write(watchList.get(i).get("tivo"));
            }
            ofp.write(eol);
         }
         ofp.close();
         
      }         
      catch (Exception ex) {
         log.error("Problem writing to config file: " + configIni);
         log.error(ex.toString());
         return false;
      }
      
      return true;
   }
   
   // Read current settings from configIni
   public static Boolean load() {
      watchList = new Stack<Hashtable<String,String>>();
      if (! file.isFile(configIni))
         return false;
      try {
         BufferedReader ini = new BufferedReader(new FileReader(configIni));
         String line = null;
         String key = null;
         while (( line = ini.readLine()) != null) {
            // Get rid of leading and trailing white space
            line = line.replaceFirst("^\\s*(.*$)", "$1");
            line = line.replaceFirst("^(.*)\\s*$", "$1");
            if (line.length() == 0) continue; // skip empty lines
            if (line.matches("^#.+")) continue; // skip comment lines
            if (line.matches("^<.+>")) {
               key = line.replaceFirst("<", "");
               key = key.replaceFirst(">", "");
               continue;
            }
            if (key.equals("host")) {
               host = removeLeadingTrailingSpaces(line);
            }
            if (key.equals("pyTivo.conf")) {
               pyTivoConf = removeLeadingTrailingSpaces(line);
            }
            if (key.equals("ffmpeg")) {
               ffmpeg = removeLeadingTrailingSpaces(line);
            }
            if (key.equals("wait")) {
               wait = Integer.parseInt(removeLeadingTrailingSpaces(line));
            }
            if (key.equals("timeout_http")) {
               timeout_http = Integer.parseInt(removeLeadingTrailingSpaces(line));
            }
            if (key.equals("timeout_ffmpeg")) {
               timeout_ffmpeg = Integer.parseInt(removeLeadingTrailingSpaces(line));
            }
            if (key.equals("trackingFile")) {
               trackingFile = removeLeadingTrailingSpaces(line);
            }
            if (key.equals("tivos")) {
               addTivo(removeLeadingTrailingSpaces(line));
            }
            if (key.equals("watchList")) {
               String[] l = line.split(",");
               if (l.length > 1) {
                  Hashtable<String,String> h = new Hashtable<String,String>();
                  h.put("share", removeLeadingTrailingSpaces(l[0]));
                  h.put("path", removeLeadingTrailingSpaces(l[1]));
                  if (l.length > 2) {
                     h.put("tivo", removeLeadingTrailingSpaces(l[2]));
                  }
                  watchList.add(h);
               }
            }
         }
         ini.close();
         
         // Check against shares in pyTivoConf if available
         pyTivoConfUpdate();
      }
      catch (Exception ex) {
         log.error("Problem parsing config file: " + configIni);
         log.error(ex.toString());
         return false;
      }
      return true;
   }
  
   // Read pyTivoConf and update watchList with any new/changed shares
   public static void pyTivoConfUpdate() {      
      // Check against shares in pyTivoConf if available
      if (file.isFile(pyTivoConf)) {
         Stack<Hashtable<String,String>> shares = getPyTivoShares(pyTivoConf);
         if (shares != null) {
            for (int i=0; i<shares.size(); ++i) {
               if (! containsShare(shares.get(i), watchList, true)) {
                  // Share not in watchList so add it now
                  watchList.add(shares.get(i));
               }
            }
            
            // Remove obsolete shares in watchList
            for (int i=0; i<watchList.size(); ++i) {
               if (! containsShare(watchList.get(i), shares, false)) {
                  watchList.remove(i);
               }
            }
         }
      } else {
         log.error("File does not exist: " + pyTivoConf);
         watchList.clear();
      }
      // Update tracking hash table
      parseTrackingFiles();
   }
   
   private static Boolean containsShare(
      Hashtable<String,String> h,
      Stack<Hashtable<String,String>> shares,
      Boolean update) {
      if (watchList != null) {
         for (int i=0; i<shares.size(); ++i) {
            if (h.get("share").equals(shares.get(i).get("share"))) {
               // Update path in case different
               if (update)
                  shares.get(i).put("path", h.get("path"));                  
               return true;
            }
         }
      }
      return false;
   }
   
   public static int numActiveShares() {
      int num = 0;
      for (int i=0; i<watchList.size(); ++i) {
         if (watchList.get(i).containsKey("tivo")) {
            num++;
         }
      }      
      return num;
   }
   
   // Parse all active share tracking files and store info in "pushed" hash
   private static void parseTrackingFiles() {
      String fullTrackingFile;
      String watchDir;
      pushed.clear();
      for (int i=0; i<watchList.size(); ++i) {
         if (watchList.get(i).containsKey("tivo")) {
            watchDir = watchList.get(i).get("path");
            fullTrackingFile = watchDir + File.separator + trackingFile;
            try {
               BufferedReader ifp = new BufferedReader(new FileReader(fullTrackingFile));
               String line = null;
               while (( line = ifp.readLine()) != null) {
                  // Get rid of leading and trailing white space
                  line = line.replaceFirst("^\\s*(.*$)", "$1");
                  line = line.replaceFirst("^(.*)\\s*$", "$1");
                  if (line.length() == 0) continue; // skip empty lines
                  if (line.matches("^#.+")) continue; // skip comment lines
                  String[] l = line.split("\\s+");
                  String entry;
                  if (l.length > 1) {
                     entry = watchDir + File.separator + l[0];
                     pushed.put(entry, l[1]);
                  }
                  else if (l.length > 0) {
                     entry = watchDir + File.separator + l[0];
                     pushed.put(entry, "pushed");
                  }
               }
               ifp.close();
            }
            catch (IOException ex) {
               log.print("NOTE: tracking file not found: " + fullTrackingFile);
            }
         }
      }
   }
   
   public static Boolean appendTrackingFile(String share, String path, String pushFile) {
      String watchDir = getWatchDir(share);
      if (watchDir == null) {
         log.error("Can't determine path to share: " + share);
         return false;
      }
      String fullTrackingFile = watchDir + File.separator + trackingFile;
      String entry = buildRelativeEntry(path, pushFile);
      String eol = "\r";
      try {
         BufferedWriter ofp = new BufferedWriter(new FileWriter(fullTrackingFile, true));
         ofp.write(entry + eol);
         ofp.close();
         pushed.put(watchDir + File.separator + entry, "pushed");
      }
      catch (Exception ex) {
         log.error("Problem writing to file: " + fullTrackingFile);
         log.error(ex.toString());
         return false;
      }

      return true;
   }
   
   public static Boolean alreadyPushed(String watchDir, String path, String pushFile) {
      String entry = buildRelativeEntry(path, pushFile);
      String track = watchDir + File.separator + entry;
      if (pushed.containsKey(track)) {
         return true;
      } else {
         return false;
      }
   }
   
   private static String getWatchDir(String share) {
      for (int i=0; i<watchList.size(); ++i) {
         if (watchList.get(i).get("share").equals(share)) {
            return watchList.get(i).get("path");
         }
      }
      return null;
   }
   
   private static String buildRelativeEntry(String path, String pushFile) {
      String entry;
      if (OS.equals("windows")) {
         path = path.replaceAll("/", "\\\\");
      }
      if (path.length() > 0) {
         entry = path + File.separator + pushFile;
      } else {
         entry = pushFile;
      }
      return entry;
   }
      
}
