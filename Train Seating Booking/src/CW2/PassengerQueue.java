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

    public int getMaxStayInQueue(){
//        for(int i =0; i < maxLength;i++){
//            maxStayInQueue = maxStayInQueue + queueArray[i].getSecondsInQueue();
//        }
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
