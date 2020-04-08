package CW2;

public class Passenger {
    private String firstName;
    private String surname;
    private int secondsInQueue;
    private String train;
    private String seatNumber;
    private String nic;
    private String date;
    private String from;
    private String to;
    private String[] otherDetails = new String[7];
    private static int ticketNo;
    private String ticketId;

    public String getName(){
        return firstName+" "+surname;
    }

    public void setName(String firstName, String surname){
        this.firstName = firstName;
        this.surname = surname;
    }

    public String[] getOtherDetails(){
        otherDetails[0] = train;
        otherDetails[1] = seatNumber;
        otherDetails[2] = ticketId;
        otherDetails[3] = nic;
        otherDetails[4] = date;
        otherDetails[5] = from;
        otherDetails[6] = to;
        return otherDetails;
    }

    public void setOtherDetails(String train, String seatNumber, String nic, String date, String from, String to){
        this.train = train;
        this.seatNumber = seatNumber;
        this.nic = nic;
        this.date = date;
        this.from = from;
        this.to = to;
        ticketNo++;
        this.ticketId = "#DM"+ticketNo;
    }

    public int getSeconds(){
        return secondsInQueue;
    }

    public void setSecondsInQueue(int sec){
        this.secondsInQueue = sec;
    }

    public void display(){}
}
