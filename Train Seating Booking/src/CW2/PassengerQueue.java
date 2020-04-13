package CW2;

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
        last = last+1%42;
        maxLength++;
    }

    public Passenger remove(){
        Passenger passenger = queueArray[first];
        first = first+1%42;
        maxLength--;
        return passenger;
    }

    public boolean isEmpty(){
        return maxLength==0;
    }

    public boolean isFull(){
        return maxLength==queueArray.length;
    }

    public void display(){}

    public int getLength(){
        return maxLength;
    }

    public int getMaxStay(){
        return maxStayInQueue;
    }

    public void setFirstAndLast(int first, int last){
        this.first = first;
        this.last = last;
    }
}
