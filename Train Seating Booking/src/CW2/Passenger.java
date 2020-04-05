package CW2;

public class Passenger {
    private String firstName;
    private String surname;
    private int secondsInQueue;

    public String getName(){
        return firstName+surname;
    }

    public void setName(String firstName, String surname){
        this.firstName = firstName;
        this.surname = surname;
    }

    public int getSeconds(){
        return secondsInQueue;
    }

    public void setSecondsInQueue(int sec){
        this.secondsInQueue = sec;
    }

    public void display(){
    }
}
