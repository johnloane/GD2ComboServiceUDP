package com.dkit.gd2.johnloane.client;

import com.dkit.gd2.johnloane.core.ComboServiceDetails;

import java.io.IOException;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ComboServiceClient
{
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        DatagramSocket clientSocket = null;

        try
        {
            InetAddress serverHost = InetAddress.getByName(ComboServiceDetails.serverHost);
            clientSocket = new DatagramSocket(ComboServiceDetails.clientPort);

            boolean continueRunning = true;
            while(continueRunning)
            {
                displayMenu();

                int choice = getChoice(keyboard);
                String message = null;
                boolean sendMessage = true;

                switch(choice)
                {
                    case 0:
                        continueRunning = false;
                        sendMessage = false;
                        break;
                    case 1:
                        //@TODO validate the input here
                        System.out.print("Please enter message to echo: ");
                        System.out.println();
                        //echo&&"Please turn on your cameras"
                        message = "echo" + ComboServiceDetails.breakCharacters+ keyboard.nextLine();
                        System.out.println(message);
                        break;
                    case 2:
                        message = "daytime";
                        break;
                    case 3:
                        message = "stats";
                        break;
                    default:
                        System.out.println("Bad request. Please choose from the menu");
                        sendMessage = false;
                }
                if(sendMessage)
                {
                    //Building the message
                    byte buffer[] = message.getBytes();

                    DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length, serverHost, ComboServiceDetails.serverPort);

                    //Sending the message

                    clientSocket.send(requestPacket);
                    System.out.println("Message sent");

                    //Wait to receive a response
                    byte[] responseBuffer = new byte[ComboServiceDetails.MAX_LEN];
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

                    clientSocket.receive(responsePacket);
                    String data = new String(responsePacket.getData());
                    System.out.println("Response: " + data.trim() + ".");
                }
            }
        } catch (UnknownHostException e)
        {
            System.out.println("Problem getting server address " + e.getMessage());
        }
        catch(SocketException e)
        {
            System.out.println("Problem binding clientSocket to port " + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            if(clientSocket != null)
            {
                clientSocket.close();
            }
        }
    }

    //@TODO refactor to catch inputs that are ints but not in the range 0-3
    private static int getChoice(Scanner keyboard)
    {
        int choice = -1;
        boolean validNumber = false;
        while(!validNumber)
        {
            try
            {
                choice = keyboard.nextInt();
                validNumber = true;
                keyboard.nextLine();
            }
            catch(InputMismatchException e)
            {
                System.out.println("Please enter a number from the menu");
                System.out.println();
                displayMenu();
                keyboard.nextLine();
            }
        }
        return choice;
    }


    //@TODO refactor to use enums
    private static void displayMenu()
    {
        System.out.println("Please enter one of the following options: ");
        System.out.println("0) To quit");
        System.out.println("1) Echo a message");
        System.out.println("2) Get current date and time");
        System.out.println("3) Get number of requests to server");
    }
}
