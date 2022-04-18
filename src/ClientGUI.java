
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGUI implements ActionListener
{
    //Basic operation of client
    private final int Query = 1;
    private final int Delete = 2;
    private final int Update = 3;
    private final int Add = 4;
    private final int Exit = 5;

    //Define the text length of one word in dictionary
    private final int textRowMax = 10;

    //Define the UI contributes
    private JFrame frame;
    //private JTextArea textArea;
    private JTextArea textArea;
    private JTextField textField;

    //File IO
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    //TCP Socket
    private Socket client;

    //Constructor of the class
    public ClientGUI(BufferedReader reader, PrintWriter writer, Socket socket)
    {
        initialize();
        this.bufferedReader = reader;
        this.printWriter = writer;
        this.client = socket;
    }

    public void initialize()
    {
        frame = new JFrame();

        //set bounds of UI
        frame.setBounds(100, 100, 1100, 500);
        frame.setTitle("Client");
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.getContentPane().setLayout(null);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
                printWriter.println(Exit);
                printWriter.println("");
                printWriter.close();

                try
                {
                    bufferedReader.close();
                    client.close();
                }
                catch (IOException e)
                {
                    e.getMessage();
                }
                System.exit(0);
            }
        });

        textField = new JTextField();
        //set attribute of text in UI
        textField.setFont(new Font("Helvetica", Font.PLAIN, 20));

        //set attributes
        textField.setBounds(55, 30, 190, 45);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        textField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                lookUp();
            }
        });

        textField.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                if (textField.getText().trim().equals("Enter Word"))
                {
                    textField.setText("");
                    textField.setForeground(Color.darkGray);
                }
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (textField.getText().trim().equals(""))
                {
                    textField.setText("Enter Word");
                    textField.setForeground(Color.darkGray);
                }
            }
        });

        //Add new buttons: Search button
        JButton searchButton = new JButton("Search"); //No Text Input
        searchButton.setToolTipText("Search Word");
        searchButton.setActionCommand("Look up");
//        searchButton.setBackground(new Color(51, 203, 252));
        searchButton.setBounds(45, 100, 120, 35);
        searchButton.addActionListener(this);
        frame.getContentPane().add(searchButton);


        //Add new button: Add new word button
        JButton addButton = new JButton("Add");
        addButton.setToolTipText("Add a new word");
        addButton.setActionCommand("Add");
//        addButton.setBackground(new Color(51, 203, 252));
        addButton.setBounds(45,180,120,30);
        addButton.addActionListener(this);
        frame.getContentPane().add(addButton);


        //Add new button: Delete Button
        JButton deleteButton = new JButton("Remove");
        deleteButton.setToolTipText("Delete Word");
        deleteButton.setActionCommand("Delete");
//        deleteButton.setBackground(new Color(51, 203, 252));
        deleteButton.setBounds(45, 340,120,35);
        deleteButton.addActionListener(this);
        frame.getContentPane().add(deleteButton);


        //Add new button: Update Button
        JButton updateButton = new JButton("Update");
        updateButton.setToolTipText("Update a word");
        updateButton.setActionCommand("Update");
//        updateButton.setBackground(new Color(51, 203, 252));
        updateButton.setBounds(45, 260, 120, 35);
        updateButton.addActionListener(this);
        frame.getContentPane().add(updateButton);



        //Add new button: Clear Button
        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear ALL Input");
        clearButton.setActionCommand("Clear");
//        clearButton.setBackground(new Color(240, 155, 48));
        clearButton.setBounds(255,31,75,40);
        clearButton.addActionListener(this);
        frame.getContentPane().add(clearButton);

        //Add new button: close button
        JButton closeButton = new JButton("Disconnect");
        closeButton.setToolTipText("Disconnect this client");
        closeButton.setActionCommand("Disconnect");
