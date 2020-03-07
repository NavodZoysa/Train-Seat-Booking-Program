import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
        root.setStyle("-fx-background-color:GRAY");
        Scene scene = new Scene(root, 700, 800);
        consoleMenu(root, stage, scene);
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames) {
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                seat.setId(String.valueOf(labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * 80);
                seat.setLayoutY(i * colYCord);
                root.getChildren().addAll(seat);
                seat.setStyle("-fx-background-color:GREEN");
                seat.setAlignment(Pos.CENTER);

                int selectedSeat = labelNo;
                seat.setOnMouseClicked(event -> {
                    if(customerNames.get(selectedSeat).equals("nb")) {
                        seat.setStyle("-fx-background-color:RED");
                        customerNames.put(selectedSeat,"b");
                    }
                });
                if(!customerNames.get(selectedSeat).equals("nb")){
                    seat.setStyle("-fx-background-color:RED");
                }
            }
        }
        stage.setTitle("Train Seat Booking Application");
        stage.setScene(scene);
        stage.showAndWait();

        TextInputDialog customerNameBox = new TextInputDialog();
        customerNameBox.setTitle("Customer name");
        customerNameBox.setHeaderText("Enter the name of the person the seat is booked to");
        customerNameBox.setContentText("Please enter your name : ");
        Optional<String> customerNameField = customerNameBox.showAndWait();
        customerNameField.ifPresent(name -> {
            for(int item : customerNames.keySet()) {
                if(customerNames.get(item).equals("b")) {
                    customerNames.put(item,name);
                }
            }
        });

        stage.close();
        System.out.println(customerNames);
        System.out.println(customerNames.size());
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames) {
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                seat.setId(String.valueOf(labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * 80);
                seat.setLayoutY(i * colYCord);
                root.getChildren().addAll(seat);
                seat.setStyle("-fx-background-color:GREEN");
                seat.setAlignment(Pos.CENTER);

                if(!customerNames.get(labelNo).equals("nb")){
                    seat.setStyle("-fx-background-color:RED");
                }
                else{
                    seat.setStyle("-fx-background-color:GREEN");
                }
            }
        }
        stage.setTitle("Train Seat Booking Application");
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames){
        int colYCord = 60;
        int labelNo = 0;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 7; j++) {
                Label seat = new Label("S-" + (++labelNo));
                seat.setId(String.valueOf(labelNo));
                if (customerNames.size()<seatingCapacity) {
                    customerNames.put(labelNo,"nb");
                }
                seat.setPrefSize(50, 50);
                seat.setLayoutX(j * 80);
                seat.setLayoutY(i * colYCord);
                root.getChildren().addAll(seat);
                seat.setStyle("-fx-background-color:GREEN");
                seat.setAlignment(Pos.CENTER);

                if(!customerNames.get(labelNo).equals("nb")){
                    seat.setStyle("-fx-background-color:GRAY");
                    seat.setTextFill(Paint.valueOf("gray"));
                }
                else{
                    seat.setStyle("-fx-background-color:GREEN");
                }
            }
        }
        stage.setTitle("Train Seat Booking Application");
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
    }

    public void deleteCustomer(Scanner scanner, HashMap<Integer,String> customerNames){
        System.out.print("Please choose whether you want to remove all the seats related to your name or not(y/n) : ");
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
        ArrayList<String> orderList = new ArrayList<>(seatingCapacity);
        for(int i=1;i<=seatingCapacity;i++){
            orderList.add(customerNames.get((i)));
        }

        for(int i=0;i<orderList.size();i++){
            for(int j=i+1;j<orderList.size();j++){
                if(orderList.get(i).compareTo(orderList.get(j))>0){
                    String temp = orderList.get(i);
                    orderList.set(i,orderList.get(j));
                    orderList.set(j,temp);
                }
            }
        }
        System.out.println(orderList);
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
