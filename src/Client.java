

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    private static final int defaultPort = 1111;
    private static final String defaultHost = "localhost";
    private static BufferedReader reader = null;
    private static PrintWriter writer = null;
    private static Socket client;

    private static void defaultConnect()
    {
        System.out.println("Input port or address is invalid");
        System.out.println("The system will use default port and address");

        try
        {
            client = new Socket(defaultHost, defaultPort);
            System.out.println("Start connecting...");
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
        }
        catch (IOException e)
        {
            System.out.println("Connection failed!");
            System.out.println("Try again later!");
            System.exit(0);
        }
    }

    private static void connect(String[] input)
    {
        try
        {
            String serverAddress = input[0];
            int port = Integer.parseInt(input[1]);
            client = new Socket(serverAddress, port);

            System.out.printf("Connecting...");


            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("The system will use default connect mechanism!");
            defaultConnect();
        }
        catch (UnknownHostException e)
        {
            System.out.println("The system will use default connect mechanism!");
            defaultConnect();
        }
        catch (NumberFormatException e)
        {
            System.out.println("The system will use default connect mechanism!");
            defaultConnect();
        }
        catch (IOException e)
        {
            System.out.println("The system will use default connect mechanism!");
            defaultConnect();
        }
        catch (Exception e)
        {
            System.out.println("The system will use default connect mechanism!");
            defaultConnect();
        }

    }

    /**Run the main function of Client
     * Build a new socket connection of server
     * Run the UI of client*/
    public static void main(String[] args)
    {
        connect(args);
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ClientGUI window = new ClientGUI(reader, writer, client);
                    window.getFrame().setVisible(true);
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
            }
        });
    }
}

