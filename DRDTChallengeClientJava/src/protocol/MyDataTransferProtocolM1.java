package protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.*;

public class MyDataTransferProtocolM1 extends IRDTProtocol {

    // change the following as you wish:
    static final int HEADERSIZE = 1;   // number of header bytes in each packet
    static final int DATASIZE = 32;   // max. number of user data bytes in each packet
    
    // sliding window variables and constants
    static final int SendWindowSize = 4; //  packets send window
    private int lastPacketSend = -1;
    private List<Integer> sendList = new ArrayList<Integer>();
    
    private int timeOutCount = 0;
    private int seqNumber = 0;
    private int lastHeader = -1;

    @Override
    public void sender() {
        System.out.println("Sending...");

        // read from the input file
        Integer[] fileContents = Utils.getFileContents(getFileID());

        // keep track of where we are in the data
        int filePointer = 0;
        for (int i = 0; i < SendWindowSize && (i*DATASIZE) < fileContents.length;i++) {
          sendList.add(i*DATASIZE);
        }
        
        while (filePointer < fileContents.length && !sendList.isEmpty()) {
          
          for (int i =0; i<sendList.size() && filePointer < fileContents.length;i++) {
            filePointer = sendList.get(i);
            
            // create a new packet of appropriate size
            int datalen = Math.min(DATASIZE, fileContents.length - filePointer);
            Integer[] pkt = new Integer[HEADERSIZE + datalen];
            
            // write count into the header byte
            pkt[0] = seqNumber; 
            
            // copy databytes from the input file into data part of the packet, i.e., after the header
            System.arraycopy(fileContents, filePointer, pkt, HEADERSIZE, datalen);
            
            // send the packet to the network layer
            getNetworkLayer().sendPacket(pkt);
            System.out.println("Sent one packet with header="+pkt[0]);
            lastPacketSend++;
            
          }

          // schedule a timer for 1000 ms into the future, just to show how that works:
          //client.Utils.Timeout.SetTimeout(1000, this, seqNumber);
          
          
          
          //and loop and sleep; you may use this loop to check for incoming acks...
          boolean stop = false;
          while (!stop) {
              try {
                  Thread.sleep(100);
                  Integer[] ackPacket = getNetworkLayer().receivePacket();
                  stop = true;
                  if (ackPacket != null && sendList.contains(ackPacket[0]*DATASIZE)) {
                    boolean remove = sendList.remove(new Integer(ackPacket[0]*DATASIZE));
                    
                    sendList.add((lastPacketSend+1)*DATASIZE);
                    System.out.println("Acknowlegde received: " + ackPacket[0]);
                    
                    // dit moet ergens anders
                    seqNumber++;
                  } 
              } catch (InterruptedException e) {
                  stop = true;
              }
          }
        }
        System.out.println("Total file send. Number of packets send: " +(seqNumber+1));
    }

    @Override
    public void TimeoutElapsed(Object tag) {
        int z=(Integer)tag;
        // handle expiration of the timeout:
        System.out.println("Timer expired with tag="+z);
    }

    @Override
    public void receiver() {
        System.out.println("Receiving...");

        // create the array that will contain the file contents
        // note: we don't know yet how large the file will be, so the easiest (but not most efficient)
        //   is to reallocate the array every time we find out there's more data
        Integer[] fileContents = new Integer[0];

        // loop until we are done receiving the file
        boolean stop = false;
        while (!stop) {

            // try to receive a packet from the network layer
            Integer[] packet = getNetworkLayer().receivePacket();

            // if we indeed received a packet
            if (packet != null) {
              timeOutCount = 0;
              
              
              // tell the user
              System.out.println("Received packet, length = " + packet.length + " first byte=" + packet[0]);
              
              // tell the server
              Integer [] ackPacket = {packet[0]};
              getNetworkLayer().sendPacket(ackPacket);
              System.out.println("Ack send");
              
              if(lastHeader != packet[0]) {
             // append the packet's data part (excluding the header) to the fileContents array, first making it larger
                int oldlength=fileContents.length;
                int datalen= packet.length - HEADERSIZE;
                fileContents = Arrays.copyOf(fileContents, oldlength+datalen);
                System.arraycopy(packet, HEADERSIZE, fileContents, oldlength, datalen);
                lastHeader = packet[0];
              }

            } else {
                // wait ~10ms (or however long the OS makes us wait) before trying again
                try {
                    Thread.sleep(10);
                    timeOutCount ++;
                } catch (InterruptedException e) {
                    stop = true;
                }
            }
            
            if (timeOutCount == 1000) {
              // let's just hope the file is now complete after 1000*10ms
              stop = true;
            } 
        }
        // write to the output file
        Utils.setFileContents(fileContents, getFileID());
    }
}
