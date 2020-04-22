package CW2;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TrainStation extends Application {
    private Passenger[] waitingRoom = new Passenger[42];
    private PassengerQueue trainQueue1 = new PassengerQueue();
    private PassengerQueue trainQueue2 = new PassengerQueue();
    private Passenger[] trainQueueArray1 = trainQueue1.getQueueArray();
    private Passenger[] trainQueueArray2 = trainQueue2.getQueueArray();
    private Passenger[] boardedPassengers = new Passenger[42];
    private TableView<Passenger> waitingRoomTableView;
    private TableView<Passenger> trainQueueTableView1;
    private TableView<Passenger> trainQueueTableView2;
    private TableView<Passenger> boardedTableView;

    public static void main(String[] args) {
        launch(args);
    }

    public void consoleMenu() throws IOException {
        Scanner scanner = new Scanner(System.in);

        // List created to store both train details
        List<List<String>> customerDetails = new ArrayList<>();

        // A List created to store each stop in the colonbo to badulla train
        List<String> stationStops = Arrays.asList("Colombo Fort" , "Polgahawela", "Peradeniya Junction", "Gampola",
                "Nawalapitiya", "Hatton", "Talawakelle", "Nanu Oya", "Haputale", "Diyatalawa", "Bandarawela",
                "Ella", "Badulla");

        List<String> stationDetails = new ArrayList<>(Arrays.asList("0","0","0"));

        while(true){
            System.out.println(
                    "\nWelcome To Sri Lanka Railways Department\n" +
                    "Denuwara Menike Intercity Express Train departure from Colombo to Badulla / Badulla to Colombo\n" +
                    "\nPlease enter 'A' to add passengers from waiting room to the train queue\n" +
                    "Please enter 'V' to view waiting room, train queue and boarded passengers\n" +
                    "Please enter 'D' to delete customer from train queue\n" +
                    "Please enter 'S' to store the train queue data to a file or database\n" +
                    "Please enter 'L' to load the train queue data from a file or database\n" +
                    "Please enter 'R' to run the simulation and produce report\n" +
                    "Please enter 'Q' to quit the program");

            String userInput = scanner.nextLine().toUpperCase();
            // Switch case used to check which inputs were taken
            switch(userInput){
                /* For add, view and empty welcomeScreen is used to select route, destination and date. Then inside
                   trainDestination the relevant methods for adding, viewing and viewing only empty seats are called. */
                case "A":
                    if(checkWaitingRoom()==0 && trainQueue1.isEmpty() && trainQueue2.isEmpty()) {
                        selectStation(userInput, stationStops, stationDetails);
                        loadCustomersFromBooking(stationDetails, customerDetails);
                    }
                    if(checkWaitingRoom()>0) {
                        addPassenger();
                    }
                    else {
                        errorAlert("waitingRoom");
                    }
                    break;
                case "V":
                    viewPassenger();
                    break;
                case "D":
                    deletePassenger();
                    break;
                case "S":
                    saveTrainQueue();
                    break;
                case "L":
                    loadTrainQueue();
                    break;
                case "R":
                    runSimulation(userInput, stationStops, stationDetails, customerDetails);
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
    public void start(Stage primaryStage) throws IOException {
        consoleMenu();
    }

    public int checkWaitingRoom(){
        int roomPassengers = 42;
        for(Passenger item : waitingRoom){
            if(item==null){
                roomPassengers --;
            }
        }
        return roomPassengers;
    }

    public void selectStation(String userInput, List<String> stationStops, List<String> stationDetails){
        Stage stage = new Stage();
        Pane root = new Pane();

        Label title = new Label("Welcome to Sri Lanka Railways Department");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label(
                "Train name - Denuwara Menike\n" +
                "Class - 1st Class A/C Compartment\n" +
                "Train number - Colombo to Badulla (1001)\n" +
                "Train number - Badulla to Colombo (1002)\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(220);
        details.setLayoutY(100);

        Label information = new Label("Please select a date and station to view the train station queue");
        information.setStyle("-fx-font: 16 arial; -fx-text-fill: black;");
        information.setLayoutX(180);
        information.setLayoutY(200);

        // Default value set to the systems local date
        DatePicker selectDate = new DatePicker(LocalDate.now());
        selectDate.setPrefSize(150, 40);
        selectDate.setLayoutX(120);
        selectDate.setLayoutY(250);
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
                if(item.compareTo(presentDay)<0 && userInput.equals("Z")) { // change to A later
                    setDisable(true);
                    setStyle("-fx-background-color: red");
                }
            }
        });

        // Dropdown list of trains
        ComboBox<String> train = new ComboBox<>();
        train.setPromptText("Train");
        train.setPrefSize(150, 40);
        train.setLayoutX(320);
        train.setLayoutY(250);
        train.getItems().add("1001");
        train.getItems().add("1002");

        // Dropdown list of stops from the starting location
        ComboBox<String> station = new ComboBox<>();
        station.setPromptText("Station");
        station.setPrefSize(150, 40);
        station.setLayoutX(520);
        station.setLayoutY(250);

        for(String item : stationStops){
            station.getItems().add(item);
        }

        Button confirmStation = new Button("Confirm");
        confirmStation.setPrefSize(150, 40);
        confirmStation.setLayoutX(320);
        confirmStation.setLayoutY(350);

        confirmStation.setOnAction(event -> {
            String date = selectDate.getValue().toString();
            String selectedTrain = train.getSelectionModel().getSelectedItem();
            String selectedStation = station.getSelectionModel().getSelectedItem();

            if(selectedTrain != null && selectedStation != null){
                stationDetails.set(0,date);
                stationDetails.set(1,selectedTrain);
                stationDetails.set(2,selectedStation);
            }
            else{
                Alert noSelection = new Alert(Alert.AlertType.WARNING);
                noSelection.setTitle("No Selection Detected");
                noSelection.setHeaderText("Warning! No Option selected!");
                noSelection.setContentText("Please select a date, train and a station! Try again.");
                noSelection.showAndWait();
            }
            stage.close();
        });

        root.setStyle("-fx-background-color: #1b87c2");
        stage.setTitle("Train Station Queue Application");
        root.getChildren().addAll(title, details, information, selectDate, train, station, confirmStation);
        Scene scene1 = new Scene(root, 820, 500);
        stage.setScene(scene1);
        stage.showAndWait();
    }

    public void loadCustomersFromBooking(List<String> stationDetails, List<List<String>> customerDetails) {
        if(!stationDetails.contains("0")) {
            //Connecting to MongoDB then creating a database and then two collections for each train route
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
            MongoCollection<Document> customerCollection = trainDatabase.getCollection("customerDetails");
            System.out.println("Connected to the Database");

            // Gets all the documents in colomboCollection train route into findColomboDocument
            FindIterable<Document> findCustomerDocument = customerCollection.find();

            // Loops through each document in colomboColletion and adds each value from the keys to colomboCustomers
            // and colomboBadullaDetails List
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

                if (details.get(5).equals(stationDetails.get(0)) && details.get(0).equals(stationDetails.get(1)) &&
                        details.get(6).equals(stationDetails.get(2))) {
                    // Add each details List to customerDetails List
                    customerDetails.add(details);
                }
            }
            mongoClient.close(); // Closes the database connection
            System.out.println("Details loaded from the database successfully");
            System.out.println(customerDetails);
            if(customerDetails.size()>10){
                notArrivedPassengers(customerDetails);
            }
            addPassengerToWaitingRoom(customerDetails);
        }
    }

    public void notArrivedPassengers(List<List<String>> customerDetails){
        Random random = new Random();
        int randomChance = random.nextInt(4);
        Passenger[] notArrivedPassengersArray = new Passenger[randomChance];
        System.out.println(randomChance);
        System.out.println("Before delete "+customerDetails);
        System.out.println("size before "+customerDetails.size());
        if(randomChance!=0) {
            for (int i = 0; i < randomChance; i++) {
                int randomPassenger = random.nextInt(customerDetails.size()) + 1;
                System.out.println(randomPassenger);
                for (List<String> customer : customerDetails) {
                    if (customerDetails.indexOf(customer) == randomPassenger) {
                        Passenger passenger = new Passenger();
                        passenger.setTrain(customer.get(0));
                        passenger.setSeatNumber(customer.get(1));
                        passenger.setNic(customer.get(2));
                        passenger.setName(customer.get(3), customer.get(4));
                        passenger.setDate(customer.get(5));
                        passenger.setFrom(customer.get(6));
                        passenger.setTo(customer.get(7));
                        passenger.setTicketId(customer.get(8));
                        passenger.setArrived("No");
                        for (int j = 0; j < notArrivedPassengersArray.length; j++) {
                            if (notArrivedPassengersArray[j] == null) {
                                notArrivedPassengersArray[j] = passenger;
                                break;
                            }
                        }
                        customerDetails.remove(randomPassenger);
                        break;
                    }
                }
            }
            for (int i = 0; i < notArrivedPassengersArray.length; i++) {
                boardedPassengers[i] = notArrivedPassengersArray[i];
            }
        }
        System.out.println("After delete "+customerDetails);
        System.out.println("size after "+customerDetails.size());
        System.out.println("not arrived "+ Arrays.toString(notArrivedPassengersArray));
        System.out.println("boarded "+ Arrays.toString(boardedPassengers));
    }

    public void addPassengerToWaitingRoom(List<List<String>> customerDetails){
        for(List<String> customer : customerDetails){
            Passenger passenger = new Passenger();
            passenger.setTrain(customer.get(0));
            passenger.setSeatNumber(customer.get(1));
            passenger.setNic(customer.get(2));
            passenger.setName(customer.get(3), customer.get(4));
            passenger.setDate(customer.get(5));
            passenger.setFrom(customer.get(6));
            passenger.setTo(customer.get(7));
            passenger.setTicketId(customer.get(8));
            passenger.setArrived("Yes");
            waitingRoom[customerDetails.indexOf(customer)] = passenger;
        }
    }

    public void addPassengerToTrainQueue() {
        int randomQueueSize = randomNumberGenerator();
        System.out.println("Random number : "+randomQueueSize);
        for(int i = 0; i < randomQueueSize; i++){
            if(trainQueue1.isFull() && trainQueue2.isFull()){
                break;
            }
            else if(checkWaitingRoom()==0){
                break;
            }
            for(int j = 0; j < waitingRoom.length; j++) {
                if (waitingRoom[j]!=null) {
                    if(trainQueue1.getMaxLength()==trainQueue2.getMaxLength()){
                        waitingRoom[j].setQueueNumber("1");
                        trainQueue1.add(waitingRoom[j]);
                        waitingRoom[j] = null;
                        break;
                    }
                    else if(trainQueue1.getMaxLength()<trainQueue2.getMaxLength()){
                        waitingRoom[j].setQueueNumber("1");
                        trainQueue1.add(waitingRoom[j]);
                        waitingRoom[j] = null;
                        break;
                    }
                    else if(trainQueue1.getMaxLength()>trainQueue2.getMaxLength()){
                        waitingRoom[j].setQueueNumber("2");
                        trainQueue2.add(waitingRoom[j]);
                        waitingRoom[j] = null;
                        break;
                    }
                }
            }
        }
    }

    public void errorAlert(String queuePlace){
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if(queuePlace.equals("waitingRoom")) {
            warning.setTitle("Waiting Room is Empty");
            warning.setHeaderText("Waiting Room is Empty!");
            warning.setContentText("Waiting room passengers are empty for the current date");
        }
        else if(queuePlace.equals("trainQueue")){
            warning.setTitle("Queue 1 or 2 is Full");
            warning.setHeaderText("Queue 1 or 2 is Full!");
            warning.setContentText("Please remove passengers from queue before adding to the queue");
        }
        warning.showAndWait();
    }

    public void sortPassengersInQueue(PassengerQueue trainQueue,Passenger[] trainQueueArray){
        for (int i = 0; i < trainQueue.getMaxLength(); i++) {
            for (int j = i + 1; j < trainQueue.getMaxLength(); j++) {
                if ((Integer.parseInt(trainQueueArray[i].getSeatNumber()) > (Integer.parseInt(trainQueueArray[j].getSeatNumber())))) {
                    Passenger temp = trainQueueArray[i];
                    trainQueueArray[i] = trainQueueArray[j];
                    trainQueueArray[j] = temp;
                }
            }
        }
    }

    public ObservableList<Passenger> getPassengersToTableView(Passenger[] passengerArray){
        ObservableList<Passenger> passengerObservableList = FXCollections.observableArrayList();
        for(Passenger passenger : passengerArray){
            if(passenger!=null){
                passengerObservableList.add(passenger);
            }
        }
        return passengerObservableList;
    }

    public int randomNumberGenerator(){
        Random random = new Random();
        return random.nextInt(6)+1;
    }

    public void addTableColumns(TableView<Passenger> tableView){
        TableColumn<Passenger, String> seatNumberColumn = new TableColumn<>("Seat");
        seatNumberColumn.setMaxWidth(50);
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        TableColumn<Passenger, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMaxWidth(150);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Passenger, String> ticketIdColumn = new TableColumn<>("Ticket No");
        ticketIdColumn.setMaxWidth(80);
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));

        TableColumn<Passenger, String> trainColumn = new TableColumn<>("Train");
        trainColumn.setMaxWidth(50);
        trainColumn.setCellValueFactory(new PropertyValueFactory<>("train"));

        TableColumn<Passenger, String> nicColumn = new TableColumn<>("NIC");
        nicColumn.setMaxWidth(100);
        nicColumn.setCellValueFactory(new PropertyValueFactory<>("nic"));

        TableColumn<Passenger, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMaxWidth(80);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Passenger, String> fromColumn = new TableColumn<>("From");
        fromColumn.setMaxWidth(125);
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));

        TableColumn<Passenger, String> toColumn = new TableColumn<>("To");
        toColumn.setMaxWidth(125);
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));

        tableView.getColumns().addAll(seatNumberColumn, nameColumn, ticketIdColumn, trainColumn, nicColumn, dateColumn, fromColumn, toColumn);
    }
    public AnchorPane createPane(String title, String color){
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(600, 500);
        pane.setStyle("-fx-border-width: 2; -fx-border-style: solid; -fx-background-color:"+color);
        Label paneTitle = new Label(title);
        paneTitle.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        paneTitle.setLayoutX(200);
        paneTitle.setLayoutY(10);
        pane.getChildren().addAll(paneTitle);
        return pane;
    }

    public AnchorPane displayWaitingRoomTable(AnchorPane pane){
        waitingRoomTableView = new TableView<>();
        waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
        addTableColumns(waitingRoomTableView);
        AnchorPane.setBottomAnchor(waitingRoomTableView, 0.0);
        AnchorPane.setLeftAnchor(waitingRoomTableView, 0.0);
        AnchorPane.setRightAnchor(waitingRoomTableView, 0.0);
        AnchorPane.setTopAnchor(waitingRoomTableView, 50.0);
        pane.getChildren().addAll(waitingRoomTableView);

        return pane;
    }

    public AnchorPane displayTrainQueueTable(AnchorPane pane){
        trainQueueTableView1 = new TableView<>();
        trainQueueTableView1.setItems(getPassengersToTableView(trainQueueArray1));
        addTableColumns(trainQueueTableView1);
        AnchorPane.setBottomAnchor(trainQueueTableView1, 0.0);
        AnchorPane.setLeftAnchor(trainQueueTableView1, 0.0);
        AnchorPane.setRightAnchor(trainQueueTableView1, 300.0);
        AnchorPane.setTopAnchor(trainQueueTableView1, 50.0);

        trainQueueTableView2 = new TableView<>();
        trainQueueTableView2.setItems(getPassengersToTableView(trainQueueArray2));
        addTableColumns(trainQueueTableView2);
        AnchorPane.setBottomAnchor(trainQueueTableView2, 0.0);
        AnchorPane.setLeftAnchor(trainQueueTableView2, 305.0);
        AnchorPane.setRightAnchor(trainQueueTableView2, 0.0);
        AnchorPane.setTopAnchor(trainQueueTableView2, 50.0);
        pane.getChildren().addAll(trainQueueTableView1, trainQueueTableView2);

        return pane;
    }

    public AnchorPane displayBoardedTable(AnchorPane pane){
        TableColumn<Passenger, String> arrivedColumn = new TableColumn<>("Arrived");
        arrivedColumn.setMinWidth(50);
        arrivedColumn.setCellValueFactory(new PropertyValueFactory<>("arrived"));

        TableColumn<Passenger, String> secondsColumn = new TableColumn<>("Queue Time");
        secondsColumn.setMinWidth(70);
        secondsColumn.setCellValueFactory(new PropertyValueFactory<>("secondsInQueue"));

        TableColumn<Passenger, String> queueNumberColumn = new TableColumn<>("Queue Number");
        queueNumberColumn.setMinWidth(70);
        queueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("queueNumber"));

        boardedTableView = new TableView<>();
        boardedTableView.setItems(getPassengersToTableView(boardedPassengers));
        addTableColumns(boardedTableView);
        boardedTableView.getColumns().addAll(arrivedColumn, secondsColumn, queueNumberColumn);
        AnchorPane.setBottomAnchor(boardedTableView, 0.0);
        AnchorPane.setLeftAnchor(boardedTableView, 0.0);
        AnchorPane.setRightAnchor(boardedTableView, 0.0);
        AnchorPane.setTopAnchor(boardedTableView, 50.0);
        pane.getChildren().addAll(boardedTableView);

        return pane;
    }

    public void addPassenger(){
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1205, 700);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        AnchorPane waitingRoomPane = createPane("Waiting Room","#fcba03");
        AnchorPane trainQueuePane = createPane("Train Queue 1 and 2","#00ad71");

        Pane buttonPane = new Pane();
        buttonPane.setPrefSize(1100, 145);
        Button addButton = new Button("Add to Queue");
        addButton.setPrefSize(120, 50);
        addButton.setStyle("-fx-font: 14 arial;");
        addButton.setLayoutX(530);
        addButton.setLayoutY(40);
        buttonPane.getChildren().addAll(addButton);

        addButton.setOnAction(event -> {
            addPassengerToTrainQueue();
            sortPassengersInQueue(trainQueue1,trainQueueArray1);
            sortPassengersInQueue(trainQueue2,trainQueueArray2);
            waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
            trainQueueTableView1.setItems(getPassengersToTableView(trainQueueArray1));
            trainQueueTableView2.setItems(getPassengersToTableView(trainQueueArray2));
            if(trainQueue1.isFull() || trainQueue2.isFull()){
                errorAlert("trainQueue");
            }
            if(checkWaitingRoom()==0){
                errorAlert("waitingRoom");
            }
        });

        root.setLeft(displayWaitingRoomTable(waitingRoomPane));
        root.setCenter(displayTrainQueueTable(trainQueuePane));
        root.setBottom(buttonPane);

        stage.setScene(scene);
        stage.showAndWait();
    }

    public void viewPassenger(){
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #00ad71");

        Label title = new Label("Seats of passengers in Train Queue");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        title.setPadding(new Insets(10, 0, 0, 550));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(40, 50, 50, 50));
        grid.setHgap(20);
        grid.setVgap(20);
        Scene scene = new Scene(root, 1600, 680);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        int seatNumber = 0;
        for(int i = 0; i < 6; i++){
            for(int j = 0; j<7; j++){
                VBox seatBox = new VBox();
                seatBox.setPrefSize(200,80);
                seatBox.setStyle("-fx-background-color: #fcba03; -fx-border-width: 2; " +
                        "-fx-border-style: solid; -fx-border-color: black; " +
                        "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");

                Label passengerSeat = new Label("Seat "+(++seatNumber));
                passengerSeat.setStyle("-fx-font: 18 arial; -fx-font-weight: bold; -fx-text-fill: black;");
                Label passengerName = new Label("Name : "+"Empty");
                passengerName.setStyle("-fx-text-fill: black;");
                Label passengerTicket = new Label("Ticket : "+"Empty");
                passengerTicket.setStyle("-fx-text-fill: black;");

                if(!trainQueue1.isEmpty()) {
                    for (Passenger passenger : trainQueueArray1) {
                        if(passenger==null){
                            continue;
                        }
                        if (passenger.getSeatNumber().equals(String.valueOf(seatNumber))) {
                            passengerName.setText("Name : "+passenger.getName());
                            passengerTicket.setText("Ticket : "+passenger.getTicketId());
                            seatBox.setStyle("-fx-background-color: #bd244f; -fx-border-width: 2; " +
                                    "-fx-border-style: solid; -fx-border-color: black; " +
                                    "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                        }
                    }
                }
                if(!trainQueue2.isEmpty()) {
                    for (Passenger passenger : trainQueueArray2) {
                        if(passenger==null){
                            continue;
                        }
                        if (passenger.getSeatNumber().equals(String.valueOf(seatNumber))) {
                            passengerName.setText("Name : "+passenger.getName());
                            passengerTicket.setText("Ticket : "+passenger.getTicketId());
                            seatBox.setStyle("-fx-background-color: #bd244f; -fx-border-width: 2; " +
                                    "-fx-border-style: solid; -fx-border-color: black; " +
                                    "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                        }
                    }
                }
                seatBox.getChildren().addAll(passengerSeat, passengerName, passengerTicket);
                grid.add(seatBox,j, i);
            }
        }
        root.setTop(title);
        root.setLeft(grid);

        stage.setScene(scene);
        stage.showAndWait();
    }

    public void deletePassenger(){
        Passenger[] tempQueueArray1 = new Passenger[trainQueue1.getMaxLength()];
        Passenger[] tempQueueArray2 = new Passenger[trainQueue2.getMaxLength()];
        Passenger deletedPassenger = null;
        boolean foundInQueue1 = false;
        boolean foundInQueue2 = false;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the seat number of passenger to remove from the train queue : ");
        String seatSelected = scanner.nextLine();

        if(!trainQueue1.isEmpty() && !seatSelected.isEmpty() &&
                (Integer.parseInt(seatSelected) >0) && (Integer.parseInt(seatSelected) <43)) {
            for (Passenger passenger : trainQueueArray1) {
                if(passenger==null){
                    continue;
                }
                if (passenger.getSeatNumber().equals(seatSelected)) {
                    foundInQueue1 = true;
                    break;
                }
            }
        }
        if(!trainQueue2.isEmpty() && !seatSelected.isEmpty() &&
                (Integer.parseInt(seatSelected) >0) && (Integer.parseInt(seatSelected) <43)){
            for (Passenger passenger : trainQueueArray2) {
                if(passenger==null){
                    continue;
                }
                if (passenger.getSeatNumber().equals(seatSelected)) {
                    foundInQueue2 = true;
                    break;
                }
            }
        }

        if(foundInQueue1) {
            for (int i = 0, j = 0; i < trainQueueArray1.length; i++) {
                if (trainQueueArray1[i] == (null)) {
                    continue;
                } else if (!trainQueueArray1[i].getSeatNumber().equals(seatSelected)) {
                    tempQueueArray1[j++] = trainQueueArray1[i];
                } else if (trainQueueArray1[i].getSeatNumber().equals(seatSelected)) {
                    deletedPassenger = trainQueueArray1[i];
                }
                trainQueueArray1[i] = null;
            }

            trainQueue1.setFirstAndLast(0, 0);
            trainQueue1.setMaxLength(0);
            for (Passenger passenger : tempQueueArray1) {
                if (passenger != null) {
                    trainQueue1.add(passenger);
                }
            }
            System.out.println("\nDeleted passenger details \n\n" +
                    "Passenger seat number   : " + deletedPassenger.getSeatNumber() +
                    "\nPassenger name          : " + deletedPassenger.getName() +
                    "\nPassenger ticket number : " + deletedPassenger.getTicketId() +
                    "\nPassenger train number  : " + deletedPassenger.getTrain() +
                    "\nPassenger NIC           : " + deletedPassenger.getNic() +
                    "\nPassenger Date          : " + deletedPassenger.getDate() +
                    "\nPassenger From          : " + deletedPassenger.getFrom() +
                    "\nPassenger To            : " + deletedPassenger.getTo() +
                    "\nPassenger Queue Number  : "+ deletedPassenger.getQueueNumber());
        }
        else if(foundInQueue2){
            for (int i = 0, j = 0; i < trainQueueArray2.length; i++) {
                if (trainQueueArray2[i] == (null)) {
                    continue;
                } else if (!trainQueueArray2[i].getSeatNumber().equals(seatSelected)) {
                    tempQueueArray2[j++] = trainQueueArray2[i];
                } else if (trainQueueArray2[i].getSeatNumber().equals(seatSelected)) {
                    deletedPassenger = trainQueueArray2[i];
                }
                trainQueueArray2[i] = null;
            }

            trainQueue2.setFirstAndLast(0, 0);
            trainQueue2.setMaxLength(0);
            for (Passenger passenger : tempQueueArray2) {
                if (passenger != null) {
                    trainQueue2.add(passenger);
                }
            }
            System.out.println("\nDeleted passenger details \n\n" +
                    "Passenger seat number   : " + deletedPassenger.getSeatNumber() +
                    "\nPassenger name          : " + deletedPassenger.getName() +
                    "\nPassenger ticket number : " + deletedPassenger.getTicketId() +
                    "\nPassenger train number  : " + deletedPassenger.getTrain() +
                    "\nPassenger NIC           : " + deletedPassenger.getNic() +
                    "\nPassenger Date          : " + deletedPassenger.getDate() +
                    "\nPassenger From          : " + deletedPassenger.getFrom() +
                    "\nPassenger To            : " + deletedPassenger.getTo() +
                    "\nPassenger Queue Number  : "+ deletedPassenger.getQueueNumber());
        }
        else{
            System.out.println("\nThe passenger for that seat entered is not in the queue. Please enter a passenger with a seat in the train queue.");
        }
    }

    public void saveToCollectionFromArray(MongoCollection<Document> passengerCollection, Passenger[] passengerArray){
        for (Passenger passenger : passengerArray) {
            if (passenger != null) {
                // Creates a new document
                Document document = new Document();
                // Gets the train number
                document.append("train", passenger.getTrain());
                // Gets the seat number
                document.append("seat", passenger.getSeatNumber());
                // Gets the NIC
                document.append("NIC", passenger.getNic());
                // Gets the first name
                document.append("firstname", passenger.getFirstName());
                // Gets the surname
                document.append("surname", passenger.getSurname());
                // Gets the date
                document.append("date", passenger.getDate());
                // Gets the boarding station
                document.append("from", passenger.getFrom());
                // Gets the destination
                document.append("to", passenger.getTo());
                // Gets the ticket number
                document.append("ticket", passenger.getTicketId());
                if(String.valueOf(passenger.getSecondsInQueue())!=null){
                    document.append("seconds", String.valueOf(passenger.getSecondsInQueue()));
                }
                // Gets arrived
                document.append("arrived", passenger.isArrived());
                if(passenger.getQueueNumber()!=null){
                    // Gets the queue number
                    document.append("queue", String.valueOf(passenger.getQueueNumber()));
                }
                // Add the document to the collection
                passengerCollection.insertOne(document);
            }
        }
    }

    public void saveTrainQueue(){
        //Connecting to MongoDB then creating a database and then two collections for each train route
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> waitingRoomCollection = trainDatabase.getCollection("waitingRoomDetails");
        MongoCollection<Document> queueCollection = trainDatabase.getCollection("queueDetails");
        MongoCollection<Document> boardedCollection = trainDatabase.getCollection("boardedDetails");
        System.out.println("Connected to the Database");

        // Checks if the documents for each route stored in two separate collections has any document
        if(waitingRoomCollection.countDocuments() == 0 || queueCollection.countDocuments() == 0 ||
                boardedCollection.countDocuments() == 0){

            if(waitingRoomCollection.countDocuments() == 0) {
                saveToCollectionFromArray(waitingRoomCollection, waitingRoom);
            }
            if(queueCollection.countDocuments() == 0) {
                saveToCollectionFromArray(queueCollection, trainQueueArray1);
                saveToCollectionFromArray(queueCollection, trainQueueArray2);
            }
            if(boardedCollection.countDocuments() == 0){
                saveToCollectionFromArray(boardedCollection, boardedPassengers);
            }
        }
        // Checks if the documents for each route stored in two separate collections has 1 or more documents
        else if(waitingRoomCollection.countDocuments() > 0 || queueCollection.countDocuments() > 0){
            if(waitingRoomCollection.countDocuments() > 0) {
                trainDatabase.getCollection("waitingRoomDetails").drop();
                saveToCollectionFromArray(waitingRoomCollection, waitingRoom);
            }
            if(queueCollection.countDocuments() > 0) {
                trainDatabase.getCollection("queueDetails").drop();
                saveToCollectionFromArray(queueCollection, trainQueueArray1);
                saveToCollectionFromArray(queueCollection, trainQueueArray2);
            }
            if(boardedCollection.countDocuments() > 0){
                trainDatabase.getCollection("boardedDetails").drop();
                saveToCollectionFromArray(boardedCollection, boardedPassengers);
            }
        }
        mongoClient.close(); // Closes the database connection
        System.out.println("Saved the details to the database successfully");
    }

    public void loadWaitingAndBoardedFromCollection(MongoCollection<Document> passengerCollection, Passenger[] passengerArray){
        // Gets all the documents in colomboCollection train route into findColomboDocument
        FindIterable<Document> findDocuments = passengerCollection.find();

        // Loops through each document in colomboColletion and adds each value from the keys to colomboCustomers
        // and colomboBadullaDetails List
        Arrays.fill(passengerArray, null);
        for(Document document : findDocuments){
            for(int i = 0; i < passengerArray.length; i++) {
                if(passengerArray[i]==null) {
                    Passenger passenger = new Passenger();
                    passenger.setTrain(document.getString("train"));
                    passenger.setSeatNumber(document.getString("seat"));
                    passenger.setNic(document.getString("NIC"));
                    passenger.setName(document.getString("firstname"), document.getString("surname"));
                    passenger.setDate(document.getString("date"));
                    passenger.setFrom(document.getString("from"));
                    passenger.setTo(document.getString("to"));
                    passenger.setTicketId(document.getString("ticket"));
                    if(document.getString("seconds")!=null){
                        passenger.setSecondsInQueue(Integer.parseInt(document.getString("seconds")));
                    }
                    passenger.setArrived(document.getString("arrived"));
                    if(document.getString("queue")!=null){
                        // Gets the queue number
                        passenger.setQueueNumber(document.getString("queue"));
                    }
                    passengerArray[i] = passenger;
                    break;
                }
            }
        }
        System.out.println("Array "+ Arrays.toString(passengerArray));
    }

    public void loadTrainQueueFromCollection(MongoCollection<Document> passengerCollection){
        // Gets all the documents in colomboCollection train route into findColomboDocument
        FindIterable<Document> findQueueDocument = passengerCollection.find();

        Arrays.fill(trainQueueArray1, null);
        Arrays.fill(trainQueueArray2, null);
        for(Document document : findQueueDocument){
            Passenger passenger = new Passenger();
            if(document.getString("queue").equals("1")){
                passenger.setTrain(document.getString("train"));
                passenger.setSeatNumber(document.getString("seat"));
                passenger.setNic(document.getString("NIC"));
                passenger.setName(document.getString("firstname"), document.getString("surname"));
                passenger.setDate(document.getString("date"));
                passenger.setFrom(document.getString("from"));
                passenger.setTo(document.getString("to"));
                passenger.setTicketId(document.getString("ticket"));
                passenger.setArrived(document.getString("arrived"));
                passenger.setQueueNumber(document.getString("queue"));
                trainQueue1.add(passenger);
            }
            else if(document.getString("queue").equals("2")){
                passenger.setTrain(document.getString("train"));
                passenger.setSeatNumber(document.getString("seat"));
                passenger.setNic(document.getString("NIC"));
                passenger.setName(document.getString("firstname"), document.getString("surname"));
                passenger.setDate(document.getString("date"));
                passenger.setFrom(document.getString("from"));
                passenger.setTo(document.getString("to"));
                passenger.setTicketId(document.getString("ticket"));
                passenger.setArrived(document.getString("arrived"));
                passenger.setQueueNumber(document.getString("queue"));
                trainQueue2.add(passenger);
            }
        }
    }

    public void loadTrainQueue(){
        //Connecting to MongoDB then creating a database and then two collections for each train route
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> waitingRoomCollection = trainDatabase.getCollection("waitingRoomDetails");
        MongoCollection<Document> queueCollection = trainDatabase.getCollection("queueDetails");
        MongoCollection<Document> boardedCollection = trainDatabase.getCollection("boardedDetails");
        System.out.println("Connected to the Database");

        loadWaitingAndBoardedFromCollection(waitingRoomCollection, waitingRoom);

        loadTrainQueueFromCollection(queueCollection);

        loadWaitingAndBoardedFromCollection(boardedCollection, boardedPassengers);

        mongoClient.close(); // Closes the database connection
        System.out.println("Details loaded from the database successfully");
    }

    public void displayReport(int[] reportStats){
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1580, 600);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        GridPane reportPane = new GridPane();
        reportPane.setPadding(new Insets(0, 50, 0, 0));
        reportPane.setHgap(20);
        reportPane.setVgap(20);
        Label reportTitle = new Label("Report");
        reportTitle.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        reportTitle.setPadding(new Insets(10,0,0,300));
        reportPane.add(reportTitle,0,0,4,2);

        AnchorPane boardedPane = createPane("Boarded Passengers", "#3670b5");

        Label queue1Heading = new Label("Queue 1");
        queue1Heading.setStyle("-fx-font: 18 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        Label queue2Heading = new Label("Queue 2");
        queue2Heading.setStyle("-fx-font: 18 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        Label bothQueuesHeading = new Label("Both Queues");
        bothQueuesHeading.setStyle("-fx-font: 18 arial; -fx-font-weight: bold; -fx-text-fill: black;");

        VBox queueDetails = new VBox();
        Label length = new Label("Maximum Queue Length");
        Label maxWait = new Label("Maximum Waiting Time");
        Label minWait = new Label("Minimum Waiting Time");
        Label avgWait = new Label("Average Waiting Time");

        VBox queue1Box = new VBox();
        Label queue1length = new Label(reportStats[0]+" passengers");
        Label queue1maxWait = new Label(reportStats[1]+" seconds");
        Label queue1minWait = new Label(reportStats[2]+" seconds");
        Label queue1avgWait = new Label(reportStats[3]+" seconds");

        VBox queue2Box = new VBox();
        Label queue2length = new Label(reportStats[4]+" passengers");
        Label queue2maxWait = new Label(reportStats[5]+" seconds");
        Label queue2minWait = new Label(reportStats[6]+" seconds");
        Label queue2avgWait = new Label(reportStats[7]+" seconds");

        VBox bothQueuesBox = new VBox();
        Label finalLength = new Label(reportStats[8]+" passengers");
        Label finalMaxWait = new Label(reportStats[9]+" seconds");
        Label finalMinWait = new Label(reportStats[10]+" seconds");
        Label finalAvgWait = new Label(reportStats[11]+" seconds");

        Label[] labelArray = new Label[]{length, maxWait, minWait, avgWait, queue1Heading,
                queue2Heading, bothQueuesHeading, queue1length, queue1maxWait,
                queue1minWait, queue1avgWait, queue2length, queue2maxWait, queue2minWait,
                queue2avgWait, finalLength, finalMaxWait, finalMinWait, finalAvgWait};

        for(int i = 0;i<labelArray.length;i++){
            if(i<=6){
                labelArray[i].setPrefSize(200,50);
                labelArray[i].setStyle("-fx-background-color: #00ad71; -fx-border-width: 2; " +
                        "-fx-border-style: solid; -fx-border-color: black; " +
                        "-fx-alignment: center; -fx-font: 16 arial; -fx-font-weight: bold;" +
                        " -fx-text-fill: black;");
                if(i>=4){
                    labelArray[i].setPrefSize(130,50);
                }
            }
            else if(i>6){
                labelArray[i].setPrefSize(130,50);
                labelArray[i].setStyle("-fx-background-color: #fcba03; -fx-border-width: 2; " +
                        "-fx-border-style: solid; -fx-border-color: black; " +
                        "-fx-alignment: center; -fx-font: 16 arial; -fx-font-weight: bold;" +
                        " -fx-text-fill: black;");
            }
        }
        queueDetails.getChildren().addAll(length, maxWait, minWait, avgWait);
        queue1Box.getChildren().addAll(queue1length, queue1maxWait, queue1minWait, queue1avgWait);
        queue2Box.getChildren().addAll(queue2length, queue2maxWait, queue2minWait, queue2avgWait);
        bothQueuesBox.getChildren().addAll(finalLength, finalMaxWait, finalMinWait, finalAvgWait);

        reportPane.add(queueDetails,2,4);
        reportPane.add(queue1Heading,3,3);
        reportPane.add(queue1Box,3,4);
        reportPane.add(queue2Heading,4,3);
        reportPane.add(queue2Box,4,4);
        reportPane.add(bothQueuesHeading,5,3);
        reportPane.add(bothQueuesBox,5,4);

        root.setLeft(reportPane);
        root.setCenter(displayBoardedTable(boardedPane));
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void sortPassengersInBoarded(Passenger[] trainQueueArray){
        for (int i = 0; i < trainQueueArray.length; i++) {
            for (int j = i + 1; j < trainQueueArray.length; j++) {
                if(trainQueueArray[i]==null || trainQueueArray[j]==null){
                    continue;
                }
                if ((Integer.parseInt(trainQueueArray[i].getSeatNumber()) > (Integer.parseInt(trainQueueArray[j].getSeatNumber())))) {
                    Passenger temp = trainQueueArray[i];
                    trainQueueArray[i] = trainQueueArray[j];
                    trainQueueArray[j] = temp;
                }
            }
        }
    }

    public void reportToFile(int[] reportStats) throws IOException {
        FileWriter writer = new FileWriter("src/CW2/report.txt");

        writer.write("Maximum Queue 1 Length : "+reportStats[0]);
        writer.write("\nMaximum Queue 1 Waiting Time : "+reportStats[1]);
        writer.write("\nMinimum Queue 1 Waiting Time : "+reportStats[2]);
        writer.write("\nAverage Queue 1 Waiting Time : "+reportStats[3]);

        writer.write("\n\nMaximum Queue 2 Length : "+reportStats[4]);
        writer.write("\nMaximum Queue 2 Waiting Time : "+reportStats[5]);
        writer.write("\nMinimum Queue 2 Waiting Time : "+reportStats[6]);
        writer.write("\nAverage Queue 2 Waiting Time : "+reportStats[7]);

        writer.write("\n\nMaximum Queue Length of both Queues : "+reportStats[8]);
        writer.write("\nMaximum Waiting Time of both Queues : "+reportStats[9]);
        writer.write("\nMinimum Waiting Time of both Queues : "+reportStats[10]);
        writer.write("\nAverage Queue Waiting Time of both Queues : "+reportStats[11]);

        writer.close();
    }

    public void runSimulation(String userInput, List<String> stationStops,
                              List<String> stationDetails,
                              List<List<String>> customerDetails) throws IOException {

        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1800, 700);
        stage.setTitle("Train Station Queue Application");

        AnchorPane waitingRoomPane = createPane("Waiting Room", "#fcba03");
        AnchorPane queuePane = createPane("Train Queue 1 and 2", "#00ad71");
        AnchorPane boardedPane = createPane("Boarded Passengers", "#3670b5");

        Pane buttonPane = new Pane();
        buttonPane.setPrefSize(1100, 145);
        Button simulateButton = new Button("Run Simulation");
        simulateButton.setPrefSize(120, 50);
        simulateButton.setStyle("-fx-font: 14 arial;");
        simulateButton.setLayoutX(800);
        simulateButton.setLayoutY(40);
        buttonPane.getChildren().addAll(simulateButton);

        PauseTransition wait1 = new PauseTransition(Duration.millis(1000));
        PauseTransition wait2 = new PauseTransition(Duration.millis(2000));
        PauseTransition wait3 = new PauseTransition(Duration.millis(3000));

        Passenger[] boardedPassenger = new Passenger[1];
        int[] reportStats = new int[12];
        simulateButton.setOnAction(event -> {
            if(checkWaitingRoom()==0 && trainQueue1.isEmpty() && trainQueue2.isEmpty()){
                selectStation(userInput, stationStops, stationDetails);
                loadCustomersFromBooking(stationDetails, customerDetails);
                waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
            }
            wait1.setOnFinished(event1 -> {
                while(checkWaitingRoom()>0) {
                    if(trainQueue1.isFull() && trainQueue2.isFull()){
                        break;
                    }
                    addPassengerToTrainQueue();
                    sortPassengersInQueue(trainQueue1, trainQueueArray1);
                    sortPassengersInQueue(trainQueue2, trainQueueArray2);
                    waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
                    trainQueueTableView1.setItems(getPassengersToTableView(trainQueueArray1));
                    trainQueueTableView2.setItems(getPassengersToTableView(trainQueueArray2));
                }
            });
            wait1.play();

            wait2.setOnFinished(event2 -> {
                reportStats[0] = trainQueue1.getMaxLength();
                reportStats[4] = trainQueue2.getMaxLength();
                reportStats[8] = trainQueue1.getMaxLength()+trainQueue2.getMaxLength();

                for(int i =0; i<trainQueueArray1.length;i++){
                    if(trainQueueArray1[i]!=null){
                        for(int j = 0;j<boardedPassengers.length;j++) {
                            if(boardedPassengers[j]==null) {
                                trainQueueArray1[i].setSecondsInQueue(randomNumberGenerator() + randomNumberGenerator() + randomNumberGenerator());
                                boardedPassenger[0] = trainQueue1.remove();
                                boardedPassengers[j] = boardedPassenger[0];
                                trainQueue1.setMaxStayInQueue(trainQueueArray1[i].getSecondsInQueue());
                                if (i == 0) {
                                    reportStats[2] = trainQueue1.getMinStayInQueue();
                                } else if (i == reportStats[0] - 1) {
                                    reportStats[1] = trainQueue1.getMaxStayInQueue();
                                }
                                trainQueueArray1[i] = null;
                                trainQueueTableView1.setItems(getPassengersToTableView(trainQueueArray1));
                                break;
                            }
                        }
                    }
                }
                // Getting average waiting time by dividing maxStayInQueue by maxLength
                reportStats[3] = reportStats[1]/reportStats[0];

                for(int i =0; i<trainQueueArray2.length;i++){
                    if(trainQueueArray2[i]!=null){
                        for(int j = 0;j<boardedPassengers.length;j++){
                            if(boardedPassengers[j]==null){
                                trainQueueArray2[i].setSecondsInQueue(randomNumberGenerator()+randomNumberGenerator()+randomNumberGenerator());
                                boardedPassenger[0] = trainQueue2.remove();
                                boardedPassengers[j] = boardedPassenger[0];
                                trainQueue2.setMaxStayInQueue(trainQueueArray2[i].getSecondsInQueue());
                                if(i==0){
                                    reportStats[6] = trainQueue2.getMinStayInQueue();
                                }
                                else if(i==reportStats[4]-1){
                                    reportStats[5] = trainQueue2.getMaxStayInQueue();
                                }
                                trainQueueArray2[i] = null;
                                trainQueueTableView2.setItems(getPassengersToTableView(trainQueueArray2));
                                break;
                            }
                        }
                    }
                }
                // Getting average waiting time by dividing maxStayInQueue by maxLength
                reportStats[7] = reportStats[5]/reportStats[4];

                // Adding maxStayInQueue in queue 1 and 2
                reportStats[9] = reportStats[1]+reportStats[5];

                // Adding minStayInQueue in queue 1 and 2
                reportStats[10] = reportStats[2]+reportStats[6];

                // Getting average waiting time of both queues by getting maxStayInQueue
                // and dividing by maxLength of both queues
                reportStats[11] = reportStats[9]/reportStats[8];

                sortPassengersInBoarded(boardedPassengers);
                boardedTableView.setItems(getPassengersToTableView(boardedPassengers));
            });
            wait2.play();

            wait3.setOnFinished(event3 -> stage.close());
            wait3.play();
        });
        root.setLeft(displayWaitingRoomTable(waitingRoomPane));
        root.setCenter(displayTrainQueueTable(queuePane));
        root.setRight(displayBoardedTable(boardedPane));
        root.setBottom(buttonPane);

        stage.setScene(scene);
        stage.showAndWait();
        displayReport(reportStats);
        reportToFile(reportStats);
    }
}
