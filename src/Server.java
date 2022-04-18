
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Server implements ActionListener
{
    private static DictionaryFile dictionary;
    private int port = 1111;
    private ServerSocket serverSocket;
    private JFrame frame;
    private JTextArea textArea;
    private int numberClient = 0;


    //Initialize the server UI
    private void uiBuild()
    {
        //Set a new frame
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 680);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Server");
        frame.getContentPane().setLayout(null);

        //Set a new button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(215, 580, 216, 35); // set position and range
//        closeButton.setBackground(Color.WHITE);
        frame.getContentPane().add(closeButton);
        closeButton.addActionListener(this);

        //Set a new Scroll Pane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12,13,550,530);
        frame.getContentPane().add(scrollPane);

        //Set a new text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Helvetica", Font.PLAIN, 20));  //set text font attribute

        scrollPane.setViewportView(textArea);
    }

    //Constructor
    public Server()
    {
        uiBuild();
    }



    private void displayInitialStatus() throws UnknownHostException
    {
        InetAddress ip = InetAddress.getLocalHost();  // get local ip address
        //Print in the command line
        System.out.println("Current IP Address: " + "'" + ip.getHostAddress() + "'");
        System.out.println("Current Port: " + port);

        //Print in the Swing UI
        textArea.append("Current IP Address: " + ip.getHostAddress() + "\n");
        textArea.append("Current Port: " + port + "\n");
    }

    private void setPort(String[] args)
    {
        try
        {
            // Transform String to int
            port = Integer.parseInt(args[0]);
            if (port > 65535 || port < 0)
            {
                throw new PortNumberInvalid();
            }
            System.out.println("The input port is: " + port);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("No port input!");
            System.out.println("Using default port: " + port);
            // Output in the text ui
            textArea.append("No port input!\n");
            for (int i =0; i < 5; i++)
            {
                timeDelay(200);
                textArea.append("_ ");
            }
            timeDelay(200);
            textArea.append("\n");
            textArea.append("Using default port: " + port + "\n");
        }
        catch (PortNumberInvalid portNumberInvalid)
        {
            System.out.println("No port input!");
            System.out.println("Using default port: " + port);
            // Output in the text ui
            textArea.append("No port input!\n");
            for (int i =0; i < 5; i++)
            {
                timeDelay(200);
                textArea.append("- ");
            }
            timeDelay(200);
            textArea.append("\n");
            textArea.append("Using default port: " + port + "\n");
        }
        catch (Exception e)
        {
            System.out.println("No port input!");
            System.out.println("Using default port: " + port);
            // Output in the text ui
            textArea.append("No port input!\n");
            for (int i =0; i < 5; i++)
            {
                timeDelay(200);
                textArea.append("- ");
            }
            timeDelay(200);
            textArea.append("\n");
            textArea.append("Using default port: " + port + "\n");
        }
    }


    private void setDictionary(String[] args)
    {
        String filePath = null;

        try
        {
            filePath = args[1];
            File dictFile = new File(filePath);
            if (!dictFile.exists())
            {
                throw new FileNotFoundException();
            }
            dictionary = new DictionaryFile(filePath, this);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            //Output in command line
            System.out.println("No dictionary file input");
            //Output in ui text
            textArea.append("No dictionary file input!\n");
            for (int i = 0; i < 5; i++)
            {
                timeDelay(200);
                textArea.append("- ");
            }
            textArea.append("\n");
            //Use default dictionary
            useDefaultDictFile();
        }
        catch (FileNotFoundException e)
        {
            //Output in command line
            System.out.println("No dictionary file input");
            //Output in ui text
            textArea.append("No dictionary file input!\n");
            for (int i = 0; i < 5; i++)
            {
                timeDelay(200);
                textArea.append("- ");
            }
            textArea.append("\n");
            //Use default dictionary
            useDefaultDictFile();
        }
    }

    private void useDefaultDictFile()
    {
        //output command line
        System.out.println("The server will use default dictionary file");
        //output text area
        textArea.append("The server will use default dictionary file\n");
        //use default dictionary file in folder
        dictionary = new DictionaryFile("Dictionary.dat", this);
    }



    private void connectClient(ServerSocket serverSocket, DictionaryFile dictionary)
    {
        while (true)
        {
            System.out.println("The server is listening...");
            textArea.append("The server is listening for requests...\n");
            Socket client;
            try
            {
                //per connect
                client = serverSocket.accept();
                numberClient++;
                System.out.println("One client has connected!");
                textArea.append("client has connected!\n");

                //New dictionary, new connect
                DictThread dictThread = new DictThread(dictionary, client, this);
                new Thread(dictThread).start();
            }
            catch (IOException e)
            {
                break;
            }

            //Output the number of connected clients into UI
            textArea.append("Number of clients connected: " + numberClient + "\n");
            //Output command line
            System.out.printf("The server is connecting now");

            for (int i = 0; i < 5; i++)
            {
                System.out.printf(" *");
                timeDelay(500);
            }
        }
    }




    //JFrame getter
    public JFrame getFrame()
    {
        return frame;
    }

    //Textarea getter
    public JTextArea getTextArea()
    {
        return textArea;
    }

    //Disconnect client
    public void clientDisconnect()
    {
        textArea.append("One client has disconnected!\n");
        numberClient--;
        textArea.append("Number of clients: " + numberClient + "\n");
    }


    public void actionPerformed(ActionEvent e)
    {
        try
        {
            serverSocket.close();

            //The server will be closed if all clients are disconnected
            if (numberClient == 0)
            {
                ObjectOutputStream outputStream  = new ObjectOutputStream(new FileOutputStream("Dictionary.dat"));
                outputStream.writeObject(dictionary.getDictionary());
                outputStream.close();
                System.exit(0);
            }
            else
            {
                textArea.append("Server socket closed\n");
                textArea.append("Waiting for all clients disconnect\n");
                textArea.append(numberClient + " clients connected\n");
                textArea.append("Try again later!\n");
            }
        }
        catch (IOException io)
        {
            io.getMessage();
        }
    }


    /**
     * The main function of server class
     * create new server and its ui
     * create dictionary file
     * create socket
     * create new port*/
    public static void main(String[] args)
    {
        Server server = new Server();
        server.frame.setVisible(true);

        server.setPort(args);  // Initialize Port
        server.setDictionary(args);  // Initialize Dictionary

        try
        {
            server.serverSocket = new ServerSocket(server.port);
            server.displayInitialStatus();
            server.connectClient(server.serverSocket, dictionary);
        }
        catch (IOException e)
        {
            e.getMessage();
        }
    }

    //Time Delay Function, improve UX
    public static void timeDelay(int inputTime)
    {
        try
        {
            Thread.currentThread().sleep(inputTime);//ms
        }
        catch(Exception e)
        {
            e.getMessage();
        }
    }
}



/**Exception class
 * Input port beyond its trange
 * (0, 65535)*/
class PortNumberInvalid extends Exception
{
    public PortNumberInvalid()
    {
        super("The port input is Invalid (0 <= port <= 65535)");
    }
}
