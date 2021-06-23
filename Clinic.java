import java.util.concurrent.Semaphore; 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.lang.*;

public class Clinic{
    public int n = 1;
    public int m = 10;

    class User{
        String name;
        int time;
        User(String name, int time){
            this.name = name;
            this.time = time;
        }
    }

    ArrayList<User> users=new ArrayList<User>(
        Arrays.asList(
            new User("user1", 1000),
            new User("user2", 1000),
            new User("user3", 2000),
            new User("user4", 4000)
            )
    );

    class WaitingRoom { 
        ArrayList<User> users=new ArrayList<User>();

        Semaphore semCon = new Semaphore(n); 
        Semaphore semProd = new Semaphore(m); 

        void get() 
        { 
            try { 
                semCon.acquire(); 
            } 
            catch (InterruptedException e) { 
                System.out.println("InterruptedException caught"); 
            } 
            
            Random rand = new Random();
            int upperbound = users.size();
            int int_random = rand.nextInt(upperbound); 
            System.out.println(String.format("User %s is leaving.", users.get(int_random))); 
            this.users.remove(int_random);

            semProd.release(); 
        } 

        void put(User user) 
        { 
            try { 
                semProd.acquire(); 
            } 
            catch (InterruptedException e) { 
                System.out.println("InterruptedException caught"); 
            } 

            this.users.add(user);
            System.out.println(String.format("User %s put.", user.name)); 
            users.remove(user);

            semCon.release(); 
        } 
    } 

    class Receptionist implements Runnable { 
        WaitingRoom waitingRoom; 
        Receptionist(WaitingRoom waitingRoom) 
        { 
            this.waitingRoom = waitingRoom; 
            new Thread(this, "Receptionist").start(); 
        } 

        public void run() 
        { 
            long start = System.currentTimeMillis();
            while (users.size() != 0){
                for (int i = 0; i < users.size(); i++){
                    long finish = System.currentTimeMillis();
                    if (finish - start == users.get(i).time)
                        waitingRoom.put(users.get(i)); 
                }
                Thread.sleep(1000);
            }
        } 
    } 

    class Doctor implements Runnable { 
        WaitingRoom waitingRoom; 
        Doctor(WaitingRoom waitingRoom) 
        { 
            this.waitingRoom = waitingRoom; 
            new Thread(this, "Doctor").start(); 
        } 

        public void run() 
        { 
            for (int i = 0; i < waitingRoom.users.size(); i++) 
                waitingRoom.get(); 
        } 
    }

    public static void main(String args[]) 
    { 
        try
        {
            Clinic clinic = new Clinic ();
            clinic.run (args);
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    } 

    public void run (String[] args) throws Exception
    {
            WaitingRoom waitingRoom = new WaitingRoom(); 

            new Receptionist(waitingRoom); 

            for (int i = 0; i<m; ++i){
                new Doctor(waitingRoom); 
            }
    }
}