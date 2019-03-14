package my_protocol;

import framework.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 12-03-2019
 *
 * Copyright University of Twente, 2013-2019
 *
 **************************************************************************
 *                            Copyright notice                            *
 *                                                                        *
 *             This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
public class MyRoutingProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;

    // You can use this data structure to store your routing table.
    private HashMap<Integer, MyRoute> myRoutingTable = new HashMap<>();
    private DataTable sendDT = new DataTable(7);
    private List<Integer> neighbours = new ArrayList<Integer>();
    private int infinite = 10000;

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;
    }


    @Override
    public void tick(PacketWithLinkCost[] packetsWithLinkCosts) {
        // Get the address of this node
        int myAddress = this.linkLayer.getOwnAddress();

        System.out.println("tick; received " + packetsWithLinkCosts.length + " packets");

        if (packetsWithLinkCosts.length == 0) {
          System.out.println("geen packets, broadcast");
          Packet pkt = new Packet(myAddress, 0, sendDT);
          this.linkLayer.transmit(pkt);
        } else if (neighbours.size() > packetsWithLinkCosts.length) {
          for (PacketWithLinkCost packetCost : packetsWithLinkCosts) {
            int neighbour = packetCost.getPacket().getSourceAddress();
            if (!neighbours.contains(neighbour)) {
              myRoutingTable.get(neighbour).cost=infinite;
            }
          }
        }

        // first process the incoming packets; loop over them:
        for (int i = 0; i < packetsWithLinkCosts.length; i++) {
            Packet packet = packetsWithLinkCosts[i].getPacket();
            int neighbour = packet.getSourceAddress();             // from whom is the packet?
            int linkcost = packetsWithLinkCosts[i].getLinkCost();  // what's the link cost from/to this neighbour?
            DataTable receivedDT = packet.getDataTable();                  // other data contained in the packet
            System.out.printf("received packet from %d with %d rows and %d columns of data%n", neighbour, receivedDT.getNRows(), receivedDT.getNColumns());
 
            // you'll probably want to process the data, update your data structures (myRoutingTable) , etc....

            // reading one cell from the DataTable can be done using the  dt.get(row,column)  method

           //inserting a neighbour route into myRoutingTable:
            if (myRoutingTable.containsKey(neighbour)) {
               if (myRoutingTable.get(neighbour).cost > linkcost) {
                 myRoutingTable.remove(neighbour);
                 MyRoute r = new MyRoute();
                 r.nextHop = neighbour;
                 r.cost = linkcost;
                 myRoutingTable.put(neighbour, r);
                 neighbours.add(neighbour);
                 System.out.println("replace neighbour " + neighbour);
               }
            } else {
              MyRoute r = new MyRoute();
              r.nextHop = neighbour;
              r.cost = linkcost;
              myRoutingTable.put(neighbour, r);
              System.out.println("add neighbour " + neighbour);
            }
            
            
            // update Routing Table
            if (neighbour<receivedDT.getNRows()) {
              for (int g = 1; g<receivedDT.getNColumns();g++) {
                int costs = receivedDT.get(neighbour, g);
                if (myRoutingTable.containsKey(g)) {
                  if (myRoutingTable.get(g).cost > costs + linkcost) {
                    myRoutingTable.get(g).cost = costs + linkcost;
                    myRoutingTable.get(g).nextHop = neighbour;
                  }
                } else {
                  MyRoute r = new MyRoute();
                  r.nextHop = neighbour;
                  r.cost = linkcost + costs;
                  myRoutingTable.put(g, r);
                  System.out.println("add neighbour " + neighbour);
                }
              }
            }
            
           if (myRoutingTable.containsKey(myAddress)) {
             myRoutingTable.remove(myAddress);
           }
            
            // make new DataTable or update received one, apparently creates dead code, so:
            if (receivedDT.getNRows()==0) {
              System.out.println("receivedDT empty");
              Integer[] leeg = {infinite,infinite,infinite,infinite,infinite,infinite,infinite};
              while (sendDT.getNRows()!=7) {
                sendDT.addRow(leeg);
              }
            } else {
              sendDT = receivedDT;
              System.out.println("replace sendDT to receivedDT");
            }
            // the only thing that this node can change, is its own costs to other nodes, so:
            for (Integer key : myRoutingTable.keySet()) {          
              sendDT.set(myAddress, key, myRoutingTable.get(key).cost);
              System.out.println("Change DT");
            }
           // send data:
     //        if (myRoutingTable.containsKey(packet.getDestinationAddress())) {
     //          MyRoute route = myRoutingTable.get(packet.getDestinationAddress());
     //          Packet pkt = new Packet(myAddress, route.nextHop, sendDT);
     //          this.linkLayer.transmit(pkt);
     //          System.out.println("send to neighbour");
     //        } 
        }
        Packet pkt = new Packet(myAddress, 0, sendDT);
        this.linkLayer.transmit(pkt);
        System.out.println("broadcast from node: " + myAddress);
    }

    public Map<Integer, Integer> getForwardingTable() {
        // This code extracts from your routing table the forwarding table.
        // The result of this method is send to the server to validate and score your protocol.

        // <Destination, NextHop>
        HashMap<Integer, Integer> ft = new HashMap<>();

        for (Map.Entry<Integer, MyRoute> entry : myRoutingTable.entrySet()) {
            ft.put(entry.getKey(), entry.getValue().nextHop);
        }

        return ft;
    }
}
