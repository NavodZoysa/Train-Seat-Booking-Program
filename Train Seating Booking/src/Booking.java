import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;

public class Booking extends Application{
    static final int seatingCapacity = 42;

    public static void main(String[] args){
        launch(args);
    }

    /**
     * Creating stage, scene and pane which is passed to the consoleMenu method.
     * Labels used to give a title and some additional info about train details.
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
        Stage stage = new Stage();
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 820, 500);    // Size of the window
        stage.setTitle("Train Seat Booking Application");

        // Title and details that display on each screen (add, view and empty) except welcome screen
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
        consoleMenu(root, stage, scene);    // Stage, scene and pane passed to menu to be used in add, view and empty
    }

    /**
     * This method is used so that creating 42 seats on the GUI isn't repeated.
     * @param row a number passed so that each seat is created on the y-axis and multiplied by the y-coordinate
     * @param column a number passed so that each seat is created on the x-axis and multiplied by the x-coordinate
     * @param seatNumber number displayed on the seat in the GUI
     * @return a label is returned so that the label can be used outside of this method
     */
    public Label createSeat(int row, int column, int seatNumber){
        int XCord = 60;
        int YCord = 60;
        Label seat = new Label("S-"+(seatNumber));
        seat.setPrefSize(50, 50);
        seat.setLayoutX(column * XCord);
        seat.setLayoutY(row * YCord);
        seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; " +
                "-fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        return seat;
    }

    /**
     * This method is used to display a window that is executed before add, view and empty methods are called to
     * select the train route and destination then to select a future date. Train route is selected from two radio
     * buttons, destination is selected by two ComboBoxes and a future date is selected using DatePicker.
     * @param stage passed from start method
     * @param tempDateLocation a temporary ArrayList used to store train number, booked date, starting location and
     *                             finally destination
     */
    public void welcomeScreen(Stage stage, ArrayList<String> tempDateLocation){
        Label title = new Label("Welcome to Sri Lanka Railways Department");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label("Train name - Denuwara Menike\n"+"Class - 1st Class A/C Compartment\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(250);
        details.setLayoutY(100);

        ComboBox<String> colomboBadullaRoute = new ComboBox<>();
        colomboBadullaRoute.setPromptText("Colombo to Badulla");
        colomboBadullaRoute.setPrefSize(150, 40);
        colomboBadullaRoute.setLayoutX(220);
        colomboBadullaRoute.setLayoutY(250);

        /*
        A string array created to store each stop in the badulla to colombo train route then looped through a
        for loop to be added to the ComboBox created above
        */
        String[] colomboBadullaArray = new String[]{"Polgahawela", "Peradeniya Junction", "Gampola", "Nawalapitiya",
                "Hatton", "Thalawakele", "Nanuoya", "Haputale", "Diyatalawa", "Bandarawela", "Ella", "Badulla"};
        for(String item : colomboBadullaArray){
            colomboBadullaRoute.getItems().add(item);
        }

        ComboBox<String> badullaColomboRoute = new ComboBox<>();
        badullaColomboRoute.setPromptText("Badulla to Colombo");
        badullaColomboRoute.setPrefSize(150, 40);
        badullaColomboRoute.setLayoutX(420);
        badullaColomboRoute.setLayoutY(250);

        /*
        A string array created to store each stop in the badulla to colombo train route then looped through a
        for loop to be added to the ComboBox created above
        */
        String[] badullaColomboArray = new String[]{"Ella", "Bandarawela", "Diyatalawa", "Haputale", "Nanuoya",
                "Thalawakele", "Hatton", "Nawalapitiya", "Gampola", "Peradeniya Junction", "Polgahawela", "Maradana",
                "Colombo Fort"};
        for(String item : badullaColomboArray){
            badullaColomboRoute.getItems().add(item);
        }

        DatePicker selectDate = new DatePicker(LocalDate.now());    // Default value set to the systems local date
        selectDate.setEditable(false);  // Making the manual entry of dates unavailable which helps validation
        selectDate.setLayoutX(310);
        selectDate.setLayoutY(320);

        /*
        Restricting past dates that can be selected from the DatePicker UI element compared to the local date of
        the system
        */
        selectDate.setDayCellFactory(restrictDate -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty){
                super.updateItem(item, empty);
                LocalDate presentDay = LocalDate.now();

                // If the date in DatePicker is older than current local date disable that particular date
                if(item.compareTo(presentDay)<0) {
                    setDisable(true);
                    setStyle("-fx-background-color: red");
                }
            }
        });

