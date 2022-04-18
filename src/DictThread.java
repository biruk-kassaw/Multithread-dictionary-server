
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DictThread implements Runnable
{
    //Define 5 different actions
    private final int Query = 1;
    private final int Delete = 2;
    private final int Update = 3;
    private final int Add = 4;
    private final int Exit = 5;

    //Define max row of input meaning of words
    private final int Text_Area_Row = 10;

    //dictionary file, client, and server
    private DictionaryFile dictionaryFile;
    private Socket client;
    Server server;

    //file IO
    private InputStreamReader inputStreamReader;
    PrintWriter printWriter = null;
    BufferedReader bufferedReader = null;


    //Constructor
    public DictThread(DictionaryFile dictionaryFile, Socket client, Server server)
    {
        this.dictionaryFile = dictionaryFile;
        this.client = client;
        this.server = server;

        try
        {
            printWriter = new PrintWriter(client.getOutputStream(), true);
            inputStreamReader = new InputStreamReader(client.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
        }
        catch (IOException e)
        {
            System.out.println("File is Invalid");
            e.getMessage();
        }
    }

    //When client connect to server
    //using per-connect model
    public void run()
    {
        while (true)
        {
            int action = 0;
            String commmand = null;
            String word = null;

            try
            {
                commmand = bufferedReader.readLine();
                word = bufferedReader.readLine();
            }
            catch (IOException e)
            {
                e.getMessage();
            }

            action = Integer.parseInt(commmand);
            String[] input;

            switch (action)
            {
                case Query:
                    if (dictionaryFile.containsWord(word))
                    {
                        for (int i = 0; i < Text_Area_Row; i++)
                        {
                            printWriter.println(dictionaryFile.getDefinition(word)[i]);
                        }
                        System.out.println("Client are searching " + word + " in the dictionary");
                        server.getTextArea().append("Client are searching " + word + " in the dictionary\n");
                    }
                    else
                    {
                        for (int i = 0; i < Text_Area_Row; i++)
                        {
                            if (i == 0)
                            {
                                printWriter.println("Word not in the dictionary");
                                System.out.println("Word not in the dictionary");
                            }
                            else
                            {
                                printWriter.println(" ");
                            }
                        }
                    }
                    break;

                case Delete:
                    if (dictionaryFile.containsWord(word))
                    {
                        dictionaryFile.remove(word);
                        printWriter.println("You have Delete " + "'" + word + "'");
                        System.out.println("Client has deleted word: " + word);
                        server.getTextArea().append("Client has delete word: " + "'" + word + "'" + "\n");
                    }
                    else
                    {
                        printWriter.println("This word is not in dictionary!");
                        printWriter.println("Please enter again!");
                    }
                    break;


                case Update:
                    input = new String[Text_Area_Row];
                    try
                    {
                        for (int i = 0; i < Text_Area_Row; i++)
                        {
                            input[i] = bufferedReader.readLine();
                        }
                    }
                    catch (IOException e)
                    {
                        e.getMessage();
                    }

                    if (dictionaryFile.containsWord(word))
                    {
                        printWriter.println("Update the meaning: " + word);
                    }
                    else
                    {
                        printWriter.println("Adding new word: " + word);
                    }

                    dictionaryFile.update(word, input);
                    //Command Line
                    System.out.println("The client has updated word: " + word);
                    server.getTextArea().append("The client has update word: " + word + "\n");

                    break;


                case Add:
                    input = new String[Text_Area_Row];
                    try
                    {
                        for (int i = 0; i < Text_Area_Row; i++)
                        {
                            input[i] = bufferedReader.readLine();
                        }
                    }
                    catch (IOException e)
                    {
                        e.getMessage();
                    }

                    if (dictionaryFile.containsWord(word))
                    {
                        printWriter.println("This word already in the dictionary: " + word);
                    }
                    else
                    {
                        printWriter.println("Adding new word to dictionary: " + word);
                        dictionaryFile.update(word, input);
                        System.out.println("The client has added new word: " + word);
                        server.getTextArea().append("The client has added new word: "+ word + "\n");
                    }
                    break;

                case Exit:
                    server.clientDisconnect();
                    printWriter.close();
                    try
                    {
                        bufferedReader.close();
                        inputStreamReader.close();
                        client.close();
                    }
                    catch (IOException e)
                    {
                        e.getMessage();
                    }
                    Thread.currentThread().interrupt();
                    return;
            }
        }
    }
}
