package lpm;

import java.util.*;

public class LongestPrefixMatcher {
  private List<Integer> IPList;
  private List<Byte> prefixList;
  private List<Integer> portList;
  private List<Byte> tempPrefixList;
  private List <Integer> tempPortList;
  private int i = 0;
  /**
   * You can use this function to initialize variables.
   */
    public LongestPrefixMatcher() {
        IPList = new ArrayList <Integer>();
        prefixList = new ArrayList <Byte>();
        portList = new ArrayList <Integer>();
        tempPrefixList = new ArrayList <Byte>();
        tempPortList = new ArrayList <Integer>();
    }
    
    /**
     * Looks up an IP address in the routing tables
     * @param ip The IP address to be looked up in integer representation
     * @return The port number this IP maps to
     */
    public int lookup(int ip) {
      tempPrefixList.clear();
      tempPortList.clear();
      
      // -------------------------------------------------
      System.out.println("-----------------");
     // System.out.println(prefixList);
      // -------------------------------------------------
      
      for (int i = 0; i<IPList.size();i++) {
        int currentIP = IPList.get(i);
        byte currentPrefix = prefixList.get(i);
        
        //int shiftIP = currentIP >> 32 - currentPrefix;
        int compareIP = ip >> 32 - currentPrefix;
        
        if (compareIP == currentIP) {
          tempPrefixList.add(currentPrefix);
          tempPortList.add(portList.get(i));
        }
      }
      
      System.out.println(tempPortList);
      System.out.println(tempPrefixList);
      
      if (tempPrefixList.isEmpty()) {
        return -1;
      } else {
        byte largest = 0;
        for (int i = 0; i<tempPrefixList.size();i++) {
          byte temp = tempPrefixList.get(i);
          if (temp > largest) {
            largest = temp;
          }
        }
        return tempPortList.get(tempPrefixList.indexOf(largest));
      }
    }

    /**
     * Adds a route to the routing tables
     * @param ip The IP the block starts at in integer representation
     * @param prefixLength The number of bits indicating the network part
     *                     of the address range (notation ip/prefixLength)
     * @param portNumber The port number the IP block should route to
     */
    public void addRoute(int ip, byte prefixLength, int portNumber) {
      int shiftIP = ip >> 32 - prefixLength;
      // if (prefixList.isEmpty()) {
          IPList.add(shiftIP);
          prefixList.add(prefixLength);
          portList.add(portNumber);
          /**
          System.out.println("1");
          i++;
        } else {
          for (int i = 0; i<prefixList.size();i++) {
            byte listTemp = prefixList.get(i);
            if (listTemp < prefixLength) {
              prefixList.add(i, prefixLength);
              IPList.add(i, ip);
              portList.add(i, portNumber);
              i = prefixList.size();
              this.i++;
            } else if (i == prefixList.size()-1) {
              IPList.add(ip);
              prefixList.add(prefixLength);
              portList.add(portNumber);
              i = prefixList.size();
              this.i++;
            }
          }
          System.out.println(i+1);
        }
        */
    }

    /**
     * Converts an integer representation IP to the human readable form
     * @param ip The IP address to convert
     * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
     */
    private String ipToHuman(int ip) {
        return Integer.toString(ip >> 24 & 0xff) + "." +
                Integer.toString(ip >> 16 & 0xff) + "." +
                Integer.toString(ip >> 8 & 0xff) + "." +
                Integer.toString(ip & 0xff);
    }

    /**
     * Parses an IP
     * @param ipString The IP address to convert
     * @return The integer representation for the IP
     */
    private int parseIP(String ipString) {
        String[] ipParts = ipString.split("\\.");

        int ip = 0;
        for (int i = 0; i < 4; i++) {
            ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
        }

        return ip;
    }
}
