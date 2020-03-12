import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class Booking extends Application {
    static final int seatingCapacity = 42;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Stage stage = new Stage();
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 820, 500);
        stage.setTitle("Train Seat Booking Application");
        Label title = new Label("Welcome to Colombo Fort Railway Station");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label(
                "Train name - Denuwara Menike\n"+
                "Train number - 1001\n"+
                "Train departure - Colombo\n"+
                "Train arrival - Badulla\n"+
                "Departure time - 06:45AM\n"+
                "Arrival  time - 02:27PM\n"+
                "Class - 1st Class A/C Compartment\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(500);
        details.setLayoutY(100);

        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);
        root.getChildren().addAll(title,details,emptySeat);
        consoleMenu(root, stage, scene);
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames) {
        int colXCord = 60;
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * colXCord);
                seat.setLayoutY(i * colYCord);
                root.getChildren().add(seat);
                seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");

                int selectedSeat = labelNo;
                seat.setOnMouseClicked(event -> {
                    if(customerNames.get(selectedSeat).equals("nb")) {
                        seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                        customerNames.put(selectedSeat,"b");
                    }
                    seat.setOnMouseClicked(event1 -> {
                        if(customerNames.get(selectedSeat).equals("b")){
                            seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                            customerNames.put(selectedSeat,"nb");
                        }
                    });
                });
                if(!customerNames.get(selectedSeat).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        Label bookedSeat = new Label("Unavailable");
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);

        Button bookButton = new Button("Confirm Booking");
        bookButton.setPrefSize(120,40);
        bookButton.setStyle("-fx-background-color: #2144cf; -fx-border-width: 1.5; -fx-border-radius: 3; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black; -fx-background-insets: 0");
        bookButton.setLayoutX(500);
        bookButton.setLayoutY(310);

        Button clearButton = new Button("Clear Seats");
        clearButton.setPrefSize(100,40);
        clearButton.setStyle("-fx-background-color: #bd1520; -fx-border-width: 1.5; -fx-border-radius: 3; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black; -fx-background-insets: 0");
        clearButton.setLayoutX(650);
        clearButton.setLayoutY(310);
        root.getChildren().addAll(bookedSeat,bookButton,clearButton);

        bookButton.setOnAction(event -> {
            TextInputDialog customerNameBox = new TextInputDialog();
            customerNameBox.setTitle("Customer name");
            customerNameBox.setHeaderText("Enter the name of the person the seat is booked to");
            customerNameBox.setContentText("Please enter your name (Numbers,'b' and 'nb' not allowed): ");
            Optional<String> customerNameField = customerNameBox.showAndWait();
            customerNameField.ifPresent(name -> {
                if(name.toLowerCase().equals("b") || name.toLowerCase().equals("nb") || !name.matches("[.a-zA-Z\\s]+") || name.trim().isEmpty()){
                    for(int item : customerNames.keySet()) {
                        if(customerNames.get(item).equals("b")) {
                            customerNames.put(item,"nb");
                        }
                    }
                    Alert invalidName = new Alert(Alert.AlertType.WARNING);
                    invalidName.setTitle("Invalid Name");
                    invalidName.setHeaderText("Warning! Invalid Name Input!");
                    invalidName.setContentText(name+" is not valid! "+"Please enter a valid name when booking a seat (Only letters with or without spaces allowed. Numbers,special characters,'b' and 'nb' is not allowed)! Try again.");
                    invalidName.showAndWait();
                    stage.close();
                }
                for(int item : customerNames.keySet()) {
                    if(customerNames.get(item).equals("b")) {
                        customerNames.put(item,name);
                    }
                }
            });

            Alert emptyName = new Alert(Alert.AlertType.WARNING);
            emptyName.setTitle("No name entered");
            emptyName.setHeaderText("Warning! No name entered or Invalid input!");
            emptyName.setContentText("Please enter a valid name when booking a seat('b' and 'nb' is not allowed)! Try again.");
            for(int item : customerNames.keySet()) {
                if(customerNames.get(item).equals("b") || customerNames.get(item).isEmpty()) {
                    customerNames.put(item,"nb");
                    emptyName.showAndWait();
                    stage.close();
                    break;
                }
            }
        });

        clearButton.setOnAction(event -> {
            Alert clearSeats = new Alert(Alert.AlertType.INFORMATION);
            clearSeats.setTitle("Clear Seats");
            clearSeats.setHeaderText("You are removing selected seats for this session!");
            clearSeats.setContentText("To remove a seat that is already booked with a name please select option 'D' from the menu.");
            for(int item : customerNames.keySet()) {
                if(customerNames.get(item).equals("b") || customerNames.get(item).isEmpty()) {
                    customerNames.put(item,"nb");
                    clearSeats.showAndWait();
                    clearSeats.close();
                    stage.close();
                    break;
                }
            }
        });
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();

        for(int item : customerNames.keySet()) {
            if(customerNames.get(item).equals("b") || customerNames.get(item).isEmpty()) {
                customerNames.put(item,"nb");
            }
        }
        root.getChildren().removeAll(bookedSeat,bookButton,clearButton);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames) {
        int colXCord = 60;
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * colXCord);
                seat.setLayoutY(i * colYCord);
                root.getChildren().add(seat);
                seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");

                if(!customerNames.get(labelNo).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
                else{
                    seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        Label bookedSeat = new Label("Unavailable");
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);
        root.getChildren().add(bookedSeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().remove(bookedSeat);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames){
        int colXCord = 60;
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * colXCord);
                seat.setLayoutY(i * colYCord);
                root.getChildren().add(seat);
                seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");

                if(!customerNames.get(labelNo).equals("nb")){
                    seat.setStyle("-fx-background-color: #1b87c2;");
                    seat.setTextFill(Paint.valueOf("#1b87c2"));
                }
                else{
                    seat.setStyle("-fx-background-color:GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
    }

    public void deleteCustomer(Scanner scanner, HashMap<Integer,String> customerNames){
        for(int item : customerNames.keySet()){
            if(item%10==0){
                System.out.println("\n");
            }
            System.out.print("S-"+item+" = "+customerNames.get(item)+"|");
        }
        System.out.print("\n\nPlease choose whether you want to remove all the seats related to your name or not(y/n) : ");
        String choice = scanner.next().toLowerCase();
        if (choice.equals("y")) {
            System.out.print("Please enter your name to remove all seats booked for you : ");
            String deleteName = scanner.next();
            if(customerNames.containsValue(deleteName)) {
                for (int item : customerNames.keySet()) {
                    if (customerNames.get(item).equals(deleteName)) {
                        customerNames.put(item, "nb");
                    }
                }
                System.out.println("Successfully deleted all seats booked under your name");
            } else {
                System.out.println("Invalid name please try again");
            }
        }
        else if (choice.equals("n")) {
            System.out.print("Please enter the seat number you want to remove : S-");
            int deleteSeatNo = scanner.nextInt();
            if (customerNames.containsKey(deleteSeatNo)) {
                customerNames.put(deleteSeatNo,"nb");
                System.out.println("Successfully deleted booked seat");
            } else{
                System.out.println("Invalid seat number please try again");
            }
        }
        else {
            System.out.println("Invalid input please try again");
        }
    }

    public void findCustomer(Scanner scanner, HashMap<Integer,String> customerNames){
        System.out.print("Please enter the name of the customer to find the related seat booked : ");
        String findCustomerName = scanner.next();
        if(customerNames.containsValue(findCustomerName)) {
            System.out.print("Customer seat with name "+findCustomerName+" includes seat ");
            for (int item : customerNames.keySet()) {
                if (findCustomerName.equals(customerNames.getOrDefault(item, findCustomerName))) {
                    System.out.print("|S-" + (item)+"| ");
                }
            }
        }
        else{
            System.out.println("Customer name invalid");
        }
    }

    public void saveToFile(HashMap<Integer,String> customerNames) throws IOException {
        FileWriter writer = new FileWriter("src/customerData.txt");
        for(int item:customerNames.keySet()){
            writer.write(item+"="+customerNames.get(item)+"\n");
        }
        writer.close();
        System.out.println("Successfully save to file");
    }

    public void loadFromFile(HashMap<Integer,String> customerNames) throws FileNotFoundException {
        Scanner read = new Scanner(new File("src/customerData.txt"));
        while(read.hasNextLine()){
            String line = read.nextLine();
            String[] pairs = line.split("=");
            customerNames.put(Integer.parseInt(pairs[0]),pairs[1]);
        }
        read.close();
        System.out.println("Successfully loaded from file");
    }

    public void orderCustomerNames(HashMap<Integer,String> customerNames){
        HashMap<Integer ,String> orderList = new HashMap<>(seatingCapacity);
        for(int i=1;i<=customerNames.size();i++){
            orderList.put(i,customerNames.get(i)+" - "+(i));
        }

        System.out.println("Customer ordered on first come first served basis\n");
        for(int item : orderList.keySet()) {
            if(!orderList.get(item).contains("nb -")) {
                System.out.println(orderList.get(item));
            }
        }
        for(int i=1;i<orderList.size()+1;i++){
            for(int j=i+1;j<orderList.size()+1;j++){
                if(orderList.get(i).compareTo(orderList.get(j))>0){
                    String temp =orderList.get(i);
                    orderList.put(i,orderList.get(j));
                    orderList.put(j,temp);
                }
            }
        }
        System.out.println("\nCustomer names ordered in the ascending order\n");
        for(int item : orderList.keySet()){
            if(!orderList.get(item).contains("nb -")) {
                System.out.println(orderList.get(item));
            }
        }
    }

    public void consoleMenu(Pane root, Stage stage, Scene scene) throws IOException {
        Scanner scanner = new Scanner(System.in);
        HashMap<Integer,String> customerList = new HashMap<>(seatingCapacity);
        while(true) {
            System.out.println("\n\nWelcome To Fort Railway Station\n" +
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
                    addCustomerToSeat(root, stage, scene, customerList);
                    break;
                case "v":
                    viewAllSeats(root, stage, scene, customerList);
                    break;
                case "e":
                    displayEmptySeats(root, stage, scene, customerList);
                    break;
                case "d":
                    deleteCustomer(scanner, customerList);
                    break;
                case "f":
                    findCustomer(scanner, customerList);
                    break;
                case "s":
                    saveToFile(customerList);
                    break;
                case "l":
                    loadFromFile(customerList);
                    break;
                case "o":
                    orderCustomerNames(customerList);
                    break;
                case "q":
                    System.exit(0);
                default:
                    System.out.println("Invalid input! Please enter a valid input ");
                    break;
            }
        }
    }
}
