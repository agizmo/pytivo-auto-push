package net.pyTivo.auto_push.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class file {
   public static Boolean isFile(String f) {
      try {
         return new File(f).isFile();
      }
      catch (NullPointerException e) {
         return false;
      }
   }
   
   public static Boolean isDir(String d) {
      try {
         return new File(d).isDirectory();
      }
      catch (NullPointerException e) {
         return false;
      }
   }
   
   // Create a new empty file
   public static Boolean create(String fileName) {
      try {
         File f = new File(fileName);
         return f.createNewFile();
      } catch (IOException e) {
         log.error(e.getMessage());
         return false;
      }
   }
   
   public static String basename(String name) {
      String s = File.separator;
      if (s.equals("\\")) s = "\\\\";
      String[] l = name.split(s);
      return l[l.length-1];
   }
   
   public static String dirname(String name) {
      String s = File.separator;
      if (s.equals("\\")) s = "\\\\";
      String[] l = name.split(s);
      if (l.length > 1) {
         String dir = "";
         for (int i=0; i<l.length-1; i++) {
            if (i>0)
               dir += File.separator + l[i];
            else
               dir += l[i];
         }
         return dir;
      } else {
         return "";
      }            
   }
   
   static public List<String> getAllFiles(String dir) {
      File startDir = new File(dir);
      if ( startDir.isDirectory()) {
         List<File> result = getFileListingNoSort(startDir);
         Collections.sort(result);
         List<String> files = new ArrayList<String>();
         for (int i=0; i<result.size(); ++i) {
            if (file.isFile(result.get(i).getPath()))
               files.add(result.get(i).getPath());
         }
         return files;
      }
      return null;
   }

   static private List<File> getFileListingNoSort(File aStartingDir) {
      List<File> result = new ArrayList<File>();
      File[] filesAndDirs = aStartingDir.listFiles();
      List<File> filesDirs = Arrays.asList(filesAndDirs);
      for(File file : filesDirs) {
        result.add(file); //always add, even if directory
        if ( file.isDirectory() ) {
          //must be a directory
          //recursive call!
          List<File> deeperList = getFileListingNoSort(file);
          result.addAll(deeperList);
        }
      }
      return result;
   }

}
