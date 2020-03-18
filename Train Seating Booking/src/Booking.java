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
        Scene scene = new Scene(root, 1000, 500);    // Size of the window
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
     * @param row   a number passed so that each seat is created on the y-axis and multiplied by the y-coordinate
     * @param column    a number passed so that each seat is created on the x-axis and multiplied by the x-coordinate
     * @param seatNumber    number displayed on the seat in the GUI
     * @return  a label is returned so that the label can be used outside of this method
     */
    public Label createSeat(int row, int column, int seatNumber){
        int XCord = 60;
        int YCord = 60;
        Label seat = new Label("S-"+(seatNumber));
        seat.setPrefSize(50, 50);

        // Passed column number is multiplied with XCord to create seats on the x-axis
        seat.setLayoutX(column * XCord);

        // Passed row number is multiplied with YCord to create seats on the y-axis
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
     * @param tempDateLocation  a temporary ArrayList used to store train number, booked date, starting location and
     *                          finally destination
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

        ComboBox<String> colomboBadullaRoute = new ComboBox<>();    // Dropdown list of stops from colombo to badulla
        colomboBadullaRoute.setPromptText("Colombo to Badulla");
        colomboBadullaRoute.setPrefSize(150, 40);
        colomboBadullaRoute.setLayoutX(220);
        colomboBadullaRoute.setLayoutY(250);

        /*  A string array created to store each stop in the badulla to colombo train route then looped through a
            for loop to be added to the ComboBox created above */
        String[] colomboBadullaArray = new String[]{"Polgahawela", "Peradeniya Junction", "Gampola", "Nawalapitiya",
                "Hatton", "Thalawakele", "Nanuoya", "Haputale", "Diyatalawa", "Bandarawela", "Ella", "Badulla"};
        for(String item : colomboBadullaArray){
            colomboBadullaRoute.getItems().add(item);
        }

        ComboBox<String> badullaColomboRoute = new ComboBox<>();    // Dropdown list of stops from badulla to colombo
        badullaColomboRoute.setPromptText("Badulla to Colombo");
        badullaColomboRoute.setPrefSize(150, 40);
        badullaColomboRoute.setLayoutX(420);
        badullaColomboRoute.setLayoutY(250);

        /*  A string array created to store each stop in the badulla to colombo train route then looped through a
            for loop to be added to the ComboBox created above  */
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

        /*  Restricting past dates that can be selected from the DatePicker UI element compared to the local date of
            the system  */
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

        RadioButton colomboStart = new RadioButton("Colombo to Badulla");   // Colombo to badulla route select
        colomboStart.setLayoutX(230);
        colomboStart.setLayoutY(200);

        RadioButton badullaStart = new RadioButton("Badulla to Colombo");   // Badulla to colombo route select
        badullaStart.setLayoutX(430);
        badullaStart.setLayoutY(200);

        //  By adding the two radio buttons to a ToggleGroup you can later validate if one route is selected
        ToggleGroup destinationStart = new ToggleGroup();
        colomboStart.setToggleGroup(destinationStart);
        badullaStart.setToggleGroup(destinationStart);

        Button confirmDestination = new Button("Confirm Destination");  // Confirm selected date,route and destination
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

        /*  When confirm destination button is clicked it checks which route was selected using radio buttons and
            executes the colombo route path or badulla route path, then the selected destination is stored in
            endLocation and selected date from DatePicker to bookedDate. Train number, bookedDate, start location
            and endLocation is then added to the tempDateLocation.  */
        confirmDestination.setOnAction(event -> {
            if(colomboStart.selectedProperty().getValue().equals(true)){
                String endLocation = colomboBadullaRoute.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocation.set(0,"1001"); // 1001 train number = Colombo to Badulla
                tempDateLocation.set(1,bookedDate);
                tempDateLocation.set(2,"Colombo");
                tempDateLocation.set(3,endLocation);
                stage.close();
            }
            else if(badullaStart.selectedProperty().getValue().equals(true)){
                String endLocation = badullaColomboRoute.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocation.set(0,"1002"); // 1002 train number = Badulla to Colombo
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

    /**
     * This method is used to pass the relevant details of route, destination and date taken from welcomeScreen into
     * add, view and empty based on what the user inputted in the consoleMenu method.
     * @param root  Pane passed from start
     * @param stage passed from start
     * @param scene passed from start
     * @param userInput user input taken from consoleMenu method
     * @param tempDateLocation  a temporary ArrayList used to store train number, booked date, starting location and
     *                          finally destination
     * @param seatList  the main HashMap used to create seats in the GUI and adds 42 seat numbers as keys and
     *                  placeholder values whether its booked or not
     * @param tempSeatList  a temporary HashMap used to store multiple seats selected before adding to the
     *                      colomboCustomers or badullaCustomers
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number
     *                          and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number
     *                          and Name) using badulla to colombo train route
     * @param colomboBadullaDetails a List with both details of customers from colombo to badulla and back stored
     */
    public void trainDestination(Pane root, Stage stage, Scene scene, String userInput, ArrayList<String> tempDateLocation,
                                 HashMap<Integer,String> seatList, HashMap<Integer,String> tempSeatList,
                                 List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                                 List<List<String>> colomboBadullaDetails){

        /*  Switch case checks for user input taken from console then calls to add, view or empty methods based on
            selected route */
        switch(userInput){
            case "a":
                // If add seats method is called execute this block of code
                // 1001 train number = Colombo to Badulla
                // If colombo to badulla route is selected then colomboCustomers list is passed
                if(tempDateLocation.get(0).equals("1001")){
                    addCustomerToSeat(root, stage, scene, tempDateLocation, seatList, tempSeatList, colomboCustomers,
                             colomboBadullaDetails);
                }
                // 1002 train number = Badulla to Colombo
                // If badulla to colombo route is selected then badullaoCustomers list is passed
                else if(tempDateLocation.get(0).equals("1002")){
                    addCustomerToSeat(root, stage, scene, tempDateLocation, seatList, tempSeatList, badullaCustomers,
                            colomboBadullaDetails);
                }
                break;
            case "v":
                // If view seats method is called execute this block of code
                if(tempDateLocation.get(0).equals("1001")){
                    viewAllSeats(root, stage, scene, tempDateLocation, seatList, colomboCustomers);
                }
                else if(tempDateLocation.get(0).equals("1002")){
                    viewAllSeats(root, stage, scene, tempDateLocation, seatList, badullaCustomers);
                }
                break;
            case "e":
                // If view empty seats method is called execute this block of code
                if(tempDateLocation.get(0).equals("1001")){
                    displayEmptySeats(root, stage, scene, tempDateLocation, seatList, colomboCustomers);
                }
                else if(tempDateLocation.get(0).equals("1002")){
                    displayEmptySeats(root, stage, scene, tempDateLocation, seatList, badullaCustomers);
                }
                break;
            default:
                break;
        }
    }

    /**
     *  This method takes createSeat method to create 42 seats on the GUI and adds customer details (Date,
     *  Start location, Destination, Seat number and Name) to either colomboCustomers and badullaCustomers based on the
     *  train route selected.
     * @param root  Pane passed from start
     * @param stage passed from start
     * @param scene passed from start
     * @param tempDateLocation  a temporary ArrayList used to store train number, booked date, starting location and
     *                          finally destination
     * @param seatList  the main HashMap used to create seats in the GUI and adds 42 seat numbers as keys and
     *                  placeholder values whether its booked or not
     * @param tempSeatList  a temporary HashMap used to store multiple seats selected before adding to the
     *                      colomboCustomers or badullaCustomers
     * @param customerDetails   this List can be either colomboCustomer or badullaCustomers List based on the route
     *                          selected
     * @param colomboBadullaDetails a List with both details of customers from colombo to badulla and back stored
     */
    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, ArrayList<String> tempDateLocation,
                                  HashMap<Integer,String> seatList, HashMap<Integer,String> tempSeatList,
                                  List<List<String>> customerDetails, List<List<String>> colomboBadullaDetails){

        seatList.clear(); // If you add seats and then load a previous save this makes sure it does not conflict with previous data
        int seatNumber = 0; // Starts at 0 goes upto inside the loop 42
        for(int row = 1; row <= 6; row++){  // Used to create 6 rows in the GUI
            for(int column = 1; column <= 7; column++){ //  Used to create 7 columns in the GUI
                /*  Row and column is passed to createSeat which then multiplies it with the coordinates given inside
                    to create 42 seats in 6 rows and 7 columns */
                Label seat = createSeat(row, column, ++seatNumber);

                // Creates 42 placeholder seats with "nb" as value when the program starts the first time
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb"); // nb = Not Booked
                }

                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if not it adds
                // a placeholder value "nb" to seatList
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        // If date and seat number doesn't exist in colomboCustomers or badullaCustomers list then
                        // add "nb" to seatList
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if there is
                // adds a placeholder value "b" to seatList
                for(List<String> customerDetail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b"); // b = Booked
                        }
                    }
                }
                root.getChildren().add(seat);

                int selectedSeat = seatNumber;
                // When a Label is clicked on the screen the color is changed and adds a placeholder value to seatList
                // whether its booked or not
                seat.setOnMouseClicked(event -> {
                    // If the selected seat on the GUI is not booked when clicked then change its color to red and
                    // add booked to seatList
                    if(seatList.get(selectedSeat).equals("nb")){
                        seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                        seatList.put(selectedSeat, "b");
                    }
                    // This if condition is used to undo the booked status from previous click but works only once
                    seat.setOnMouseClicked(event1 -> {
                        if(seatList.get(selectedSeat).equals("b")){
                            seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                            seatList.put(selectedSeat, "nb");
                        }
                    });
                });
                // If a seat on the GUI is already booked then change its color to red
                if(!seatList.get(selectedSeat).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        }
        Label emptySeat = new Label("Available"); // Shows what an available seat looks like as a map legend
        emptySeat.setPrefSize(80, 50);
        emptySeat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        emptySeat.setLayoutX(60);
        emptySeat.setLayoutY(440);

        Label bookedSeat = new Label("Unavailable"); // Shows what an unavailable seat looks like as a map legend
        bookedSeat.setPrefSize(80, 50);
        bookedSeat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        bookedSeat.setLayoutX(160);
        bookedSeat.setLayoutY(440);

        Button bookButton = new Button("Confirm Booking"); // Creates confirm button
        bookButton.setPrefSize(120,40);
        bookButton.setStyle("-fx-background-color: #2144cf; -fx-border-width: 1.5; -fx-border-radius: 3; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black; -fx-background-insets: 0");
        bookButton.setLayoutX(500);
        bookButton.setLayoutY(310);

        Button clearButton = new Button("Clear Seats"); // Creates clear button
        clearButton.setPrefSize(100,40);
        clearButton.setStyle("-fx-background-color: #bd1520; -fx-border-width: 1.5; -fx-border-radius: 3; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black; -fx-background-insets: 0");
        clearButton.setLayoutX(650);
        clearButton.setLayoutY(310);
        root.getChildren().addAll(emptySeat, bookedSeat, bookButton, clearButton);

        /* When confirm booking button is selected it shows an alert for the user to enter their name and gets all the
           selected seats added to the tempSeatList which is then added to the colomboCustomers or badullaCustomers */
        bookButton.setOnAction(event -> {
            // A TextInputDialog is used to get the name from the user as an alert
            TextInputDialog customerNameBox = new TextInputDialog();
            customerNameBox.setTitle("Customer name");
            customerNameBox.setHeaderText("Enter the name of the person the seat is booked to");
            customerNameBox.setContentText("Please enter your name (Numbers,'b' and 'nb' not allowed): ");
            Optional<String> customerNameField = customerNameBox.showAndWait();
            // If any text is entered into the TextInputDialog then it goes inside this block of code
            customerNameField.ifPresent(name -> {
                // This if validates if the user entered a letter with/without a single space. "b" and "nb" is not
                // allowed because its used as a placeholder value in seatList and only spaces entered is not allowed
                if(name.toLowerCase().equals("b") || name.toLowerCase().equals("nb") || !name.matches("[.a-zA-Z\\s]+") || name.trim().isEmpty()){
                    // If name is "b", "nb", letters, special characters or just spaces then remove all the selected
                    // seats from the seatList to not booked
                    for(int item : seatList.keySet()){
                        if(seatList.get(item).equals("b")){
                            seatList.put(item, "nb");
                        }
                    }
                    // Throws a warning alert if "b", "nb", letters, special characters or just spaces are entered and
                    // closes the window
                    Alert invalidName = new Alert(Alert.AlertType.WARNING);
                    invalidName.setTitle("Invalid Name");
                    invalidName.setHeaderText("Warning! Invalid Name Input!");
                    invalidName.setContentText(name+" is not valid! "+"Please enter a valid name when booking a seat (Only letters with or without spaces allowed. Numbers,special characters,'b' and 'nb' is not allowed)! Try again.");
                    invalidName.showAndWait();
                    stage.close();
                }
                // If a valid name is entered then add the name to the value of each selected seat in the seatList
                // and add it to the tempSeatList
                for(int item : seatList.keySet()){
                    if(seatList.get(item).equals("b")){
                        seatList.put(item, name);
                        tempSeatList.put(item, name);
                    }
                }
                // After name is added to each selected seat then loop through the tempSeatList and add the details
                // stored in tempDateLocation which is date, start location, destination and each seat number and name
                // stored in the tempSeatList to either colomboCustomers or badullaCustomers List, also add it to the
                // colomboBadullaDetails which contains both colombo and badulla customer details
                for(int item : tempSeatList.keySet()){
                    List<String> newRecord = new ArrayList<>();
                    newRecord.add(tempDateLocation.get(1)); // Date
                    newRecord.add(tempDateLocation.get(2)); // Start Location
                    newRecord.add(tempDateLocation.get(3)); // Destination
                    newRecord.add((String.valueOf(item)));  // Seat number
                    newRecord.add(name);                    // Name
                    customerDetails.add(newRecord);         // Colombo or badulla customer list
                    colomboBadullaDetails.add(newRecord);   // Main list with all customer details
                }
                tempSeatList.clear(); // Remove all the data stored in tempSeatList
            });
            // If no text was entered in the TextInputDialog then it executes the code block below
            Alert emptyName = new Alert(Alert.AlertType.WARNING);
            emptyName.setTitle("No name entered");
            emptyName.setHeaderText("Warning! No name entered or Invalid input!");
            emptyName.setContentText("Please enter a valid name when booking a seat('b' and 'nb' is not allowed)! Try again.");
            // Remove each selected seat from seatList as booked to not booked
            for(int item : seatList.keySet()) {
                if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()){
                    seatList.put(item, "nb");
                    emptyName.showAndWait();
                    stage.close();
                    break;
                }
            }
        });
        // If clear seats button is clicked it removes all the selected seats and sets them to not booked and closes
        // the window
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
        // If the window is closed while some seats were selected and confirm booking or clear seats was not selected
        // then remove selected seats and set them to not booked
        for(int item : seatList.keySet()) {
            if(seatList.get(item).equals("b") || seatList.get(item).isEmpty()) {
                seatList.put(item,"nb");
            }
        }
        // Removes the buttons and Available seat legend so that if the user clicks on view or empty after this it
        // doesn't overlap with other UI elements
        root.getChildren().removeAll(root, emptySeat, bookedSeat, bookButton, clearButton);
    }

    /**
     *  This method is same as the add method but the difference is that the user cannot click on the seats and
     *  the confirm and clear buttons are removed.
     * @param root  Pane passed from start
     * @param stage passed from start
     * @param scene passed from start
     * @param tempDateLocation  a temporary ArrayList used to store train number, booked date, starting location and
     *                          finally destination
     * @param seatList  the main HashMap used to create seats in the GUI and adds 42 seat numbers as keys and
     *                  placeholder values whether its booked or not
     * @param customerDetails   this List can be either colomboCustomer or badullaCustomers List based on the route
     *                          selected
     */
    public void viewAllSeats(Pane root, Stage stage, Scene scene, ArrayList<String> tempDateLocation, HashMap<Integer,String> seatList,
                             List<List<String>> customerDetails){
        seatList.clear(); // If you add seats and then load a previous save this makes sure it does not conflict with previous data
        int seatNumber = 0; // Starts at 0 goes upto inside the loop 42
        for(int row = 1; row <= 6; row++){ // Used to create 6 rows in the GUI
            for(int column = 1; column <= 7; column++){ // Used to create 7 columns in the GUI
                /*  Row and column is passed to createSeat which then multiplies it with the coordinates given inside
                    to create 42 seats in 6 rows and 7 columns */
                Label seat = createSeat(row, column, ++seatNumber);
                // Creates 42 placeholder seats with "nb" as value when the program starts the first time
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb"); // nb = Not Booked
                }
                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if not it adds
                // a placeholder value "nb" to seatList
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        // If date and seat number doesn't exist in colomboCustomers or badullaCustomers list then
                        // add "nb" to seatList
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if there is
                // adds a placeholder value "b" to seatList
                for(List<String> customerDetail : customerDetails){
                    for(int item : seatList.keySet()){
                        if(customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b"); // b = Booked
                        }
                    }
                }
                root.getChildren().add(seat);

                // If the seat on the GUI is already booked then change its color to red
                if(!seatList.get(seatNumber).equals("nb")){
                    seat.setStyle("-fx-background-color: RED; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                }
                // If the seat on the GUI is not booked then change its color to green
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

    /**
     *  This method is same as the view method but the difference is that the user cannot click on the seats and
     *  the booked seats are not displayed as well as the confirm and clear buttons are removed.
     * @param root  Pane passed from start
     * @param stage passed from start
     * @param scene passed from start
     * @param tempDateLocation  a temporary ArrayList used to store train number, booked date, starting location and
     *                          finally destination
     * @param seatList  the main HashMap used to create seats in the GUI and adds 42 seat numbers as keys and
     *                  placeholder values whether its booked or not
     * @param customerDetails   this List can be either colomboCustomer or badullaCustomers List based on the route
     *                          selected
     */
    public void displayEmptySeats(Pane root, Stage stage, Scene scene, ArrayList<String> tempDateLocation, HashMap<Integer,String> seatList,
                                  List<List<String>> customerDetails){
        seatList.clear(); // If you add seats and then load a previous save this makes sure it does not conflict with previous data
        int seatNumber = 0; // Starts at 0 goes upto inside the loop 42
        for(int row = 1; row <= 6; row++){ // Used to create 6 rows in the GUI
            for(int column = 1; column <= 7; column++){ // Used to create 7 columns in the GUI
                /*  Row and column is passed to createSeat which then multiplies it with the coordinates given inside
                    to create 42 seats in 6 rows and 7 columns */
                Label seat = createSeat(row, column, ++seatNumber);
                // Creates 42 placeholder seats with "nb" as value when the program starts the first time
                if(seatList.size() < seatingCapacity){
                    seatList.put(seatNumber, "nb"); // nb = Not Booked
                }
                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if not it adds
                // a placeholder value "nb" to seatList
                for(List<String> detail : customerDetails){
                    for(int item : seatList.keySet()){
                        // If date and seat number doesn't exist in colomboCustomers or badullaCustomers list then
                        // add "nb" to seatList
                        if(!detail.contains(tempDateLocation.get(1)) && !detail.contains(String.valueOf(item))){
                            seatList.put(seatNumber, "nb");
                        }
                    }
                }
                // This loop checks if the record is there in colomboCustomers or badullaCustomers list if there is
                // adds a placeholder value "b" to seatList
                for (List<String> customerDetail : customerDetails){
                    for (int item : seatList.keySet()){
                        if (customerDetail.contains(tempDateLocation.get(1)) && customerDetail.contains(String.valueOf(item))){
                            seatList.put(item, "b"); // b = Booked
                        }
                    }
                }
                root.getChildren().add(seat);

                // If the seat on the GUI is already booked then change its color to the background color
                if(!seatList.get(seatNumber).equals("nb")){
                    seat.setStyle("-fx-background-color: #1b87c2;");
                    seat.setTextFill(Paint.valueOf("#1b87c2"));
                }
                // If the seat on the GUI is not booked then change its color to green
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

    /**
     * This method asks for which train route, date and name of the customer then removes the relevant record/records
     * of customers from colomboCustomers or badullaCustomer and also from colomboBadullaDetails.
     * @param scanner passed from consoleMenu to take console input
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using badulla to colombo train route
     * @param colomboBadullaDetails a List with both details of customers from colombo to badulla and back stored
     */
    public void deleteCustomer(Scanner scanner, List<List<String>> colomboCustomers,
                               List<List<String>> badullaCustomers, List<List<String>> colomboBadullaDetails){
        String[] choices = new String[3]; // String array created to store 3 inputs from user
        List<List<String>> deletedRecords = new ArrayList<>(); // List to store deleted records
        System.out.print("\nPlease enter which train you want to remove the seats from([1] Colombo to Badulla/[2] Badulla to Colombo/[3] Both) : ");
        choices[0] = scanner.next();
        System.out.print("\nPlease enter a valid date you want to remove seats from (YYYY-MM-DD) : ");
        choices[1] = scanner.next();
        System.out.print("\nPlease enter the name of the customer you want to remove : ");
        choices[2] = scanner.next();

        switch (choices[0]) {
            case "1":
                // If train route is Colombo to Badulla execute this block of code
                for (List<String> details : colomboCustomers) {
                    // Checks if the date and name is in colomboCustomers List if it is then add that record to
                    // deletedRecords List
                    if (details.contains(choices[1]) && details.contains(choices[2])) {
                        deletedRecords.add(details);
                    }
                }
                // Removes the records that has the date and name match user input from colomboCustomers List and main list
                colomboCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                colomboBadullaDetails.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                System.out.println("\nDetails of deleted customers ");
                // Loop to display the deletedRecords in a formatted manner
                for (List<String> details : deletedRecords) {
                    System.out.println(
                            "\nName : " + details.get(4) +
                                    "\nSeat : " + details.get(3) +
                                    "\nDate : " + details.get(0) +
                                    "\nFrom : " + details.get(1) +
                                    "\nTo   : " + details.get(2));
                }
                deletedRecords.clear(); // Removes data stored in deleteRecords
                break;
            case "2":
                // If train route is Badulla to Colombo execute this block of code
                for (List<String> details : badullaCustomers) {
                    // Checks if the date and name is in badullaCustomers List if it is then add that record to
                    // deletedRecords List
                    if (details.contains(choices[1]) && details.contains(choices[2])) {
                        deletedRecords.add(details);
                    }
                }
                // Removes the records that has the date and name match user input from badullaCustomers List and main list
                badullaCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                colomboBadullaDetails.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                System.out.println("\nDetails of deleted customers ");
                // Loop to display the deletedRecords in a formatted manner
                for (List<String> details : deletedRecords) {
                    System.out.println(
                            "\nName : " + details.get(4) +
                                    "\nSeat : " + details.get(3) +
                                    "\nDate : " + details.get(0) +
                                    "\nFrom : " + details.get(1) +
                                    "\nTo   : " + details.get(2));
                }
                deletedRecords.clear(); // Removes data stored in deleteRecords
                break;
            case "3":
                // If train route is Colombo to Badulla and Badulla to Colombo execute this block of code
                for (List<String> details : colomboBadullaDetails) {
                    // Checks if the date and name is in colomboBadullaDetails List if it is then add that record to
                    // deletedRecords List
                    if (details.contains(choices[1]) && details.contains(choices[2])) {
                        deletedRecords.add(details);
                    }
                }
                // Removes the records that has the date and name match user input from all Lists
                colomboCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                badullaCustomers.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                colomboBadullaDetails.removeIf(details -> details.contains(choices[1]) && details.contains(choices[2]));
                // Loop to display the deletedRecords in a formatted manner
                for (List<String> details : deletedRecords) {
                    System.out.println(
                            "\nName : " + details.get(4) +
                                    "\nSeat : " + details.get(3) +
                                    "\nDate : " + details.get(0) +
                                    "\nFrom : " + details.get(1) +
                                    "\nTo   : " + details.get(2));
                }
                deletedRecords.clear(); // Removes data stored in deleteRecords
                break;
            default:
                System.out.println("Invalid input please try again");
                break;
        }
    }

    /**
     * This method asks for which train route and name of the customer then finds it in the colomboCustomers or
     * badullaCustomers List or both
     * @param scanner   passed from consoleMenu to take console input
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using badulla to colombo train route
     */
    public void findCustomer(Scanner scanner, List<List<String>> colomboCustomers,
                             List<List<String>> badullaCustomers, List<List<String>> colomboBadullaDetails){
        String[] choices = new String[2]; // String array created to store 2 inputs from user
        System.out.print("\nPlease enter which train you want to find the customer from ([1] Colombo to Badulla/[2] Badulla to Colombo /[3] Both) : ");
        choices[0] = scanner.next();
        System.out.print("Please enter the name of the customer to find the related seat booked : ");
        choices[1] = scanner.next();

        switch(choices[0]){
            case "1":
                // If colombo to badulla is entered execute this block of code
                for(List<String> details : colomboCustomers){
                    // If the name is in the colomboCustomers List print them in a formatted manner
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
                // If badulla to colombo is entered execute this block of code
                for(List<String> details : badullaCustomers){
                    // If the name is in the badullaCustomers List print them in a formatted manner
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
                // If colombo to badulla and badulla to colombo is entered execute this block of code
                for(List<String> details : colomboBadullaDetails){
                    // If the name is in the colomboBadullaDetails List print them in a formatted manner
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
            default:
                System.out.println("Invalid input please try again");
                break;
        }
    }

    /**
     * This method is used to save the customer details of both train routes into a text file or store it in a database,
     * MongoDB is used to store the details to the database and a txt file is used to store into a file.
     * @param scanner   passed from consoleMenu to take console input
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using badulla to colombo train route
     * @param colomboBadullaDetails a List with both details of customers from colombo to badulla and back stored
     * @throws IOException Exception for file handling
     */
    public void saveToFile(Scanner scanner, List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                           List<List<String>> colomboBadullaDetails) throws IOException{
        System.out.print("Do you want to save the details to a text file(T) or store it in the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        if(choice.equals("t")){
            // If text file is chosen execute the code block below
            FileWriter writer = new FileWriter("src/customerData.txt"); // Loads the file to writer variable
            // Loops through all the records of colomboBadullaDetails
            for(List<String> details : colomboBadullaDetails){
                // Takes 5 elements for each outer loop from colomboBadullaDetails which is [Date, Start Location,
                // Destination, Seat number, Name]
                for(int i = 0; i < 5; i++){
                    if(i == 4){
                        // If its the 5th element then write it to file then go to next line
                        writer.write(details.get(i)+"\n");
                    }
                    else{
                        // Write to file using "/" as the file separator
                        writer.write(details.get(i) + "/");
                    }
                }
            }
            writer.close(); // Close the file
            System.out.println("Successfully save to file");
        }
        else if(choice.equals("d")){
            //Connecting to MongoDB then creating a database and then two collections for each train route
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> colomboCollection = customerDatabase.getCollection("colomboDetails");
            MongoCollection<Document> badullaCollection = customerDatabase.getCollection("badullaDetails");
            System.out.println("Connected to the Database");

            // Checks if the documents for each route stored in two separate collections has any document
            if(colomboCollection.countDocuments() == 0 || badullaCollection.countDocuments() == 0){
                // Checks if colomboCollection has no documents
                if(colomboCollection.countDocuments() == 0) {
                    // Loops through each inner list in colomboCustomers to get [Date, Start location, Destination,
                    // Seat number, Name]
                    for (List<String> details : colomboCustomers) {
                        Document colomboDocument = new Document(); // Creates a new document
                        colomboDocument.append("date", details.get(0)); // Gets the date
                        colomboDocument.append("from", details.get(1)); // Gets the start location
                        colomboDocument.append("to", details.get(2)); // Gets the destination
                        colomboDocument.append("seat", details.get(3)); // Gets the seat number
                        colomboDocument.append("name", details.get(4)); // Gets the name
                        colomboCollection.insertOne(colomboDocument); // Add the document to the collection
                    }
                }
                // Checks if badullaCollection has no documents
                if(badullaCollection.countDocuments() == 0) {
                    // Loops through each inner list in badullaCustomers to get [Date, Start location, Destination,
                    // Seat number, Name]
                    for (List<String> details : badullaCustomers) {
                        Document badullaDocument = new Document(); // Creates a new document
                        badullaDocument.append("date", details.get(0)); // Gets the date
                        badullaDocument.append("from", details.get(1)); // Gets the start location
                        badullaDocument.append("to", details.get(2)); // Gets the destination
                        badullaDocument.append("seat", details.get(3)); // Gets the seat number
                        badullaDocument.append("name", details.get(4)); // Gets the name
                        badullaCollection.insertOne(badullaDocument); // Add the document to the collection
                    }
                }
            }
            // Checks if the documents for each route stored in two separate collections has 1 or more documents
            else if(colomboCollection.countDocuments() > 0 || badullaCollection.countDocuments() > 0){
                // Gets all the documents in colomboCollection train route into findColomboDocument
                FindIterable<Document> findColomboDocument = colomboCollection.find();
                // Gets all the documents in badullaCollection train route into findBadullaDocument
                FindIterable<Document> findBadullaDocument = badullaCollection.find();

                // Checks if colomboCollection has 1 or more documents
                if(colomboCollection.countDocuments() > 0){
                    // Loops through each document in colomboCollection and deletes them
                    for(Document document : findColomboDocument){
                        colomboCollection.deleteOne(document);
                    }
                    // Loops through each inner list in colomboCustomers to get [Date, Start location, Destination,
                    // Seat number, Name]
                    for(List<String> details : colomboCustomers){
                        Document colomboDocument = new Document(); // Creates a new document
                        colomboDocument.append("date",details.get(0)); // Gets the date
                        colomboDocument.append("from",details.get(1)); // Gets the start location
                        colomboDocument.append("to",details.get(2)); // Gets the destination
                        colomboDocument.append("seat",details.get(3)); // Gets the seat number
                        colomboDocument.append("name",details.get(4)); // Gets the name
                        colomboCollection.insertOne(colomboDocument); // Add the document to the collection
                    }
                }
                // Checks if badullaCollection has 1 or more documents
                if(badullaCollection.countDocuments() > 0){
                    // Loops through each document in badullaCollection and deletes them
                    for(Document document : findBadullaDocument){
                        badullaCollection.deleteOne(document);
                    }
                    // Loops through each inner list in badullaCustomers to get [Date, Start location, Destination,
                    // Seat number, Name]
                    for(List<String> details : badullaCustomers){
                        Document badullaDocument = new Document(); // Creates a new document
                        badullaDocument.append("date",details.get(0)); // Gets the date
                        badullaDocument.append("from",details.get(1)); // Gets the start location
                        badullaDocument.append("to",details.get(2)); // Gets the destination
                        badullaDocument.append("seat",details.get(3)); // Gets the seat number
                        badullaDocument.append("name",details.get(4)); // Gets the name
                        badullaCollection.insertOne(badullaDocument); // Add the document to the collection
                    }
                }
            }
            mongoClient.close(); // Closes the database connection
            System.out.println("Saved the details to the database successfully");
        }
        else{
            System.out.println("Invalid input. Please try again.");
        }
    }

    /**
     * This method is used to retrieve the customer details of both train routes from a text file or from the database,
     * colomboCustomers, badullaCustomers and colomboBadullaDetails get the data added to them from a txt file or from
     * MongoDB.
     * @param scanner   passed from consoleMenu to take console input
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using badulla to colombo train route
     * @param colomboBadullaDetails a List with both details of customers from colombo to badulla and back stored
     * @throws FileNotFoundException Exception when the specified file is not found
     */
    public void loadFromFile(Scanner scanner, List<List<String>> colomboCustomers, List<List<String>> badullaCustomers,
                             List<List<String>> colomboBadullaDetails) throws FileNotFoundException{
        System.out.print("Do you want to load the details from the text file(T) or retrieve it from the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        // If text file is selected execute the code inside if condition
        if(choice.equals("t")){
            // Removes the data stored in the below list if the user loads right after saving
            colomboBadullaDetails.clear();
            // Using scanner to read the file
            Scanner read = new Scanner(new File("src/customerData.txt"));
            if(!read.hasNextLine()){
                // If the file is empty gives an error
                System.out.println("Error file is empty! Please save data before loading");
            }
            else{
                // If the file already has data execute the code block below
                while (read.hasNextLine()) { // Checks if each line has data
                    // Add each line to the variable line
                    String line = read.nextLine();
                    // Uses a string array to get each set of characters separated by "/" and the output looks like
                    // [Date, Start location, Destination, Seat number, Name]
                    String[] holdDetails = line.split("/");
                    // Creates a new List called details and adds each element from holdDetails up to 5 elements each
                    // time
                    List<String> details = new ArrayList<>(Arrays.asList(holdDetails).subList(0, 5));
                    colomboBadullaDetails.add(details); // Add each details List to colomboBadullaDetails List
                }
                // Remove existing data in the arrays if the user loads right after saving
                colomboCustomers.clear();
                badullaCustomers.clear();
                // Loops through the colomboBadullaDetails and adds each inner list to colomboCustomers or
                // badullaCustomers based on the starting location
                for (List<String> details : colomboBadullaDetails) {
                    if (details.get(1).contains("Colombo")) {
                        colomboCustomers.add(details);
                    } else if (details.get(1).contains("Badulla")) {
                        badullaCustomers.add(details);
                    }
                }
                System.out.println("Successfully loaded from file");
            }
            read.close(); // Close the file
        }
        else if(choice.equals("d")){
            //Connecting to MongoDB then creating a database and then two collections for each train route
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> colomboCollection = customerDatabase.getCollection("colomboDetails");
            MongoCollection<Document> badullaCollection = customerDatabase.getCollection("badullaDetails");
            System.out.println("Connected to the Database");

            // Gets all the documents in colomboCollection train route into findColomboDocument
            FindIterable<Document> findColomboDocument = colomboCollection.find();
            // Gets all the documents in badullaCollection train route into findBadullaDocument
            FindIterable<Document> findBadullaDocument = badullaCollection.find();

            // Loops through each document in colomboColletion and adds each value from the keys to colomboCustomers
            // and colomboBadullaDetails List
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
            // Loops through each document in badullaColletion and adds each value from the keys to badullaCustomers
            // and colomboBadullaDetails List
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
            mongoClient.close(); // Closes the database connection
            System.out.println("Details loaded from the database successfully");
        }
        else{
            System.out.println("Invalid input. Please try again.");
        }
    }

    /**
     * This method is used to sort all the customer details based on their name in the ascending order by outputting
     * the names and seats only.
     * @param scanner   passed from consoleMenu to take console input
     * @param colomboCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using colombo to badulla train route
     * @param badullaCustomers  a List with all the details of customers(Date, Start location, Destination, Seat number and Name) using badulla to colombo train route
     */
    public void orderCustomerNames(Scanner scanner, List<List<String>> colomboCustomers,
                                   List<List<String>> badullaCustomers, List<List<String>> colomboBadullaCustomers){
        List<String> orderedList = new ArrayList<>(); // Create new List to store name and seat from the main list
        System.out.print("\nPlease enter which train you want to sort the seats according to customers from([1] Colombo to Badulla/[2] Badulla to Colombo/[3] Both) : ");
        String choice = scanner.next();
        //If Colombo to Badulla is selected go into the if condition
        switch (choice) {
            case "1":
                //Loops through colomboCustomers and gets the name and seat then adds to orderedList
                for (List<String> details : colomboCustomers) {
                    orderedList.add(details.get(4) + " - " + details.get(3));
                }
                System.out.println("\nCustomer names ordered based on first come first served basis\n");
                // Loops through each item in the orderedList to show the order which was added
                for (String item : orderedList) {
                    System.out.println(item);
                }
                // Bubble sort algorithm used to sort each item in orderedList in the ascending order
                for (int i = 0; i < orderedList.size(); i++) {
                    for (int j = i + 1; j < orderedList.size(); j++) {
                        // Checks if the value of index i is less than the value of index j if it is smaller then it
                        // goes inside the loop
                        if (orderedList.get(i).compareTo(orderedList.get(j)) > 0) {
                            // Stores the value of index i in hold which is less than j
                            String hold = orderedList.get(i);
                            // Sets the value of index j into the index of i
                            orderedList.set(i, orderedList.get(j));
                            // Sets the value of hold into the index of j
                            orderedList.set(j, hold);
                        }
                    }
                }
                System.out.println("\nCustomer names ordered in the ascending order\n");
                for (String item : orderedList) {
                    System.out.println(item);
                }
                orderedList.clear(); // Removes the data stored so it doesn't conflict with the other train route
                break;
            //If Badulla to Colombo is selected go into the else if condition
            case "2":
                //Loops through badullaCustomers and gets the name and seat then adds to orderedList
                for (List<String> details : badullaCustomers) {
                    orderedList.add(details.get(4) + " - " + details.get(3));
                }
                System.out.println("\nCustomer names ordered based on first come first served basis\n");
                // Loops through each item in the orderedList to show the order which was added
                for (String item : orderedList) {
                    System.out.println(item);
                }
                // Bubble sort algorithm used to sort each item in orderedList in the ascending order
                for (int i = 0; i < orderedList.size(); i++) {
                    for (int j = i + 1; j < orderedList.size(); j++) {
                        // Checks if the value of index i is less than the value of index j if it is smaller then it
                        // goes inside the loop
                        if (orderedList.get(i).compareTo(orderedList.get(j)) > 0) {
                            // Stores the value of index i in hold which is less than j
                            String hold = orderedList.get(i);
                            // Sets the value of index j into the index of i
                            orderedList.set(i, orderedList.get(j));
                            // Sets the value of hold into the index of j
                            orderedList.set(j, hold);
                        }
                    }
                }
                System.out.println("\nCustomer names ordered in the ascending order\n");
                for (String item : orderedList) {
                    System.out.println(item);
                }
                orderedList.clear(); // Removes the data stored so it doesn't conflict with the other train route
                break;
            case "3":
                //Loops through badullaCustomers and gets the name and seat then adds to orderedList
                for (List<String> details : colomboBadullaCustomers) {
                    orderedList.add(details.get(4) + " - " + details.get(3));
                }
                System.out.println("\nCustomer names ordered based on first come first served basis\n");
                // Loops through each item in the orderedList to show the order which was added
                for (String item : orderedList) {
                    System.out.println(item);
                }
                // Bubble sort algorithm used to sort each item in orderedList in the ascending order
                for (int i = 0; i < orderedList.size(); i++) {
                    for (int j = i + 1; j < orderedList.size(); j++) {
                        // Checks if the value of index i is less than the value of index j if it is smaller then it
                        // goes inside the loop
                        if (orderedList.get(i).compareTo(orderedList.get(j)) > 0) {
                            // Stores the value of index i in hold which is less than j
                            String hold = orderedList.get(i);
                            // Sets the value of index j into the index of i
                            orderedList.set(i, orderedList.get(j));
                            // Sets the value of hold into the index of j
                            orderedList.set(j, hold);
                        }
                    }
                }
                System.out.println("\nCustomer names ordered in the ascending order\n");
                for (String item : orderedList) {
                    System.out.println(item);
                }
                orderedList.clear(); // Removes the data stored so it doesn't conflict with the other train route
                break;
        }
    }

    /**
     * The other main method that is needed to run the whole program where the stage, scene and pane are passed from the
     * start method. Six data structures are created which are been used throughout the whole program. Relevant methods
     * are called for each option except for add, view and empty where it goes to the welcomeScreen method first
     * where a window is opened to select the train route, destination and date, which then goes to trainDestination
     * method where the relevant method (add, view and empty) based on user input is called.
     * @param root  Pane passed from start
     * @param stage passed from start
     * @param scene passed from start
     * @throws IOException  Exception for file handling
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
            // Switch case used to check which inputs were taken
            switch(userInput){
                /*
                 For add, view and empty welcomeScreen is used to select route, destination and date. Then inside
                 trainDestination the relevant methods for adding, viewing and viewing only empty seats are called.
                 */
                case "a":
                case "v":
                case "e":
                    welcomeScreen(stage, tempDateLocation);
                    trainDestination(root, stage, scene, userInput, tempDateLocation, seatList, tempSeatList,
                            colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "d":
                    deleteCustomer(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "f":
                    findCustomer(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "s":
                    saveToFile(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "l":
                    loadFromFile(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
                    break;
                case "o":
                    orderCustomerNames(scanner, colomboCustomers, badullaCustomers, colomboBadullaDetails);
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
