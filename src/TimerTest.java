
import java.util.*;
public class TimerTest
{
    public static void main(String[] args)
    {
        Timer timer = new Timer();

        timer.schedule(new TimerTask()
        {
            public void run()
            {
                System.out.println("tm");
                this.cancel();
            }
        }, 5000);

        System.out.println("This program will quit in 5 seconds");
    }
}
