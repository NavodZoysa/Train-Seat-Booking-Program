package CW2;

public class Passenger {
    private String firstName, surname, name, train, seatNumber, nic, date, from,
            to, ticketId, queueNumber;
    private boolean arrived;
    private int secondsInQueue;

    public String getName(){
        return name = firstName+" "+surname;
    }

    public void setName(String firstName, String surname){
        this.firstName = firstName;
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getSeatNumber(){
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber){
        this.seatNumber = seatNumber;
    }

    public String getTicketId(){
        return ticketId;
    }

    public void setTicketId(String ticketId){
        this.ticketId = ticketId;
    }

    public String getTrain() {
        return train;
    }

    public void setTrain(String train) {
        this.train = train;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getSecondsInQueue(){
        return secondsInQueue;
    }

    public void setSecondsInQueue(int sec){
        this.secondsInQueue = sec;
    }

    public String isArrived() {
        if(arrived){
            return "Yes";
        }
        else{
            return "No";
        }
    }

    public void setArrived(String arrived) {
        if(arrived.equals("Yes")) {
            this.arrived = true;
        }
        else if(arrived.equals("No")){
            this.arrived = false;
        }
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }
}
