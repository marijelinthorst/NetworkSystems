package ns.tcphack;

import java.math.BigInteger;
import java.util.Arrays;

public class IntToByteAndBackTest {

  public static void main(String[] args) {
    // van int naar byte array en terug
    int number  = 37429;
    byte[] byteArray = BigInteger.valueOf(number).toByteArray();
    System.out.println(Arrays.toString(byteArray));
    
    int backInt = new BigInteger(byteArray).intValue();
    System.out.println(backInt);
    
    // werkt niet omdat aantal bits dat erachter wordt geplakt niet varierend
    // zijn en dat wil je wel (je wil geen nullen die vooraan staan shiften)
    int[] txpkt = new int[3];
    txpkt[0] = 31;
    txpkt[1] = 144;
    
    int bytes = txpkt[0] << 8;
    bytes = bytes + txpkt[1];
    System.out.println(bytes);
    
    txpkt[2] = 3;    
    bytes = 0;
    for (int h:txpkt) {
      bytes = bytes << 8;
      bytes = bytes + h;
    }
    System.out.println(bytes); 
  }
}
