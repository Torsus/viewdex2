/* @(#) UDPClient.java 05/12/2003
 *
 * Copyright (c) 2010 Sahlgrenska University Hospital.
 * All Rights Reserved.
 *
 */

/*
 * @author Sune Svensson.
 */
package mft.vdex.util;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {

    String hostName;
    int port;
    String msg;
    DatagramSocket socket;

    public void send() {

        try {
            socket = new DatagramSocket();

            // send request
            byte[] buf = new byte[256];
            String msgTest = "ET_XXX" + '\n';
            String msg2 = msg + '\n';
            buf = msg2.getBytes();
            InetAddress address = InetAddress.getByName(hostName);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            
            // get response
            /*
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            // display response
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Quote of the Moment: " + received);
            */
        } catch (SocketException e) {
            System.out.println("Socket: + e.getMessage()");
        } catch (IOException e) {
            System.out.println("Socket: + e.getMessage()");
        }
    }
    
    public void setHostName(String host){
        hostName = host;
    }
    
    public void setPortName(String str){
        port = getPortValue(str);
    }
    
    public void setMessage(String str){
        msg = str;
    }
    
    public int getPortValue(String str) {
        String a = null;
        //int b = 0x8FFFFFFF;
        int b = Integer.MIN_VALUE;

        if (str != null) {
            try {
                a = str.trim();
                b = Integer.parseInt(a);
            } catch (NumberFormatException e) {
                System.out.println("UDPClient:getPortValue: NumberFormatException");
            }
        }
        return b;
    }
}
