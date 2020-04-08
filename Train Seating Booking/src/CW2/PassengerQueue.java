package CW2;

public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[6];
    private int first;
    private int last;
    private int maxStayInQueue;
    private int maxLength;

    private void add(Passenger next){}

    private void remove(){}

    public boolean isEmpty(){
        return true;
    }

    public boolean isFull(){
        return false;
    }

    public void display(){}

    public int getLength(){
        return maxLength;
    }

    public int getMaxStay(){
        return maxStayInQueue;
    }
}
