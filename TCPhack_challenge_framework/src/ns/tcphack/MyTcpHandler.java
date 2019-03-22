package ns.tcphack;

import java.net.InetAddress;
import java.net.UnknownHostException;

class MyTcpHandler extends TcpHandler {
	public static void main(String[] args) throws UnknownHostException {
		new MyTcpHandler();
	}

	public MyTcpHandler() throws UnknownHostException {
		super();

		boolean done = false;
		
		// inet6 2001:67c:2564:a311:8868:5b05:d4d:cb1a prefixlen 64 autoconf temporary 
		// gerjan ding test
        InetAddress ipsource = InetAddress.getByName("2001:67c:2564:a183::1");
             //fe80::3a:13ee:2e6f:49ac");
        byte[] ipBytessource = ipsource.getAddress();
        System.out.println(ipBytessource);
        int[] ipIntsSource = new int[ipBytessource.length];
        for(int i = 0; i < ipBytessource.length; i++) {
          ipIntsSource[i] = ipBytessource[i];
        }
        
        InetAddress ipdes = InetAddress.getByName("2001:67c:2564:a170:204:23ff:fede:4b2c"); 
        byte[] ipBytesDes = ipdes.getAddress();
        System.out.println(ipBytesDes);
        int[] ipIntsDes = new int[ipBytesDes.length];
        for(int i = 0; i < ipBytesDes.length; i++) {
          ipIntsDes[i] = ipBytesDes[i];
        }
        
        
		// array of bytes in which we're going to build our packet:
		int[] txpkt = new int[60];		// 40 bytes long for now, may need to expand this later

		/** IPV6 HEADER*/
		txpkt[0] = 0x60;	// first byte of the IPv6 header contains version number in upper nibble
		txpkt[1] = 0;
		txpkt[2] = 0;
		txpkt[3] = 0;
		
		//payload length
		txpkt[4] = 0;
		txpkt[5] = 20;		
		// next header
		txpkt[6] = 253;
		// hop limit
		txpkt[7] = 64;
		
		
		// begin source address
		txpkt[8] = ipBytessource[0];
		txpkt[9] = ipBytessource[1];		
		txpkt[10] = ipBytessource[2];
        txpkt[11] = ipBytessource[3];
        
        txpkt[12] = ipBytessource[4];
        txpkt[13] = ipBytessource[5];
        txpkt[14] = ipBytessource[6];
        txpkt[15] = ipBytessource[7];  
        
        txpkt[16] = ipBytessource[8];   
        txpkt[17] = ipBytessource[9];
        txpkt[18] = ipBytessource[10];
        txpkt[19] = ipBytessource[11];
        
        txpkt[20] = ipBytessource[12];
        txpkt[21] = ipBytessource[13];
        txpkt[22] = ipBytessource[14];
        txpkt[23] = ipBytessource[15];
        
        
        // begin des address
        // 2001:67c:2564:a170:204:23ff:fede:4b2c   7710/s1234567
        txpkt[24] = ipBytesDes[0];
        txpkt[25] = ipBytesDes[1];       
        txpkt[26] = ipBytesDes[2];   
        txpkt[27] = ipBytesDes[3];
        
        txpkt[28] = ipBytesDes[4];
        txpkt[29] = ipBytesDes[5];       
        txpkt[30] = ipBytesDes[6];
        txpkt[31] = ipBytesDes[7];
        
        txpkt[32] = ipBytesDes[8];
        txpkt[33] = ipBytesDes[9];
        txpkt[34] = ipBytesDes[10];
        txpkt[35] = ipBytesDes[11];  
        
        txpkt[36] = ipBytesDes[12];   
        txpkt[37] = ipBytesDes[13];
        txpkt[38] = ipBytesDes[14];
        txpkt[39] = ipBytesDes[15];
        

        /** OWN TCP HEADER*/
        // source port
        txpkt[40] = 31;
        txpkt[41] = 144;
        // des port
        txpkt[42] = 30;
        txpkt[43] = 30;
        
        //seq number
        txpkt[44] = 0;
        txpkt[45] = 0;
        txpkt[46] = 0;
        txpkt[47] = 1;
        
        // ack number
        txpkt[48] = 0;
        txpkt[49] = 0;
        txpkt[50] = 0;
        txpkt[51] = 0;
        
        // rest
        txpkt[52] = 80;
        txpkt[53] = 2;
        // window size
        txpkt[54] = 0;
        txpkt[55] = 10;
        
        // checksum
        txpkt[56] = 0;
        txpkt[57] = 0;
        // urgent pointer
        txpkt[58] = 0;
        txpkt[59] = 0;
        
        // options
       // txpkt[60] = 0;
       // txpkt[61] = 0;
       // txpkt[62] = 0;
       // txpkt[63] = 0;
        
        
        
		this.sendData(txpkt);	// send the packet

		while (!done) {
			// check for reception of a packet, but wait at most 500 ms:
			int[] rxpkt = this.receiveData(500);
			if (rxpkt.length==0) {
				// nothing has been received yet
				System.out.println("Nothing...");
				continue;
			}

			// something has been received
			int len=rxpkt.length;

			// print the received bytes:
			int i;
			System.out.print("Received "+len+" bytes: ");
			for (i=0;i<len;i++) System.out.print(rxpkt[i]+" ");
			System.out.println("");
		}   
	}
}
