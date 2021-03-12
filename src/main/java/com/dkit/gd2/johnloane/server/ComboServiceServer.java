package com.dkit.gd2.johnloane.server;

import com.dkit.gd2.johnloane.core.ComboServiceDetails;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

/**
 * Server for our UDP combo service
 * Constantly listening and responding to requests
 */
public class ComboServiceServer
{
    public static void main( String[] args )
    {
        int countRequests = 0;
        boolean continueRunning = true;

        System.out.println( "This is the Server");

        DatagramSocket serverSocket = null;

        try
        {
            //Create the socket to send and receive
            serverSocket = new DatagramSocket(ComboServiceDetails.serverPort);

            System.out.println("Listening on port: " + ComboServiceDetails.serverPort);

            //The server should run forever or at least until the owner
            //of the server decides to shutdown - don't give users this options
            while(continueRunning)
            {
                byte[] incomingMessage = new byte[ComboServiceDetails.MAX_LEN];
                DatagramPacket incomingPacket = new DatagramPacket(incomingMessage, incomingMessage.length);

                //Wait for a message
                //This is blocking

                serverSocket.receive(incomingPacket);
                System.out.println("Listening on port: " + ComboServiceDetails.serverPort);

                String data = new String(incomingPacket.getData());
                System.out.println(data);

                data = data.trim();

                String[] messageComponents = data.split(ComboServiceDetails.breakCharacters);
                countRequests++;

                String response = null;

                if(messageComponents[0].equalsIgnoreCase("echo"))
                {
                    //echo&&Ah do ->Ah do
                    response = data.replace("echo"+ComboServiceDetails.breakCharacters, "");
                }
                else if(messageComponents[0].equalsIgnoreCase("daytime"))
                {
                    response = new Date().toString();
                }
                else if(messageComponents[0].equalsIgnoreCase("stats"))
                {
                    response = "Number of requests dealt with by the server is: " + countRequests;
                }
                else
                {
                    response = "Unrecognised request";
                }

                //Get the address of the sender
                InetAddress clientAddress = incomingPacket.getAddress();

                byte[] responseBuffer = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, ComboServiceDetails.clientPort);

                serverSocket.send(responsePacket);


            }

        } catch (SocketException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }


    }
}
