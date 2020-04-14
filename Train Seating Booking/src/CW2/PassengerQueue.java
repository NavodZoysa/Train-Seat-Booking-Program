package CW2;

public class PassengerQueue {
    private Passenger[] queueArray = new Passenger[42];
    private int first;
    private int last;
    private static int maxStayInQueue;
    private int maxLength;

    public Passenger[] getQueueArray(){
        return queueArray;
    }

    public void add(Passenger next){
        if(!isFull()) {
            queueArray[last] = next;
            last = last + 1 % 42;
            maxLength++;
        }
        else {
            System.out.println("Queue is full");
        }
    }

    public Passenger remove(){
        Passenger passenger = queueArray[first];
        if(!isEmpty()) {
            first = first + 1 % 42;
            maxLength--;
        }
        else{
            System.out.println("Queue is empty");
        }
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

    public int getMaxStayInQueue(){
        return maxStayInQueue;
    }

    public void setMaxStayInQueue(int seconds){
        maxStayInQueue = maxStayInQueue + seconds;
        queueArray[last-1].setSecondsInQueue(maxStayInQueue);
    }

    public int getMinStayInQueue(){
        return queueArray[first].getSecondsInQueue();
    }

    public void setFirstAndLast(int first, int last){
        this.first = first;
        this.last = last;
    }
}
