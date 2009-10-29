package net.pyTivo.auto_push.main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class mdns {
   private JmDNS jmdns = null;
   
   public mdns() {
      try {
         jmdns = JmDNS.create(InetAddress.getLocalHost());
      } catch (UnknownHostException e) {
         log.error("mdns error: " + e.getMessage());
      } catch (IOException e) {
         log.error(e.getMessage());
      }
   }
   
   public void close() {
      if (jmdns != null) jmdns.close();
      jmdns = null;
   }
   
   // Detect tivos on the network
   // If new TiVo detected return true
   public Boolean getTivos() {      
      if (jmdns == null) return false;
      ServiceInfo info[] = jmdns.list("_http._tcp.local.");
      if (info.length > 0) {
         Stack<String> tivoNames = config.getTivoNames();
         // Step through list of found host names
         for (int i=0; i<info.length; ++i) {
            // No tsn => not a tivo
            String tsn = info[i].getPropertyString("TSN");
            if (tsn != null) {
               Boolean add = true;
               String name = info[i].getName();
               if (name != null) {
                  // Check against current tivo list
                  for (int j=0; j<tivoNames.size(); ++j) {
                     if ( tivoNames.get(j).equals(name) ) {
                        add = false;
                     }
                  }
               } else {
                  add = false;
               }
               if (add) {
                  // This tivo not part of current list so add it
                  config.addTivo(name);
                  return true;
               }
            }
         }
      }
      return false;
   }
}
