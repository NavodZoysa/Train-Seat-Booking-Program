import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.*;

public class Booking extends Application {
    static final int seatingCapacity = 42;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Stage stage = new Stage();
        Pane root = new Pane();
        root.setStyle("-fx-background-color:GRAY");
        Scene scene = new Scene(root, 700, 800);
        consoleMenu(root, stage, scene);
    }

    public void createSeats(Pane root,Stage stage, Scene scene, String input, List<String> seatlist, HashMap<Integer,String> customerNames) {
        int colYCord = 60;
        int labelNo = 0;
        int bookedSeatIndex = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" +(++labelNo));
                seat.setId(String.valueOf(labelNo));
                seatlist.add("nb");
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * 80);
                seat.setLayoutY(i * colYCord);
                root.getChildren().addAll(seat);
                seat.setStyle("-fx-background-color:GREEN");
                seat.setAlignment(Pos.CENTER);
                if(input.equals("a")) {
                    int selectedSeat = labelNo-1;
                    System.out.println(selectedSeat);
                    seat.setOnMouseClicked(event -> {
                        if(!seatlist.get(selectedSeat).equals("b")) {
                            seat.setStyle("-fx-background-color:RED");
                            seatlist.set(selectedSeat,"b");
                            System.out.println(seatlist);
                        }
                        TextInputDialog customerNameBox = new TextInputDialog();
                        customerNameBox.setTitle("Customer name");
                        customerNameBox.setHeaderText("Enter the name of the person the seat is booked to");
                        customerNameBox.setContentText("Please enter your name : ");
                        Optional<String> customerNameField = customerNameBox.showAndWait();
                        customerNameField.ifPresent(s -> customerNames.put(selectedSeat, s));
                    });
                    if(seatlist.get(selectedSeat).equals("b")){
                        seat.setStyle("-fx-background-color:RED");
                    }
                }
                else if(input.equals("v")){
                    if(seatlist.get(bookedSeatIndex++).equals("b")){
                        System.out.println(bookedSeatIndex);
                        seat.setStyle("-fx-background-color:RED");
                    }
                    else{
                        seat.setStyle("-fx-background-color:GREEN");
                    }
                }
                else{
                    System.out.println("e");
                    if(seatlist.get(bookedSeatIndex++).equals("b")){
                        seat.setStyle("-fx-background-color:GRAY");
                        seat.setTextFill(Paint.valueOf("gray"));
                    }
                    else{
                        seat.setStyle("-fx-background-color:GREEN");
                    }
                }
            }
        }
        stage.setTitle("Train Seat Booking Application");
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        System.out.println(customerNames);
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, String input, List<String> seatlist, HashMap<Integer,String> customerNames) {
        createSeats(root, stage, scene, input, seatlist, customerNames);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, String input, List<String> seatlist, HashMap<Integer,String> customerNames) {
        createSeats(root, stage, scene, input, seatlist, customerNames);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, String input, List<String> seatlist, HashMap<Integer,String> customerNames){
        createSeats(root, stage, scene, input, seatlist, customerNames);
    }

    public void deleteCustomer(Scanner scanner,List<String> seatlist, HashMap<Integer,String> customerNames){
        System.out.print("Please enter the seat number you want to remove : S-");
        int deleteSeatNo = scanner.nextInt();
        seatlist.set(deleteSeatNo-1,"nb");
        customerNames.remove(deleteSeatNo-1);
        System.out.println(seatlist);
        System.out.println(customerNames);
    }

    public void consoleMenu(Pane root, Stage stage, Scene scene) {
        Scanner scanner = new Scanner(System.in);
        List<String> bookedList = new ArrayList<>(seatingCapacity);
        HashMap<Integer,String> customerList = new HashMap<>(seatingCapacity);
        while(true) {
            System.out.println("\nWelcome To Fort Railway Station\n" +
                    "Denuwara Menike Intercity Express Train departure from Colombo to Badulla\n"+
                    "\nPlease enter 'A' to add a customer to a seat\n" +
                    "Please enter 'V' to view all seats\n" +
                    "Please enter 'E' to display empty seats\n" +
                    "Please enter 'D' to delete customer from seat\n" +
                    "Please enter 'F' to find the seat for a given customer name\n" +
                    "Please enter 'S' to store the booking details to a file\n" +
                    "Please enter 'L' to load the booking details from a file\n" +
                    "Please enter 'O' to view seats ordered alphabetically by customer name\n" +
                    "Please enter 'Q' to quit the program");
            String userInput = scanner.next().toLowerCase();

            switch (userInput) {
                case "a":
                    addCustomerToSeat(root, stage, scene, userInput, bookedList, customerList);
                    break;
                case "v":
                    viewAllSeats(root, stage, scene, userInput, bookedList, customerList);
                    break;
                case "e":
                    displayEmptySeats(root, stage, scene, userInput, bookedList, customerList);
                    break;
                case "d":
                    deleteCustomer(scanner, bookedList, customerList);
                    break;
                /*case "f":
                    break;
                case "s":
                    break;
                case "l":
                    break;
                case "o":
                    break;*/
                case "q":
                    System.exit(0);
                default:
                    System.out.println("Invalid input! Please enter a valid input ");
                    break;
            }
        }
    }
}
