package CW2;

import java.util.Arrays;

public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[42];
    private int first;
    private int last;
    private int maxStayInQueue;
    private int maxLength;

    public Passenger[] getQueueArray(){
        return queueArray;
    }

    public void add(Passenger next){
        queueArray[last] = next;
        last++;
        maxLength++;
    }

    public void remove(){}

    public boolean isEmpty(){
        return first == last;
    }

    public boolean isFull(){
        return last==maxLength-1;
    }

    public void display(){}

    public int getLength(){
        return maxLength;
    }

    public int getMaxStay(){
        return maxStayInQueue;
    }
}