        RadioButton colomboStart = new RadioButton("Colombo to Badulla");
        colomboStart.setLayoutX(230);
        colomboStart.setLayoutY(200);

        RadioButton badullaStart = new RadioButton("Badulla to Colombo");
        badullaStart.setLayoutX(430);
        badullaStart.setLayoutY(200);

        //  By adding the two radio buttons to a ToggleGroup you can later validate if one route is selected
        ToggleGroup destinationStart = new ToggleGroup();
        colomboStart.setToggleGroup(destinationStart);
        badullaStart.setToggleGroup(destinationStart);

        Button confirmDestination = new Button("Confirm Destination");
        confirmDestination.setPrefSize(150, 40);
        confirmDestination.setLayoutX(310);
        confirmDestination.setLayoutY(400);

        // If colombo to badulla route is selected it disables being able to select badulla to colombo
        colomboStart.selectedProperty().addListener((observable, oldValue, newValue) -> {
            badullaColomboRoute.setDisable(true);
            colomboBadullaRoute.setDisable(false);
        });

        // If badulla to colombo route is selected it disables being able to select colombo to badulla
        badullaStart.selectedProperty().addListener((observable, oldValue, newValue) -> {
            badullaColomboRoute.setDisable(false);
            colomboBadullaRoute.setDisable(true);
        });

        /*
        When confirm destination button is clicked it checks which route was selected using radio buttons and executes
        the colombo route path or badulla route path, then the selected destination is stored in endLocation and
        selected date from DatePicker to bookedDate. Train number, bookedDate, start location and endLocation is then
        added to the tempDateLocation.
        */
        confirmDestination.setOnAction(event -> {
            if(colomboStart.selectedProperty().getValue().equals(true)){
                String endLocation = colomboBadullaRoute.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocation.set(0,"1001");
                tempDateLocation.set(1,bookedDate);
                tempDateLocation.set(2,"Colombo");
                tempDateLocation.set(3,endLocation);
                stage.close();
            }
            else if(badullaStart.selectedProperty().getValue().equals(true)){
                String endLocation = badullaColomboRoute.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocation.set(0,"1002");
                tempDateLocation.set(1,bookedDate);
                tempDateLocation.set(2,"Badulla");
                tempDateLocation.set(3,endLocation);
                stage.close();
            }
        });

