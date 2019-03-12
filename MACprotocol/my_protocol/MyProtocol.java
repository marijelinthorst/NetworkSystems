package my_protocol;

import framework.IMACProtocol;
import framework.MediumState;
import framework.TransmissionInfo;
import framework.TransmissionType;

import java.util.LinkedList;
import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 *
 * @author Jaco ter Braak, University of Twente
 * @version 05-12-2013
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

public class MyProtocol implements IMACProtocol {
  private int id = 3;
  private LinkedList<Integer> nodesWithData = new LinkedList<Integer>();
  private int currentNode = 0;
  static final int[] POWERS_OF_10 = {1000, 100, 10, 1};//{1, 10, 100, 1000, 10000};
  private int almostEmptyCount = 0;
  private int twoEmptyCount = 0;

    @Override
    public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
                                              int controlInformation, int localQueueLength) {
      
      System.out.println("-----------------------------");
      System.out.println("controlInformation: " + controlInformation);
      System.out.println("previousMediumState: " + previousMediumState);
      System.out.println("localQueueLength: " + localQueueLength);
      System.out.println("currentNode: " + currentNode);
      
      
      this.determineNodesWithData(controlInformation);
      
      if (currentNode == id) {
        if (localQueueLength == 0) {
          System.out.println("SLOT - No data to send. Replying no data type.");
          return new TransmissionInfo(TransmissionType.NoData, determineReturnNodes(true));
        } else {
          System.out.println("SLOT - Sending data and hope for no collision.");
          return new TransmissionInfo(TransmissionType.Data, determineReturnNodes(false));
        }
      } else {
        System.out.println("SLOT - Not the current node. Not replying.");
        return new TransmissionInfo(TransmissionType.Silent, 0);
      }
      

      /**
        // No data to send, just be quiet
        if (localQueueLength == 0) {
            System.out.println("SLOT - No data to send.");
            if (controlInformation % 4 == id) {
              controlInformation++;
              return new TransmissionInfo(TransmissionType.NoData, controlInformation);
            } else {
              return new TransmissionInfo(TransmissionType.Silent, 0);
            }
        }

        // Randomly transmit with 60% probability
        //if (new Random().nextInt(100) < 25) {
        if (controlInformation % 4 == id) {
            System.out.println("SLOT - Sending data and hope for no collision.");
            controlInformation++;
            return new TransmissionInfo(TransmissionType.Data, controlInformation);
        } else {
            System.out.println("SLOT - Not sending data to give room for others.");
            return new TransmissionInfo(TransmissionType.Silent, 0);
        }
      */
    }
    
    private void determineNodesWithData (int controlInformation) {
      int x = controlInformation;
      if (controlInformation == 0) {
        currentNode = 0;
      } else {
        currentNode = controlInformation/10000 - 1;
        x = x - (currentNode+1)*10000;
      }
      
      int firstNode = x/1000;
      x = x - firstNode*1000;
      int secondNode = x/100;
      x = x - secondNode*100;
      int thirdNode = x/10;
      int fourthNode = x%10;
        
      nodesWithData.clear();
      if (!nodesWithData.contains(firstNode-1) && firstNode - 1 != -1) {
        nodesWithData.add(firstNode-1);
      }
      if (!nodesWithData.contains(secondNode-1) && secondNode - 1 != -1) {
        nodesWithData.add(secondNode-1);
      }
      if (!nodesWithData.contains(thirdNode-1) && thirdNode - 1 != -1) {
        nodesWithData.add(thirdNode-1);
      }
      if (!nodesWithData.contains(fourthNode-1) && fourthNode - 1 != -1) {
        nodesWithData.add(fourthNode-1);
      }
    }
    
    
    private int determineReturnNodes (boolean noData) {
      if (noData && nodesWithData.contains(Integer.valueOf(id))) {
        nodesWithData.remove(Integer.valueOf(id));
      } else {
        nodesWithData.remove(Integer.valueOf(id));
        nodesWithData.addLast(id);
      }
      
      if (nodesWithData.size() == 1) {
        almostEmptyCount++;
      } else {
        almostEmptyCount = 0;
      }
      
      if (almostEmptyCount == 2) {
        nodesWithData.clear();
      }
      
      if (nodesWithData.size() == 2) {
        twoEmptyCount++;
      } else {
        twoEmptyCount = 0;
      }
      
      if (twoEmptyCount == 3) {
        nodesWithData.clear();
      }
      
      int controlInformation = 0;
      if (nodesWithData.isEmpty()) {
        nodesWithData.clear();
        nodesWithData.add(0);
        nodesWithData.add(1);
        nodesWithData.add(2);
        nodesWithData.add(3);
        controlInformation = (nodesWithData.get(Integer.valueOf((id+1))%4))*10000;
      } else {
        controlInformation = (nodesWithData.get(0)+1)*10000;
      }
      
      
        
      for (int i = 0 ; i<nodesWithData.size(); i++) {
        controlInformation = controlInformation + (nodesWithData.get(i)+1)* POWERS_OF_10[i];
      }
      return controlInformation;
    }

}
