import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    public void createSeats(Pane root,Stage stage, Scene scene, String input, List<String> seatlist) {
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
                            System.out.println(selectedSeat);
                            seat.setStyle("-fx-background-color:RED");
                            seatlist.set(selectedSeat,"b");
                            System.out.println(seatlist);
                        }
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
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, String input, List<String> seatlist) {
        createSeats(root, stage, scene, input, seatlist);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, String input, List<String> seatlist) {
        createSeats(root, stage, scene, input, seatlist);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, String input, List<String> seatlist){
        createSeats(root, stage, scene, input, seatlist);
    }

    public void consoleMenu(Pane root, Stage stage, Scene scene) {
        Scanner scan = new Scanner(System.in);
        List<String> bookedList = new ArrayList<>(seatingCapacity);
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
            String userInput = scan.next().toLowerCase();

            switch (userInput) {
                case "a":
                    addCustomerToSeat(root, stage, scene, userInput, bookedList);
                    break;
                case "v":
                    viewAllSeats(root, stage, scene, userInput, bookedList);
                    break;
                case "e":
                    displayEmptySeats(root, stage, scene, userInput, bookedList);
                    break;
                /*case "d":
                    break;
                case "f":
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