        Pane root1 = new Pane();
        root1.setStyle("-fx-background-color: #1b87c2");
        Scene scene1 = new Scene(root1, 820, 500);
        root1.getChildren().addAll(title, details, colomboStart, badullaStart, colomboBadullaRoute,
                badullaColomboRoute, selectDate, confirmDestination);
        stage.setScene(scene1);
        stage.showAndWait();
    }

    public void trainDestination(Pane root, Stage stage, Scene scene, ArrayList<String> tempDateLocation,
                                 HashMap<Integer,String> tempSeatList, HashMap<Integer,String> seatList,
                                 List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                                 String userInput, List<List<String>> colomboBadullaDetails){
        switch(userInput){
            case "a":
                if(tempDateLocation.get(0).equals("1001")){
                    addCustomerToSeat(root, stage, scene, seatList, colomboCustomers, tempDateLocation,
                            tempSeatList, colomboBadullaDetails);
                }
                else if(tempDateLocation.get(0).equals("1002")){
                    addCustomerToSeat(root, stage, scene, seatList, badullaCustomers, tempDateLocation,
                            tempSeatList, colomboBadullaDetails);
                }
                break;
            case "v":
                if(tempDateLocation.get(0).equals("1001")){
                    viewAllSeats(root, stage, scene, seatList, colomboCustomers, tempDateLocation);
                }
                else if(tempDateLocation.get(0).equals("1002")){
                    viewAllSeats(root, stage, scene, seatList, badullaCustomers, tempDateLocation);
                }
                break;
            case "e":
                if(tempDateLocation.get(0).equals("1001")){
                    displayEmptySeats(root, stage, scene, seatList, colomboCustomers, tempDateLocation);
                }
                else if(tempDateLocation.get(0).equals("1002")){
                    displayEmptySeats(root, stage, scene, seatList, badullaCustomers, tempDateLocation);
                }
                break;
            default:
                break;
        }
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, HashMap<Integer,String> seatList,
                                  List<List<String>> customerDetails,
                                  ArrayList<String> tempDateLocation,
                                  HashMap<Integer,String> tempSeatList,
                                  List<List<String>> colomboBadullaDetails){
        seatList.clear();
        int seatNumber = 0;
        for(int row = 1; row <= 6; row++){
            for(int column = 1; column <= 7; column++){
                Label seat = createSeat(row, column, ++seatNumber);
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb");
                }
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                for(List<String> customerDetail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                int selectedSeat = seatNumber;
                seat.setOnMouseClicked(event -> {
                    if(seatList.get(selectedSeat).equals("nb")){
                        seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                        seatList.put(selectedSeat, "b");
                    }
                    seat.setOnMouseClicked(event1 -> {
                        if(seatList.get(selectedSeat).equals("b")){
                            seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                            seatList.put(selectedSeat, "nb");
                        }
                    });
                });
                if(!seatList.get(selectedSeat).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

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
        root.getChildren().addAll(emptySeat, bookedSeat, bookButton, clearButton);

        bookButton.setOnAction(event -> {
            TextInputDialog customerNameBox = new TextInputDialog();
            customerNameBox.setTitle("Customer name");
            customerNameBox.setHeaderText("Enter the name of the person the seat is booked to");
            customerNameBox.setContentText("Please enter your name (Numbers,'b' and 'nb' not allowed): ");
            Optional<String> customerNameField = customerNameBox.showAndWait();
            customerNameField.ifPresent(name -> {
                if(name.toLowerCase().equals("b") || name.toLowerCase().equals("nb") || !name.matches("[.a-zA-Z\\s]+") || name.trim().isEmpty()){
                    for(int item : seatList.keySet()){
                        if(seatList.get(item).equals("b")){
                            seatList.put(item, "nb");
                        }
                    }
                    Alert invalidName = new Alert(Alert.AlertType.WARNING);
                    invalidName.setTitle("Invalid Name");
                    invalidName.setHeaderText("Warning! Invalid Name Input!");
                    invalidName.setContentText(name+" is not valid! "+"Please enter a valid name when booking a seat (Only letters with or without spaces allowed. Numbers,special characters,'b' and 'nb' is not allowed)! Try again.");
                    invalidName.showAndWait();
                    stage.close();
                }
                for(int item : seatList.keySet()){
                    if(seatList.get(item).equals("b")){
                        seatList.put(item, name);
                        tempSeatList.put(item, name);
                    }
                }
                for(int item : tempSeatList.keySet()){
                    List<String> newRecord = new ArrayList<>();
                    newRecord.add(tempDateLocation.get(1));
                    newRecord.add(tempDateLocation.get(2));
                    newRecord.add(tempDateLocation.get(3));
                    newRecord.add((String.valueOf(item)));
                    newRecord.add(name);
                    customerDetails.add(newRecord);
                    colomboBadullaDetails.add(newRecord);
                }
                tempSeatList.clear();
            });

            Alert emptyName = new Alert(Alert.AlertType.WARNING);
            emptyName.setTitle("No name entered");
            emptyName.setHeaderText("Warning! No name entered or Invalid input!");
            emptyName.setContentText("Please enter a valid name when booking a seat('b' and 'nb' is not allowed)! Try again.");
            for(int item : seatList.keySet()) {
                if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()){
                    seatList.put(item, "nb");
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
        for(int item : seatList.keySet()) {
            if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()) {
                seatList.put(item,"nb");
            }
        }
        root.getChildren().removeAll(root,emptySeat, bookedSeat, bookButton, clearButton);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> seatList,
                             List<List<String>> customerDetails, ArrayList<String> tempDateLocation){
        seatList.clear();
        int seatNumber = 0;
        for(int row = 1; row <= 6; row++){
            for(int column = 1; column <= 7; column++){
                Label seat = createSeat(row, column, ++seatNumber);
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb");
                }
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                for(List<String> customerDetail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                if(!seatList.get(seatNumber).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
                else{
                    seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

        Label bookedSeat = new Label("Unavailable");
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);
        root.getChildren().addAll(emptySeat, bookedSeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().removeAll(root, emptySeat, bookedSeat);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> seatList,
                                  List<List<String>> customerDetails, ArrayList<String> tempDateLocation){
        seatList.clear();
        int seatNumber = 0;
        for(int row = 1; row <= 6; row++){
            for(int column = 1; column <= 7; column++){
                Label seat = createSeat(row, column, ++seatNumber);
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb");
                }
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                for (List<String> customerDetail : customerDetails){
                    for (int item : seatList.keySet()){
                        if (customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                if(!seatList.get(seatNumber).equals("nb")){
                    seat.setStyle("-fx-background-color: #1b87c2;");
                    seat.setTextFill(Paint.valueOf("#1b87c2"));
                }
                else{
                    seat.setStyle("-fx-background-color:GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }

        Label emptySeat = new Label("Available");
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);
        root.getChildren().add(emptySeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().removeAll(root,emptySeat);
    }

    public void deleteCustomer(Scanner scanner, List<List<String>> colomboCustomers,
                               List<List<String>> badullaCustomers, List<List<String>> colomboBadullaDetails){
        String[] choices = new String[3];
        List<List<String>> deletedRecords = new ArrayList<>();
        System.out.print("\nPlease enter which train you want to remove the seats from(Colombo to Badulla(1)/Badulla to Colombo(2)) : ");
        choices[0] = scanner.next();
        System.out.print("\nPlease enter a valid date you want to remove seats from (YYYY-MM-DD) : ");
        choices[1] = scanner.next();
        System.out.print("\nPlease enter the name of the customer you want to remove : ");
        choices[2] = scanner.next();

        if (choices[0].equals("1")) {
            for(List<String> details : colomboCustomers){
                if(details.contains(choices[1]) && details.contains(choices[2])){
                    deletedRecords.add(details);
                }
            }
            colomboCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
            colomboBadullaDetails.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
            System.out.println("\nDetails of deleted customers ");
            for(List<String> details : deletedRecords) {
                System.out.println(
                        "\nName : " + details.get(4) +
                        "\nSeat : " + details.get(3) +
                        "\nDate : " + details.get(0) +
                        "\nFrom : " + details.get(1) +
                        "\nTo   : " + details.get(2));
            }
            deletedRecords.clear();
        }
        else if (choices[0].equals("2")) {
            for(List<String> details : badullaCustomers){
                if(details.contains(choices[1]) && details.contains(choices[2])){
                    deletedRecords.add(details);
                }
            }
            badullaCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
            colomboBadullaDetails.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
            System.out.println("\nDetails of deleted customers ");
            for(List<String> details : deletedRecords) {
                System.out.println(
                        "\nName : " + details.get(4) +
                                "\nSeat : " + details.get(3) +
                                "\nDate : " + details.get(0) +
                                "\nFrom : " + details.get(1) +
                                "\nTo   : " + details.get(2));
            }
            deletedRecords.clear();
        }
        else {
            System.out.println("Invalid input please try again");
        }
    }

    public void findCustomer(Scanner scanner, List<List<String>> colomboCustomers,
                             List<List<String>> badullaCustomers){
        String[] choices = new String[2];
        System.out.print("\nPlease enter which train you want to find the customer from ([1] Colombo to Badulla/[2] Badulla to Colombo /[3] Both) : ");
        choices[0] = scanner.next();
        System.out.print("Please enter the name of the customer to find the related seat booked : ");
        choices[1] = scanner.next();

        switch(choices[0]){
            case "1":
                for(List<String> details : colomboCustomers){
                    if(details.contains(choices[1])){
                        System.out.println(
                                "\nName : " + details.get(4) +
                                "\nSeat : " + details.get(3) +
                                "\nDate : " + details.get(0) +
                                "\nFrom : " + details.get(1) +
                                "\nTo   : " + details.get(2));
                    }
                    else{
                        System.out.println("Invalid customer name or customer not in booked details");
                        break;
                    }
                }
                break;
            case "2":
                for(List<String> details : badullaCustomers){
                    if(details.contains(choices[1])){
                        System.out.println(
                                "\nName : " + details.get(4) +
                                "\nSeat : " + details.get(3) +
                                "\nDate : " + details.get(0) +
                                "\nFrom : " + details.get(1) +
                                "\nTo   : " + details.get(2));
                    }
                    else{
                        System.out.println("Invalid customer name or customer not in booked details");
                        break;
                    }
                }
                break;
            case "3":
                for(List<String> details : colomboCustomers){
                    if(details.contains(choices[1])){
                        System.out.println(
                                "\nName : " + details.get(4) +
                                "\nSeat : " + details.get(3) +
                                "\nDate : " + details.get(0) +
                                "\nFrom : " + details.get(1) +
                                "\nTo   : " + details.get(2));
                    }
                    else{
                        System.out.println("Invalid customer name or customer not in booked details");
                        break;
                    }
                }
                for(List<String> details : badullaCustomers){
                    if(details.contains(choices[1])){
                        System.out.println(
                                "\nName : " + details.get(4) +
                                "\nSeat : " + details.get(3) +
                                "\nDate : " + details.get(0) +
                                "\nFrom : " + details.get(1) +
                                "\nTo   : " + details.get(2));
                    }
                    else{
                        System.out.println("Invalid customer name or customer not in booked details");
                        break;
                    }
                }
                break;
            default:
                System.out.println("Invalid input please try again");
                break;
        }
    }

    public void saveToFile(Scanner scanner, List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                           List<List<String>> colomboBadullaDetails) throws IOException{
        System.out.print("Do you want to save the details to a text file(T) or store it in the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        if(choice.equals("t")){
            FileWriter writer = new FileWriter("src/customerData.txt");
            for(List<String> details : colomboBadullaDetails){
                for(int i = 0; i < 5; i++){
                    if(i == 4){
                        writer.write(details.get(i)+"\n");
                    }
                    else{
                        writer.write(details.get(i) + "/");
                    }
                }
            }
            writer.close();
            System.out.println("Successfully save to file");
        }
        else if(choice.equals("d")){
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> colomboCollection = customerDatabase.getCollection("colomboDetails");
            MongoCollection<Document> badullaCollection = customerDatabase.getCollection("badullaDetails");
            System.out.println("Connected to the Database");

            if(colomboCollection.countDocuments() == 0 || badullaCollection.countDocuments() == 0){
                if(colomboCollection.countDocuments() == 0) {
                    for (List<String> details : colomboCustomers) {
                        Document colomboDocument = new Document();
                        colomboDocument.append("date", details.get(0));
                        colomboDocument.append("from", details.get(1));
                        colomboDocument.append("to", details.get(2));
                        colomboDocument.append("seat", details.get(3));
                        colomboDocument.append("name", details.get(4));
                        colomboCollection.insertOne(colomboDocument);
                    }
                }
                if(badullaCollection.countDocuments() == 0) {
                    for (List<String> details : badullaCustomers) {
                        Document badullaDocument = new Document();
                        badullaDocument.append("date", details.get(0));
                        badullaDocument.append("from", details.get(1));
                        badullaDocument.append("to", details.get(2));
                        badullaDocument.append("seat", details.get(3));
                        badullaDocument.append("name", details.get(4));
                        badullaCollection.insertOne(badullaDocument);
                    }
                }
            }
            else if(colomboCollection.countDocuments() > 0 || badullaCollection.countDocuments() > 0){
                FindIterable<Document> findColomboDocument = colomboCollection.find();
                FindIterable<Document> findBadullaDocument = badullaCollection.find();
                if(colomboCollection.countDocuments() > 0){
                    for(Document document : findColomboDocument){
                        colomboCollection.deleteOne(document);
                    }
                    for(List<String> details : colomboCustomers){
                        Document colomboDocument = new Document();
                        colomboDocument.append("date",details.get(0));
                        colomboDocument.append("from",details.get(1));
                        colomboDocument.append("to",details.get(2));
                        colomboDocument.append("seat",details.get(3));
                        colomboDocument.append("name",details.get(4));
                        colomboCollection.insertOne(colomboDocument);
                    }
                }
                if(badullaCollection.countDocuments() > 0){
                    for(Document document : findBadullaDocument){
                        badullaCollection.deleteOne(document);
                    }
                    for(List<String> details : badullaCustomers){
                        Document badullaDocument = new Document();
                        badullaDocument.append("date",details.get(0));
                        badullaDocument.append("from",details.get(1));
                        badullaDocument.append("to",details.get(2));
                        badullaDocument.append("seat",details.get(3));
                        badullaDocument.append("name",details.get(4));
                        badullaCollection.insertOne(badullaDocument);
                    }
                }
            }
            System.out.println(colomboBadullaDetails);
            System.out.println(colomboCustomers);
            System.out.println(badullaCustomers);
            mongoClient.close();
            System.out.println("Saved the details to the database successfully");

        }
        else{
            System.out.println("Invalid input. Please try again.");
        }
    }

    public void loadFromFile(Scanner scanner, List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                             List<List<String>> colomboBadullaDetails) throws FileNotFoundException{
        System.out.print("Do you want to load the details from the text file(T) or retrieve it from the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        if(choice.equals("t")){
            Scanner read = new Scanner(new File("src/customerData.txt"));
            while (read.hasNextLine()){
                String line = read.nextLine();
                String[] holdDetails = line.split("/");
                List<String> details = new ArrayList<>(Arrays.asList(holdDetails).subList(0, 5));
                colomboBadullaDetails.add(details);
            }
            for(List<String> details : colomboBadullaDetails){
                if(details.get(1).contains("Colombo")){
                    colomboCustomers.add(details);
                }
                else if(details.get(1).contains("Badulla")){
                    badullaCustomers.add(details);
                }
            }
            read.close();
            System.out.println("Successfully loaded from file");
        }
        else if(choice.equals("d")){
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> colomboCollection = customerDatabase.getCollection("colomboDetails");
            MongoCollection<Document> badullaCollection = customerDatabase.getCollection("badullaDetails");
            System.out.println("Connected to the Database");

            FindIterable<Document> findColomboDocument = colomboCollection.find();
            FindIterable<Document> findBadullaDocument = badullaCollection.find();
            for(Document document : findColomboDocument){
                List<String> details = new ArrayList<>();
                details.add(document.getString("date"));
                details.add(document.getString("from"));
                details.add(document.getString("to"));
                details.add(document.getString("seat"));
                details.add(document.getString("name"));
                colomboCustomers.add(details);
                colomboBadullaDetails.add(details);
            }
            for(Document document : findBadullaDocument){
                List<String> details = new ArrayList<>();
                details.add(document.getString("date"));
                details.add(document.getString("from"));
                details.add(document.getString("to"));
                details.add(document.getString("seat"));
                details.add(document.getString("name"));
                badullaCustomers.add(details);
                colomboBadullaDetails.add(details);
            }
            System.out.println(colomboBadullaDetails);
            System.out.println(colomboCustomers);
            System.out.println(badullaCustomers);
            mongoClient.close();
            System.out.println("Details loaded from the database successfully");
        }
        else{
            System.out.println("Invalid input. Please try again.");
        }
    }

    public void orderCustomerNames(Scanner scanner, List<List<String>> colomboCustomers,
                                   List<List<String>> badullaCustomers){
        List<String> orderedList = new ArrayList<>();
        System.out.print("\nPlease enter which train you want to sort the seats according to customers from([1] Colombo to Badulla/[2] Badulla to Colombo) : ");
        String choice = scanner.next();
        if(choice.equals("1")){
            for(List<String> details : colomboCustomers){
                orderedList.add(details.get(4)+" - "+details.get(3));
            }
            System.out.println("\nCustomer names ordered based on first come first served basis\n");
            for(String item : orderedList){
                System.out.println(item);
            }
            for(int i = 0; i < orderedList.size(); i++){
                for(int j = i + 1; j < orderedList.size(); j++){
                    if(orderedList.get(i).compareTo(orderedList.get(j))>0){
                        String hold = orderedList.get(i);
                        orderedList.set(i, orderedList.get(j));
                        orderedList.set(j, hold);
                    }
                }
            }
            System.out.println("\nCustomer names ordered in the ascending order\n");
            for(String item : orderedList){
                System.out.println(item);
            }
            orderedList.clear();
        }
        else if(choice.equals("2")){
            for(List<String> details : badullaCustomers){
                orderedList.add(details.get(4)+" - "+details.get(3));
            }
            System.out.println("\nCustomer names ordered based on first come first served basis\n");
            for(String item : orderedList){
                System.out.println(item);
            }
            for(int i = 0; i < orderedList.size(); i++){
                for(int j = i + 1; j < orderedList.size(); j++){
                    if(orderedList.get(i).compareTo(orderedList.get(j))>0){
                        String hold = orderedList.get(i);
                        orderedList.set(i, orderedList.get(j));
                        orderedList.set(j, hold);
                    }
                }
            }
            System.out.println("\nCustomer names ordered in the ascending order\n");
            for(String item : orderedList){
                System.out.println(item);
            }
            orderedList.clear();
        }
    }

    /**
     * The other main method that is needed to run the whole program where the stage, scene and pane are passed from the
     * start method. Six data structures are created which are been used throughout the whole program. Relevant methods
     * are called for each option except for add, view and empty where it goes to the welcomeScreen method first
     * where a window is opened to select the train route, destination and date, which then goes to trainDestination
     * method
     * @param root asd
     * @param stage asd
     * @param scene asd
     * @throws IOException asd
     */
    public void consoleMenu(Pane root, Stage stage, Scene scene) throws IOException{
        Scanner scanner = new Scanner(System.in);

        // List created to store both train details
        List<List<String>> colomboBadullaDetails = new ArrayList<>();

        // Hashmap to store seat and name of customers with a starting capacity of 42 elements
        HashMap<Integer,String> seatList = new HashMap<>(seatingCapacity);

        // Temporary hashmap to store seat and name of customers with a starting capacity of 42 elements
        HashMap<Integer,String> tempSeatList = new HashMap<>(seatingCapacity);

        // List created to store colombo customer details
        List<List<String>> colomboCustomers = new ArrayList<>();

        // List created to store badulla customer details
        List<List<String>> badullaCustomers = new ArrayList<>();

        // Temporary ArrayList to store train number, date booked, start location and destination
        ArrayList<String> tempDateLocation = new ArrayList<>(Arrays.asList("0","0","0","0"));

        // Main loop the program runs on
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
                    "Please enter 'O' to view seats ordered alphabetically by customer name\n" +
                    "Please enter 'Q' to quit the program");

            String userInput = scanner.next().toLowerCase();
            switch(userInput){
                case "a":
                case "v":
                case "e":
                    welcomeScreen(stage, tempDateLocation);
                    trainDestination(root, stage, scene, tempDateLocation, tempSeatList, seatList,
                            colomboCustomers, badullaCustomers, userInput, colomboBadullaDetails);
                    break;
                case "d":
                    deleteCustomer(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "f":
                    findCustomer(scanner, colomboCustomers, badullaCustomers);
                    break;
                case "s":
                    saveToFile(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "l":
                    loadFromFile(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "o":
                    orderCustomerNames(scanner, colomboCustomers, badullaCustomers);
                    break;
                case "q":
                    System.exit(0);
                default:
                    System.out.println("Invalid input! Please enter a valid input");
                    break;
            }
        }
    }
}
