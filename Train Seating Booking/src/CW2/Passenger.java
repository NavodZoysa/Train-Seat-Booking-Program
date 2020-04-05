package CW2;

public class Passenger {
    String firstName;
    String surname;
    int secondsInQueue;

    public String getName(){
        return "empty";
    }

    public void setName(String firstName, String surname){
        this.firstName = firstName;
        this.surname = surname;
    }

    public int getSeconds(){
        return 1;
    }

    public void setSecondsInQueue(int sec){
        this.secondsInQueue = sec;
    }

    public void display(){
    }
}
