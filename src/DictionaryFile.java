
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;


/**This class define some basic functions of dictionary
 * Search the word in the dictionary
 * Add new word in the dictionary
 * Update the new word in the dictionary
 * Remove the existed word in the dictionary
 * The data of dictionary stored in HashMap*/
public class DictionaryFile
{
    private Map<String, String[]> dictionary = new HashMap<String, String[]>();
    private Server server;

    //@SuppressWarnings("unchecked")
    //Constructor
    public DictionaryFile(String path, Server server)
    {
        this.server = server;

        try
        {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(path));
            dictionary = (HashMap<String, String[]>) input.readObject();
            input.close();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Invalid dictionary file!");
            useEmptyDictionary();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found in the path!");
            useEmptyDictionary();
        }
        catch (IOException e)
        {
            System.out.println("File Invalid!");
            useEmptyDictionary();
        }
        catch (Exception e)
        {
            e.getMessage();
            useEmptyDictionary();
        }
    }

    //When new file input or input is wrong
    //This method will create a new empty file
    private void useEmptyDictionary()
    {
        System.out.println("The server will use a new empty dictionary file");
        dictionary = new HashMap<String, String[]>();
    }

    //Function check if the input word in the dictionary
    public synchronized boolean containsWord(String word)
    {
        return dictionary.containsKey(word);
    }

    //Function to obtain the meaning of the input word
    public synchronized String[] getDefinition(String word)
    {
        return dictionary.get(word);
    }

    //Function update the new word to local dictionary file
    public synchronized void update(String word, String[] def)
    {
        dictionary.put(word, def);
    }

    //Function remove word in the dictionary
    public synchronized void remove(String word)
    {
        dictionary.remove(word);
    }

    //Return the final dictionary file
    public Map<String, String[]> getDictionary()
    {
        return dictionary;
    }
}
