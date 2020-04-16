package CW1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public class Booking extends Application{
    static final int SEATING_CAPACITY = 42;

    public static void main(String[] args){
        launch(args);
    }

    public void consoleMenu(Pane root, Stage stage, Scene scene) {
        Scanner scanner = new Scanner(System.in);
        List<List<String>> colomboBadullaDetails = new ArrayList<>();

        // List to store train number, date booked, start location and destination
        List<String> trainAndDateList = new ArrayList<>(Arrays.asList("0","0","0","0"));

        List<String > stationStops = Arrays.asList("Colombo Fort" , "Polgahawela", "Peradeniya Junction", "Gampola",
                "Nawalapitiya", "Hatton", "Talawakelle", "Nanu Oya", "Haputale", "Diyatalawa", "Bandarawela",
                "Ella", "Badulla");

        while(true){
            System.out.println(
                    "\nWelcome To Sri Lanka Railways Department\n" +
                    "Denuwara Menike Intercity Express Train departure from Colombo to Badulla / Badulla to Colombo\n"+
                    "\nPlease enter 'A' to add a customer to a seat\n" +
                    "Please enter 'V' to view all seats\n" +
                    "Please enter 'E' to display empty seats\n" +
                    "Please enter 'D' to delete customer from seat\n" +
                    "Please enter 'F' to find the seat for a given customer name\n" +
                    "Please enter 'S' to store the booking details to a file or database\n" +
                    "Please enter 'L' to load the booking details from a file or database\n" +
                    "Please enter 'O' to view seats ordered " +
                    "alphabetically by customer name\n" +
                    "Please enter 'Q' to quit the program");

            String userInput = scanner.next().toUpperCase();
            switch(userInput){
                // A welcomescreen is presented to select train route and date before going to add, view or empty
                // and throws an error if nothing was selected
                case "A":
                    welcomeScreen(stage, userInput, trainAndDateList, stationStops);
                    if(trainAndDateList.contains(null) || trainAndDateList.get(2).equals(trainAndDateList.get(3))){
                        errorAlert(trainAndDateList);
                        break;
                    }
                    addCustomerToSeat(root, stage, scene, userInput, trainAndDateList, stationStops, colomboBadullaDetails);
                    break;
                case "V":
                    welcomeScreen(stage, userInput, trainAndDateList, stationStops);
                    if(trainAndDateList.contains(null) || trainAndDateList.get(2).equals(trainAndDateList.get(3))){
                        errorAlert(trainAndDateList);
                        break;
                    }
                    viewAllSeats(root, stage, scene, userInput, trainAndDateList, stationStops, colomboBadullaDetails);
                    break;
                case "E":
                    welcomeScreen(stage, userInput, trainAndDateList, stationStops);
                    if(trainAndDateList.contains(null) || trainAndDateList.get(2).equals(trainAndDateList.get(3))){
                        errorAlert(trainAndDateList);
                        break;
                    }
                    displayEmptySeats(root, stage, scene, userInput, trainAndDateList, stationStops, colomboBadullaDetails);
                    break;
                case "D":
                    deleteCustomer(colomboBadullaDetails);
                    break;
                case "F":
                    findCustomer(colomboBadullaDetails);
                    break;
                case "S":
                    saveToDatabase(colomboBadullaDetails);
                    break;
                case "L":
                    loadFromDatabase(colomboBadullaDetails);
                    break;
                case "O":
                    orderCustomerNames(colomboBadullaDetails);
                    break;
                case "Q":
                    System.exit(0);
                default:
                    System.out.println("Invalid input! Please enter a valid input");
                    break;
            }
        }
    }

    @Override
    public void start(Stage primaryStage){
        Stage stage = new Stage();
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1000, 500);
        stage.setTitle("Train Seat Booking Application");

        Label title = new Label("Welcome to Sri Lanka Railways Department");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label(
                "Train name - Denuwara Menike\n" +
                "Train number - 1001 / 1002\n" +
                "Train route - Colombo to Badulla / Badulla to Colombo\n" +
                "Departure time - 06:45AM / 07:20AM\n" +
                "Arrival  time - 03:27PM / 04:03PM\n" +
                "Class - 1st Class A/C Compartment\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(500);
        details.setLayoutY(100);

        root.getChildren().addAll(title, details);
        // Stage, scene and pane passed to menu to be used in add, view and empty
        consoleMenu(root, stage, scene);
    }

    public Label createSeat(int row, int column, int seatNumber){
        int xCord = 60;
        int yCord = 60;
        Label seat = new Label("S-"+(seatNumber));
        seat.setPrefSize(50, 50);
        // Passed column number is multiplied with XCord to create seats on the x-axis
        seat.setLayoutX(column * xCord);
        // Passed row number is multiplied with YCord to create seats on the y-axis
        seat.setLayoutY(row * yCord);
        seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; " +
                "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        return seat;
    }

    public void welcomeScreen(Stage stage, String userInput, List<String> trainAndDateList, List<String> stationStops){
        Label title = new Label("Welcome to Sri Lanka Railways Department");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label("Train name - Denuwara Menike\n" +
                "Class - 1st Class A/C Compartment\n" +
                "Train number - Colombo to Badulla (1001)\n" +
                "Train number - Badulla to Colombo (1002)\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(220);
        details.setLayoutY(100);

        // Dropdown list of stops from the starting location
        ComboBox<String> startStation = new ComboBox<>();
        startStation.setPromptText("From");
        startStation.setPrefSize(150, 40);
        startStation.setLayoutX(220);
        startStation.setLayoutY(250);

        // Dropdown list of stops from the destination
        ComboBox<String> endStation = new ComboBox<>();
        endStation.setPromptText("To");
        endStation.setPrefSize(150, 40);
        endStation.setLayoutX(420);
        endStation.setLayoutY(250);

        // Add each stations to each ComboBox
        for(String item : stationStops){
            startStation.getItems().add(item);
            endStation.getItems().add(item);
        }

        // Default value set to the systems local date
        DatePicker selectDate = new DatePicker(LocalDate.now());
        selectDate.setLayoutX(310);
        selectDate.setLayoutY(320);
        // Making the manual entry of dates unavailable which helps validation
        selectDate.setEditable(false);

        /*  Restricting past dates that can be selected from the DatePicker UI element compared to the local date of
            the system  */
        selectDate.setDayCellFactory(restrictDate -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty){
                super.updateItem(item, empty);
                LocalDate presentDay = LocalDate.now();

                // If the date in DatePicker is older than current local date disable that particular date
                if(item.compareTo(presentDay)<0 && userInput.equals("A")) {
                    setDisable(true);
                    setStyle("-fx-background-color: red");
                }
            }
        });

        Label information = new Label("Please select the boarding station and destination below.");
        information.setStyle("-fx-font: 16 arial; -fx-text-fill: black;");
        information.setLayoutX(200);
        information.setLayoutY(200);

        Button confirmDestination = new Button("Confirm Destination");
        confirmDestination.setPrefSize(150, 40);
        confirmDestination.setLayoutX(310);
        confirmDestination.setLayoutY(400);

        /*  When confirm destination button is clicked it checks which route was selected using the index position
            of stationStops list and adds train number, date, starting station and destination to trainDateList
            based on that */
        confirmDestination.setOnAction(event -> {
            String startLocation = startStation.getSelectionModel().getSelectedItem();
            String endLocation = endStation.getSelectionModel().getSelectedItem();
            String bookedDate = selectDate.getValue().toString();

            if(stationStops.indexOf(startLocation)<(stationStops.indexOf(endLocation))) {
                // 1001 train number = Colombo to Badulla
                trainAndDateList.set(0, "1001");
                trainAndDateList.set(1, bookedDate);
                trainAndDateList.set(2, startLocation);
                trainAndDateList.set(3, endLocation);
            }
            else if(stationStops.indexOf(startLocation)>(stationStops.indexOf(endLocation))) {
                // 1002 train number = Badulla to Colombo
                trainAndDateList.set(0, "1002");
                trainAndDateList.set(1, bookedDate);
                trainAndDateList.set(2, startLocation);
                trainAndDateList.set(3, endLocation);
            }
            stage.close();
        });

        Pane root1 = new Pane();
        root1.setStyle("-fx-background-color: #1b87c2");
        Scene scene1 = new Scene(root1, 820, 500);
        root1.getChildren().addAll(title, details, information, startStation, endStation, selectDate, confirmDestination);
        stage.setScene(scene1);
        stage.showAndWait();
    }

    public void errorAlert(List<String> trainAndDateList){
        if(trainAndDateList.contains(null) || trainAndDateList.get(2).equals(trainAndDateList.get(3))){
            Alert invalidStop = new Alert(Alert.AlertType.WARNING);
            invalidStop.setTitle("Invalid Station Stop");
            invalidStop.setHeaderText("Warning! Invalid Station Stop!");
            invalidStop.setContentText("Please select a valid station that is on the Colombo To Badulla route! Try again.");
            invalidStop.showAndWait();
        }
    }

    public String generateTicketNo() {
        Random random = new Random();
        int range = random.nextInt(1000)+1000;
        return "DM"+range;
    }

    public HashMap<Integer, String> displaySeats(Pane root, String userInput, List<String> trainAndDateList,
                                                 List<String> stationStops, List<List<String>> colomboBadullaDetails){
        // Hashmap to store seat and name of customers with a starting capacity of 42 elements
        HashMap<Integer,String> seatList = new HashMap<>(SEATING_CAPACITY);

        int seatNumber = 0;
        // Used to create 6 seat rows in the GUI
        for(int row = 1; row <= 6; row++){
            // Used to create 7 seat columns in the GUI
            for(int column = 1; column <= 7; column++){
                /*  Row and column is passed to createSeat which then multiplies it with the coordinates given inside
                    to create 42 seats in 6 rows and 7 columns */
                Label seat = createSeat(row, column, ++seatNumber);
                // Creates 42 placeholder seats with "nb" as value when the program starts the first time
                if(seatList.size() < SEATING_CAPACITY){
                    seatList.put(seatNumber, "nb"); // nb = Not Booked
                }
                // Checks if the train is going from colombo to badulla
                if(trainAndDateList.get(0).equals("1001")) {
                    // This loop checks if the record is there in the list and adds customer nic, first and surname
                    // values to seatList based on the stops selected by the user
                    for (List<String> detail : colomboBadullaDetails) {
                        for (int item : seatList.keySet()) {
                            // Checks the train number, date and seat is already there and if the selected starting
                            // and ending station are greater/less than what's already booked
                            if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) <= stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) >= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) < stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) <= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) > stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) < stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) > stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) >= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                        }
                    }
                    // This loop is to show on the GUI that if the selected starting and ending station are
                    // less/greater than already booked stations it is available for booking
                    for (List<String> detail : colomboBadullaDetails) {
                        for (int item : seatList.keySet()) {
                            if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) >= stationStops.indexOf(detail.get(7)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) > stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, "nb");
                            } else if (detail.get(0).equals("1001") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) < stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) <= stationStops.indexOf(detail.get(6))) {
                                seatList.put(item, "nb");
                            }
                        }
                    }
                }
                // Checks if the train is going from colombo to badulla
                else if(trainAndDateList.get(0).equals("1002")) {
                    // This loop checks if the record is there in the list and adds customer nic, first and surname
                    // values to seatList based on the stops selected by the user
                    for (List<String> detail : colomboBadullaDetails) {
                        for (int item : seatList.keySet()) {
                            // Checks the train number, date and seat is already there and if the selected starting
                            // and ending station are greater/less than what's already booked
                            if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) >= stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) <= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) > stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) >= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) < stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) > stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                            if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) < stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) <= stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, detail.get(2) + " - " + detail.get(3) + " " + detail.get(4));
                            }
                        }
                    }
                    // This loop is to show on the GUI that if the selected starting and ending station are
                    // less/greater than already booked stations it is available for booking
                    for (List<String> detail : colomboBadullaDetails) {
                        for (int item : seatList.keySet()) {
                            if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) <= stationStops.indexOf(detail.get(7)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) < stationStops.indexOf(detail.get(7))) {
                                seatList.put(item, "nb");
                            } else if (detail.get(0).equals("1002") && detail.get(5).equals(trainAndDateList.get(1)) &&
                                    detail.get(1).equals(String.valueOf(item)) &&
                                    stationStops.indexOf(trainAndDateList.get(2)) > stationStops.indexOf(detail.get(6)) &&
                                    stationStops.indexOf(trainAndDateList.get(3)) >= stationStops.indexOf(detail.get(6))) {
                                seatList.put(item, "nb");
                            }
                        }
                    }
                }
                root.getChildren().add(seat);

                if(userInput.equals("A")){
                    int selectedSeat = seatNumber;
                    // When a Label is clicked on the screen the color is changed and adds a placeholder value to seatList
                    // whether its booked or not
                    seat.setOnMouseClicked(event -> {
                        // If the selected seat on the GUI is not booked when clicked then change its color to red and
                        // add booked to seatList
                        if(seatList.get(selectedSeat).equals("nb")){
                            seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid;" +
                                    "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                            seatList.put(selectedSeat, "b");
                        }
                        // This if condition is used to undo the booked status from previous click but works only once
                        seat.setOnMouseClicked(event1 -> {
                            if(seatList.get(selectedSeat).equals("b")){
                                seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid;" +
                                        "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                                seatList.put(selectedSeat, "nb");
                            }
                        });
                    });
                }
                if(userInput.equals("E")) {
                    // If the seat on the GUI is already booked then change its color to the background color to hide
                    if (!seatList.get(seatNumber).equals("nb")) {
                        seat.setStyle("-fx-background-color: #1b87c2;");
                        seat.setTextFill(Paint.valueOf("#1b87c2"));
                    }
                }
                if(userInput.equals("A") || userInput.equals("V")) {
                    // If the seat on the GUI is already booked then change its color to red
                    if (!seatList.get(seatNumber).equals("nb")) {
                        seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black;" +
                                "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                    }
                }
                // If the seat on the GUI is not booked then change its color to green
                if(seatList.get(seatNumber).equals("nb")){
                    seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid;" +
                            "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold;" +
                            "-fx-text-fill: black;");
                }
            }
        }
        return seatList;
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, String userInput, List<String> trainAndDateList,
                                  List<String> stationStops, List<List<String>> colomboBadullaDetails){
        // Temporary List to store seat and name of customers
        List<List<String>> tempSeatList = new ArrayList<>();

        // Made the repeated code in add, view and empty to be inside displaySeats and it returns a hashmap with
        // seat number and name as key/value pairs
        HashMap<Integer, String> seatList = displaySeats(root, userInput, trainAndDateList, stationStops, colomboBadullaDetails);

        Label firstNameLabel = new Label("First Name : ");
        TextField firstNameText = new TextField();
        firstNameLabel.setLayoutX(500);
        firstNameLabel.setLayoutY(265);
        firstNameLabel.setStyle("-fx-font: 16 arial; -fx-text-fill: black; -fx-font-weight: bold");
        firstNameText.setLayoutX(610);
        firstNameText.setLayoutY(260);
        firstNameText.setPrefSize(200,35);

        Label surNameLabel = new Label("Surname    : ");
        TextField surNameText = new TextField();
        surNameLabel.setLayoutX(500);
        surNameLabel.setLayoutY(305);
        surNameLabel.setStyle("-fx-font: 16 arial; -fx-text-fill: black; -fx-font-weight: bold");
        surNameText.setLayoutX(610);
        surNameText.setLayoutY(300);
        surNameText.setPrefSize(200,35);

        Label nicLabel = new Label("NIC             : ");
        TextField nicText = new TextField();
        nicLabel.setLayoutX(500);
        nicLabel.setLayoutY(345);
        nicLabel.setStyle("-fx-font: 16 arial; -fx-text-fill: black; -fx-font-weight: bold");
        nicText.setLayoutX(610);
        nicText.setLayoutY(340);
        nicText.setPrefSize(200,35);

        // Shows what an available seat looks like as a map legend
        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; " +
                "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

        // Shows what an unavailable seat looks like as a map legend
        Label bookedSeat = new Label("Unavailable");
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid;" +
                "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);

        Button bookButton = new Button("Confirm Booking");
        bookButton.setPrefSize(120,40);
        bookButton.setStyle("-fx-background-color: #2144cf; -fx-border-width: 1.5; -fx-border-radius: 3;" +
                "-fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold;" +
                "-fx-text-fill: black; -fx-background-insets: 0");
        bookButton.setLayoutX(560);
        bookButton.setLayoutY(410);

        Button clearButton = new Button("Clear Seats");
        clearButton.setPrefSize(100,40);
        clearButton.setStyle("-fx-background-color: #bd1520; -fx-border-width: 1.5; -fx-border-radius: 3;" +
                "-fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold;" +
                "-fx-text-fill: black; -fx-background-insets: 0");
        clearButton.setLayoutX(710);
        clearButton.setLayoutY(410);

        root.getChildren().addAll(firstNameLabel, firstNameText, surNameLabel, surNameText, nicLabel, nicText, emptySeat, bookedSeat, bookButton, clearButton);

        bookButton.setOnAction(event -> {
            String nic = nicText.getText();
            String firstName = firstNameText.getText();
            String surName = surNameText.getText();

            // NIC input validated so that the user can enter 9 or 12 numbered characters and firstname and surname
            // should be alphabetic characters with or without 1 space between them allowed
            if((!nic.trim().isEmpty() && !firstName.trim().isEmpty() && !surName.trim().isEmpty()) &&
                    (nic.length()==9 || nic.length()==12) && (nic.matches("[0-9]+")) &&
                    (firstName.matches("[a-zA-Z\\s]+")) && (surName.matches("[a-zA-Z\\s]+"))){

                // This loop is created so that multiple seats booked to 1 person are 1st stored inside a templist
                // before adding to the main list
                for(int item : seatList.keySet()){
                    if(seatList.get(item).equals("b")) {
                        seatList.put(item, nic + " - " + firstName + " " + surName);
                        List<String> tempInnerSeatList = new ArrayList<>();
                        tempInnerSeatList.add(String.valueOf(item));
                        tempInnerSeatList.add(nic);
                        tempInnerSeatList.add(firstName);
                        tempInnerSeatList.add(surName);
                        tempInnerSeatList.add(generateTicketNo());
                        tempSeatList.add(tempInnerSeatList);
                    }
                }
                // This loop is where all the details stored in the tempsealist are added to the main list
                // in the order of train number, seat number, nic, firstname, surname, date, from, to and ticket number
                for(List<String> tempInnerSeatList : tempSeatList){
                    List<String> newRecord = new ArrayList<>();
                    // Train route
                    newRecord.add(trainAndDateList.get(0));
                    // Seat number
                    newRecord.add(tempInnerSeatList.get(0));
                    // NIC
                    newRecord.add(tempInnerSeatList.get(1));
                    // FirstName
                    newRecord.add(tempInnerSeatList.get(2));
                    // SurName
                    newRecord.add(tempInnerSeatList.get(3));
                    // Date
                    newRecord.add(trainAndDateList.get(1));
                    // Start Location
                    newRecord.add(trainAndDateList.get(2));
                    // Destination
                    newRecord.add(trainAndDateList.get(3));
                    // Ticket number
                    newRecord.add(tempInnerSeatList.get(4));
                    //List with all customer details
                    colomboBadullaDetails.add(newRecord);
                }
                // Clears  the templist so the same booked seats are not added again in the next time add method
                // is called
                tempSeatList.clear();

                // Throws a confirmation alert for successfully booking seats
                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setTitle("Successful Booking");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Successful Booking!");
                confirmation.showAndWait();
            }
            else{
                // Throws a warning alert for entering invalid details
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Invalid Details");
                warning.setHeaderText("Invalid Details Entered!");
                warning.setContentText("Please enter only letters for First Name and Surname! And only numbers for NIC with upto 9-12 characters!");
                warning.showAndWait();
            }
            nicText.clear();
            firstNameText.clear();
            surNameText.clear();
        });

        // If clear seats button is clicked it removes all the selected seats and sets them to not booked and closes the window
        clearButton.setOnAction(event -> {
            Alert clearSeats = new Alert(Alert.AlertType.INFORMATION);
            clearSeats.setTitle("Clear Seats");
            clearSeats.setHeaderText("You are removing selected seats for this session!");
            clearSeats.setContentText("To remove a seat that is already booked with a name please select option 'D' from the menu.");

            for(int item : seatList.keySet()) {
                if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()){
                    seatList.put(item, "nb");
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

        // If the window is closed while some seats were selected and confirm booking or clear seats was not selected then remove selected seats and set them to not booked
        for(int item : seatList.keySet()) {
            if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()) {
                seatList.put(item,"nb");
            }
        }
        // Removes the buttons and Available seat legend so that if the user clicks on view or empty after this it doesn't overlap with other UI elements
        root.getChildren().removeAll(root, emptySeat, bookedSeat, firstNameLabel, firstNameText, surNameLabel, surNameText, nicLabel, nicText, bookButton, clearButton);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, String userInput, List<String> trainAndDateList,
                             List<String> stationStops, List<List<String>> colomboBadullaDetails){

        // Made the repeated code in add, view and empty to be inside displaySeats
        displaySeats(root, userInput, trainAndDateList, stationStops, colomboBadullaDetails);

        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid;" +
                "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

        Label bookedSeat = new Label("Unavailable");
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; " +
                "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);

        root.getChildren().addAll(emptySeat, bookedSeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().removeAll(root, emptySeat, bookedSeat);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, String userInput, List<String> trainAndDateList,
                                  List<String> stationStops, List<List<String>> colomboBadullaDetails){

        // Made the repeated code in add, view and empty to be inside displaySeats
        displaySeats(root, userInput, trainAndDateList, stationStops, colomboBadullaDetails);

        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black;" +
                "-fx-alignment: center;-fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

        root.getChildren().add(emptySeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().removeAll(root,emptySeat);
    }

    public void deleteCustomer(List<List<String>> colomboBadullaDetails){
        Scanner scanner = new Scanner(System.in);
        List<List<String>> deletedRecords = new ArrayList<>();
        boolean found = false;

        System.out.print("\nPlease enter a valid date you want to remove seats from (YYYY-MM-DD) : ");
        String date = scanner.nextLine();
        System.out.print("\nPlease enter the NIC of the customer you want to remove : ");
        String nic = scanner.nextLine();

        // Checks for the user entered details in the main list
        for (List<String> details : colomboBadullaDetails) {
            if (details.get(5).equals(date) && details.get(2).equals(nic)) {
                deletedRecords.add(details);
                found = true;
            }
        }
        if(found) {
            // Removes the records that has the date and name which match user input from the list
            colomboBadullaDetails.removeIf(details -> details.contains(date) && details.contains(nic));
            for (List<String> details : deletedRecords) {
                System.out.println(
                        "\nTrain      : " + details.get(0) +
                                "\nSeat       : " + details.get(1) +
                                "\nNIC        : " + details.get(2) +
                                "\nFirst Name : " + details.get(3) +
                                "\nSurname    : " + details.get(4) +
                                "\nDate       : " + details.get(5) +
                                "\nFrom       : " + details.get(6) +
                                "\nTo         : " + details.get(7) +
                                "\nTicket     : " + details.get(8));
            }
            deletedRecords.clear();
        }
        else{
            System.out.println("\nEntered details are not available or invalid. Please try again.");
        }
    }

    public void findCustomer(List<List<String>> colomboBadullaDetails){
        Scanner scanner = new Scanner(System.in);
        boolean found = false;

        System.out.print("Please enter the NIC of the customer to find the related seat booked : ");
        String nic = scanner.nextLine();

        // Checks for the user entered details in the main list
        for(List<String> details : colomboBadullaDetails){
            if(details.get(2).equals(nic)){
                found = true;
                break;
            }
        }
        if(found) {
            for (List<String> details : colomboBadullaDetails) {
                if (details.contains(nic)) {
                    System.out.println(
                            "\nTrain      : " + details.get(0) +
                                    "\nSeat       : " + details.get(1) +
                                    "\nNIC        : " + details.get(2) +
                                    "\nFirst Name : " + details.get(3) +
                                    "\nSurname    : " + details.get(4) +
                                    "\nDate       : " + details.get(5) +
                                    "\nFrom       : " + details.get(6) +
                                    "\nTo         : " + details.get(7) +
                                    "\nTicket     : " + details.get(8));
                }
            }
        }
        else{
            System.out.println("\nEntered details are not available or invalid. Please try again.");
        }
    }

    public void saveToDatabase(List<List<String>> colomboBadullaDetails){
        //Connecting to MongoDB then creating a database and then a collection
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> customerCollection = trainDatabase.getCollection("customerDetails");
        System.out.println("Connected to the Database");

        // Checks if the documents in the collection has any documents
        if(customerCollection.countDocuments() == 0){
            // Loops through each inner list in colomboBadullaDetails to get all the details stored
            for (List<String> details : colomboBadullaDetails) {
                Document customerDocument = new Document();
                customerDocument.append("train", details.get(0));
                customerDocument.append("seat", details.get(1));
                customerDocument.append("NIC", details.get(2));
                customerDocument.append("firstname", details.get(3));
                customerDocument.append("surname", details.get(4));
                customerDocument.append("date", details.get(5));
                customerDocument.append("from", details.get(6));
                customerDocument.append("to", details.get(7));
                customerDocument.append("ticket", details.get(8));
                customerCollection.insertOne(customerDocument);
            }
        }
        // Checks if the documents stored in the collection has 1 or more documents
        else if(customerCollection.countDocuments() > 0){
            // Gets all the documents in customerCollection into findCustomerDocument
            FindIterable<Document> findCustomerDocument = customerCollection.find();

            // Loops through each document in findCustomerDocument and deletes them
            for(Document document : findCustomerDocument){
                customerCollection.deleteOne(document);
            }
            // Loops through each inner list in colomboCustomers to get all the train details
            for(List<String> details : colomboBadullaDetails){
                Document customerDocument = new Document();
                customerDocument.append("train", details.get(0));
                customerDocument.append("seat", details.get(1));
                customerDocument.append("NIC", details.get(2));
                customerDocument.append("firstname", details.get(3));
                customerDocument.append("surname", details.get(4));
                customerDocument.append("date", details.get(5));
                customerDocument.append("from", details.get(6));
                customerDocument.append("to", details.get(7));
                customerDocument.append("ticket", details.get(8));
                customerCollection.insertOne(customerDocument);
            }
        }
        mongoClient.close();
        System.out.println("Saved the details to the database successfully");
    }

    public void loadFromDatabase(List<List<String>> colomboBadullaDetails){
        //Connecting to MongoDB then creating a database and then a collection
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> customerCollection = trainDatabase.getCollection("customerDetails");
        System.out.println("Connected to the Database");

        // Gets all the documents in customerCollection train route into findCustomerDocument
        FindIterable<Document> findCustomerDocument = customerCollection.find();

        colomboBadullaDetails.clear();
        // Loops through each document in findCustomerDocument and adds each value from the keys
        // to the colomboBadullaDetails
        for(Document document : findCustomerDocument){
            List<String> details = new ArrayList<>();
            details.add(document.getString("train"));
            details.add(document.getString("seat"));
            details.add(document.getString("NIC"));
            details.add(document.getString("firstname"));
            details.add(document.getString("surname"));
            details.add(document.getString("date"));
            details.add(document.getString("from"));
            details.add(document.getString("to"));
            details.add(document.getString("ticket"));
            colomboBadullaDetails.add(details);
        }
        mongoClient.close();
        System.out.println("Details loaded from the database successfully");
        System.out.println(colomboBadullaDetails);
    }

    public void orderCustomerNames(List<List<String>> colomboBadullaCustomers){
        // Create new List to store name and seat from the main list
        List<String> orderedList = new ArrayList<>();

        // Adds the first name, surname and seat number to orderedList
        for (List<String> details : colomboBadullaCustomers) {
            orderedList.add(details.get(3) + " " + details.get(4) + " - " +details.get(1));
        }
        System.out.println("\nCustomer names ordered based on first come first served basis\n");
        for (String item : orderedList) {
            System.out.println(item);
        }

        // Bubble sort algorithm used to sort each item in orderedList in the ascending order
        for (int i = 0; i < orderedList.size(); i++) {
            for (int j = i + 1; j < orderedList.size(); j++) {
                // Checks if the value of index i is less than the value of index j and if it is smaller
                // then it goes inside the loop
                if (orderedList.get(i).compareTo(orderedList.get(j)) > 0) {
                    // Stores the value of index i in hold which is less than j
                    String hold = orderedList.get(i);
                    // Sets the index i to the value value of index j
                    orderedList.set(i, orderedList.get(j));
                    // Sets the  index of j to the value of hold
                    orderedList.set(j, hold);
                }
            }
        }
        System.out.println("\nCustomer names ordered in the ascending order\n");
        for (String item : orderedList) {
            System.out.println(item);
        }
        orderedList.clear();
    }
}