//        closeButton.setBackground(new Color(240, 65, 49));
        closeButton.setBounds(500,415,100,30);
        closeButton.addActionListener(this);
        frame.getContentPane().add(closeButton);



        //scroll pane
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(190,87,809,315);
        frame.getContentPane().add(scrollPane);


        //Add text area
        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        textArea.setRows(textRowMax);
        //font size
        textArea.setFont(new Font("Tahoma", Font.PLAIN, 24));
        textArea.setLineWrap(true);
        textArea.setForeground(Color.BLACK);


        //Set new label
        JLabel label = new JLabel("Write Definition of the word Below When You Add New Word");
        label.setFont(new Font("Helvetica", Font.PLAIN, 20));
        label.setForeground(Color.white);
        label.setBounds(370, 35, 500, 35);
        frame.getContentPane().add(label);
    }


    //Framge getter
    public JFrame getFrame()
    {
        return frame;
    }


    //Override action perform
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals("Look up"))
        {
            lookUp();
        }

        if (action.equals("Delete"))
        {
            delete();
        }

        if (action.equals("Update"))
        {
            update();
        }

        if (action.equals("Add"))
        {
            add();
        }

        if (action.equals("Clear"))
        {
            textField.setText("");
            textArea.setText("");
            textField.setForeground(Color.DARK_GRAY);
        }

        if (action.equals("Disconnect"))
        {
            disconnect();
        }

    }


    /**A set of basic operations
     * Search
     * Delete
     * Update
     * Add*/
    public void lookUp()
    {
        try
        {
            String input = textField.getText().trim().toLowerCase();

            if (input.equals("") || input.equals("enter word"))
            {
                throw new NoInputWordException();
            }

            textArea.setText("");

            printWriter.println(Query);
            TimeDelay("", 3000);
            printWriter.println(input);
            //Search this word in dictionary
            for (int i = 0; i < textRowMax; i++)
            {
                String line = bufferedReader.readLine();
                textArea.append(line + "\n");
            }
        }
        catch (NoInputWordException e)
        {
            TimeDelay("Please enter word that you want search!", 1200);
            //textField.setText("");
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println("Something wrong! Please try again later!");
            textArea.setText("Something wrong! Please try again later!\n");
            e.getMessage();
        }
    }


    public void add()
    {
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to add a new word?",
                "Add", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION)
        {
            try
            {
                String word = textField.getText().trim().toLowerCase();

                if (word.equals("") || word.equals("enter word"))
                {

                    throw new NoInputWordException();
                }

                String[] temp = textArea.getText().trim().split("\\n");

                if (temp.length > textRowMax)
                {
                    throw new InputTooLong();
                }

                if (temp[0].equals(""))
                {

                    throw new NoDefinitionInput();
                }

                String[] lines = new String[textRowMax];
                printWriter.println(Add);
                TimeDelay("", 5000);
                printWriter.println(word);

                for (int i = 0; i < textRowMax; i++)
                {
                    if (i < temp.length)
                    {
                        lines[i] = temp[i];
                    }
                    else
                    {
                        lines[i] = " ";
                    }
                }

                for (int i = 0; i < textRowMax; i++)
                {
                    printWriter.println(lines[i]);
                }

                String line = bufferedReader.readLine();
                textArea.setText(line);
                textField.setText("");
            }
            catch (NoInputWordException e)
            {
                TimeDelay("Please Input word!", 1500);
                System.out.println(e.getMessage());
            }
            catch (InputTooLong e)
            {
                TimeDelay("The definition is too long", 1200);
                textArea.setText(e.getMessage());
            }
            catch (NoDefinitionInput e)
            {
                System.out.println(e.getMessage());
                TimeDelay("Please input definition here!", 1000);
            }
            catch (IOException e)
            {
                textArea.setText(e.getMessage());
                System.out.println("Something Wrong! Try again later.");
            }
        }
    }



    
    public void update()
    {
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to update the word?",
                "Update", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION);
        {
            try
            {
                String word = textField.getText().toLowerCase().trim();

                if (word.equals("") || word.equals("enter word"))
                {
                    throw new NoInputWordException();
                }

                String[] temp = textArea.getText().trim().split("\\n");

                if (temp.length > textRowMax)
                {
                    throw new InputTooLong();
                }
                if (temp[0].equals(""))
                {
                    throw new NoDefinitionInput();
                }

                String[] lines = new String[textRowMax];
                printWriter.println(Update);
                TimeDelay("", 1500);
                printWriter.println(word);

                for (int i = 0; i < textRowMax; i++)
                {
                    if (i < temp.length)
                    {
                        lines[i] = temp[i];
                    }
                    else
                    {
                        lines[i] = " ";
                    }
                }

                for (int i = 0; i < textRowMax; i++)
                {
                    printWriter.println(lines[i]);
                }

                String line = bufferedReader.readLine();
                textArea.setText(line);
            }
            catch (NoInputWordException e)
            {
                TimeDelay("Please enter word!", 1500);
                System.out.println(e.getMessage());
            }
            catch (InputTooLong e)
            {
                TimeDelay("Your input is too long!", 1500);
                System.out.println(e.getMessage());
            }
            catch (NoDefinitionInput e)
            {
                TimeDelay("Please enter definition", 1500);
                System.out.println(e.getMessage());
            }
            catch (IOException e)
            {
                e.getMessage();
                textArea.setText(e.getMessage());
            }

        }
    }

    public void disconnect()
    {
        printWriter.println(Exit);
        System.exit(0);
    }



    public void delete()
    {
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to delete it?",
                "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION)
        {
            try
            {
                String word = textField.getText().trim().toLowerCase();

                if (word.equals("") || word.equals("enter word"))
                {
                    throw new NoInputWordException();
                }

                printWriter.println(Delete);
                TimeDelay("", 1500);
                printWriter.println(word);
                //Show in UI
                String line = bufferedReader.readLine();
                textArea.setText(line);
            }
            catch (NoInputWordException e)
            {
                TimeDelay("Please enter word that you want to remove!", 1200);
                System.out.println(e.getMessage());
            }
            catch (IOException e)
            {
                e.getMessage();
            }
        }
    }
    
    



    public void TimeDelay(String arg1, int time)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                textArea.setText("");
            }
        }, time);
        textArea.setText(arg1);
    }

    //Clear all text in UI
    public  void clearAll()
    {
        TimeDelay("", 1500);
    }
}




/**Exception class to handle with no input*/
class NoInputWordException extends Exception
{
    public NoInputWordException()
    {
        super("Please enter the word!");
    }
}


/**Exception class to handle input beyond limit*/
class InputTooLong extends Exception
{
    public InputTooLong()
    {
        super("The definition is too long! Please input it again!");
    }
}


/**Exception class
 * Handle the exception situation
 * When user do not input any definition
 * */
class NoDefinitionInput extends Exception
{
    public NoDefinitionInput()
    {
        super("Please input the definition of this new word!");
    }
}