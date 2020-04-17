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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;
import java.time.LocalDate;
import java.util.*;

public class TrainStation extends Application {
    private Passenger[] waitingRoom = new Passenger[42];
    private PassengerQueue trainQueue = new PassengerQueue();
    private Passenger[] trainQueueArray = trainQueue.getQueueArray();
    private Passenger[] boardedPassengers = new Passenger[42];
    private TableView<Passenger> waitingRoomTableView;
    private TableView<Passenger> trainQueueTableView;
    private TableView<Passenger> boardedTableView;

    public static void main(String[] args) {
        launch(args);
    }

    public void consoleMenu(){
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
                    if(checkWaitingRoom()==0 && trainQueue.isEmpty()) {
                        selectStation(userInput, stationStops, stationDetails);
                        loadCustomersFromBooking(stationDetails, customerDetails);
                    }
                    if(checkWaitingRoom()>0) {
                        addPassenger();
                    }
                    else {
                        System.out.println("No passengers booked for this date");
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
    public void start(Stage primaryStage){
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
            addPassengerToWaitingRoom(customerDetails);
        }
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
            waitingRoom[customerDetails.indexOf(customer)] = passenger;
        }
    }

    public void addPassengerToTrainQueue() {
        int randomQueueSize = randomNumberGenerator();
        System.out.println("Random number : "+randomQueueSize);
        for(int i = 0; i < randomQueueSize; i++){
            if(trainQueue.isFull()){
                break;
            }
            else if(checkWaitingRoom()==0){
                break;
            }
            for(int j = 0; j < waitingRoom.length; j++) {
                if (waitingRoom[j]!=null) {
                    waitingRoom[j].setSecondsInQueue(randomNumberGenerator()+randomNumberGenerator()+randomNumberGenerator());
                    trainQueue.add(waitingRoom[j]);
                    trainQueue.setMaxStayInQueue(waitingRoom[j].getSecondsInQueue());
                    waitingRoom[j] = null;
                    break;
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
            warning.setTitle("Queue is Full");
            warning.setHeaderText("Queue is Full!");
            warning.setContentText("Please remove passengers before adding to the queue");
        }
        warning.showAndWait();
    }

    public void sortPassengers(Passenger[] trainQueueArray){
        for(Passenger passenger : trainQueueArray) {
            if (passenger != null) {
                for (int i = 0; i < trainQueue.getLength(); i++) {
                    for (int j = i + 1; j < trainQueue.getLength(); j++) {
                        if ((Integer.parseInt(trainQueueArray[i].getSeatNumber()) > (Integer.parseInt(trainQueueArray[j].getSeatNumber())))) {
                            Passenger temp = trainQueueArray[i];
                            trainQueueArray[i] = trainQueueArray[j];
                            trainQueueArray[j] = temp;
                        }
                    }
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
        nicColumn.setMaxWidth(50);
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

    public Pane createPane(String title, String color){
        Pane pane = new Pane();
        pane.setPrefSize(590, 500);
        pane.setStyle("-fx-border-width: 2; -fx-border-style: solid; -fx-background-color:"+color);
        Label paneTitle = new Label(title);
        paneTitle.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black;");
        paneTitle.setLayoutX(200);
        paneTitle.setLayoutY(10);
        pane.getChildren().addAll(paneTitle);
        return pane;
    }

    public Pane displayWaitingRoomTable(Pane pane){
        waitingRoomTableView = new TableView<>();
        waitingRoomTableView.setPrefSize(588,502);
        waitingRoomTableView.setLayoutY(50);
        waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
        addTableColumns(waitingRoomTableView);
        pane.getChildren().addAll(waitingRoomTableView);

        return pane;
    }

    public Pane displayTrainQueueTable(Pane pane){
        trainQueueTableView = new TableView<>();
        trainQueueTableView.setPrefSize(515,502);
        trainQueueTableView.setLayoutX(2);
        trainQueueTableView.setLayoutY(50);
        trainQueueTableView.setItems(getPassengersToTableView(trainQueueArray));
        addTableColumns(trainQueueTableView);
        pane.getChildren().addAll(trainQueueTableView);

        return pane;
    }

    public Pane displayBoardedTable(Pane pane){
        boardedTableView = new TableView<>();
        boardedTableView.setPrefSize(515,502);
        boardedTableView.setLayoutX(2);
        boardedTableView.setLayoutY(50);
        boardedTableView.setItems(getPassengersToTableView(boardedPassengers));
        addTableColumns(boardedTableView);
        pane.getChildren().addAll(boardedTableView);

        return pane;
    }

    public void addPassenger(){
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1110, 700);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        Pane waitingRoomPane = createPane("Waiting Room","#fcba03");
        Pane trainQueuePane = createPane("Train Queue","#00ad71");

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
            sortPassengers(trainQueueArray);
            waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
            trainQueueTableView.setItems(getPassengersToTableView(trainQueueArray));
            if(trainQueue.isFull()){
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
        title.setPadding(new Insets(0, 0, 0, 250));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50, 50, 50, 50));
        grid.setHgap(10);
        grid.setVgap(10);
        Scene scene = new Scene(root, 1110, 560);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        int seatNumber = 0;
        for(int i = 0; i < 6; i++){
            for(int j = 0; j<7; j++){
                Label seat = new Label(String.valueOf(seatNumber++));
                seat.setPrefSize(120,50);
                seat.setStyle("-fx-background-color: GREY; -fx-border-width: 2; " +
                        "-fx-border-style: solid; -fx-border-color: black; " +
                        "-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
                grid.add(seat,j, i);
            }
        }
        root.setTop(title);
        root.setLeft(grid);

        stage.setScene(scene);
        stage.showAndWait();
    }

    public void deletePassenger(){
        Passenger[] tempArray = new Passenger[trainQueueArray.length];
        Passenger deletedPassenger = null;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the seat number of passenger to remove from the train queue : ");
        String seatSelected = scanner.nextLine();

        System.out.println("Before delete "+Arrays.toString(trainQueueArray));
        for(int i = 0,j=0;i < trainQueueArray.length;i++){
            if(trainQueueArray[i]==(null)){
                continue;
            }
            else if(!trainQueueArray[i].getSeatNumber().equals(seatSelected)) {
                tempArray[j++] = trainQueueArray[i];
                System.out.println("During delete " + Arrays.toString(tempArray));
            }
            else if(trainQueueArray[i].getSeatNumber().equals(seatSelected)){
                deletedPassenger = trainQueueArray[i];
            }
            trainQueueArray[i] = null;
        }

        System.out.println("After delete "+Arrays.toString(trainQueueArray));
        trainQueue.setFirstAndLast(0, 0);
        for(Passenger passenger : tempArray){
            trainQueue.add(passenger);
        }

        System.out.println("After add "+Arrays.toString(trainQueueArray));
        for(int i = 0; i < waitingRoom.length; i++){
            if(waitingRoom[i]==null){
                waitingRoom[i] = deletedPassenger;
                break;
            }
        }
        System.out.println("Waiting Room "+Arrays.toString(waitingRoom));
    }

    public void saveTrainQueue(){
        //Connecting to MongoDB then creating a database and then two collections for each train route
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> waitingRoomCollection = trainDatabase.getCollection("waitingRoomDetails");
        MongoCollection<Document> queueCollection = trainDatabase.getCollection("queueDetails");
        System.out.println("Connected to the Database");

        // Checks if the documents for each route stored in two separate collections has any document
        if(waitingRoomCollection.countDocuments() == 0 || queueCollection.countDocuments() == 0){
            if(waitingRoomCollection.countDocuments() == 0) {
                // Loops through each inner list in olomboCustomers to get [Date, Start location, Destination, Seat number, Name]
                for (Passenger passenger : waitingRoom) {
                    if (passenger != null) {
                        // Creates a new document
                        Document waitingRoomDocument = new Document();
                        // Gets the train number
                        waitingRoomDocument.append("train", passenger.getTrain());
                        // Gets the seat number
                        waitingRoomDocument.append("seat", passenger.getSeatNumber());
                        // Gets the NIC
                        waitingRoomDocument.append("NIC", passenger.getNic());
                        // Gets the first name
                        waitingRoomDocument.append("firstname", passenger.getFirstName());
                        // Gets the surname
                        waitingRoomDocument.append("surname", passenger.getSurname());
                        // Gets the date
                        waitingRoomDocument.append("date", passenger.getDate());
                        // Gets the boarding station
                        waitingRoomDocument.append("from", passenger.getFrom());
                        // Gets the destination
                        waitingRoomDocument.append("to", passenger.getTo());
                        // Gets the ticket number
                        waitingRoomDocument.append("ticket", passenger.getTicketId());
                        // Add the document to the collection
                        waitingRoomCollection.insertOne(waitingRoomDocument);
                    }
                }
            }
            if(queueCollection.countDocuments() == 0) {
                // Loops through each inner list in olomboCustomers to get [Date, Start location, Destination, Seat number, Name]
                for (Passenger passenger : trainQueueArray) {
                    if (passenger != null) {
                        // Creates a new document
                        Document queueDocument = new Document();
                        // Gets the train number
                        queueDocument.append("train", passenger.getTrain());
                        // Gets the seat number
                        queueDocument.append("seat", passenger.getSeatNumber());
                        // Gets the NIC
                        queueDocument.append("NIC", passenger.getNic());
                        // Gets the first name
                        queueDocument.append("firstname", passenger.getFirstName());
                        // Gets the surname
                        queueDocument.append("surname", passenger.getSurname());
                        // Gets the date
                        queueDocument.append("date", passenger.getDate());
                        // Gets the boarding station
                        queueDocument.append("from", passenger.getFrom());
                        // Gets the destination
                        queueDocument.append("to", passenger.getTo());
                        // Gets the ticket number
                        queueDocument.append("ticket", passenger.getTicketId());
                        // Gets the seconds in queue
                        queueDocument.append("seconds", String.valueOf(passenger.getSecondsInQueue()));
                        // Add the document to the collection
                        queueCollection.insertOne(queueDocument);
                    }
                }
            }
        }
        // Checks if the documents for each route stored in two separate collections has 1 or more documents
        else if(waitingRoomCollection.countDocuments() > 0 || queueCollection.countDocuments() > 0){
            if(waitingRoomCollection.countDocuments() > 0) {
                // Gets all the documents in colomboCollection train route into findColomboDocument
                FindIterable<Document> findWaitingRoomDocument = waitingRoomCollection.find();

                // Loops through each document in colomboCollection and deletes them
                for (Document document : findWaitingRoomDocument) {
                    waitingRoomCollection.deleteOne(document);
                }
                // Loops through each inner list in colomboCustomers to get [Date, Start location, Destination,
                // Seat number, Name]
                for (Passenger passenger : waitingRoom) {
                    if (passenger != null) {
                        // Creates a new document
                        Document waitingRoomDocument = new Document();
                        // Gets the train number
                        waitingRoomDocument.append("train", passenger.getTrain());
                        // Gets the seat number
                        waitingRoomDocument.append("seat", passenger.getSeatNumber());
                        // Gets the NIC
                        waitingRoomDocument.append("NIC", passenger.getNic());
                        // Gets the first name
                        waitingRoomDocument.append("firstname", passenger.getFirstName());
                        // Gets the surname
                        waitingRoomDocument.append("surname", passenger.getSurname());
                        // Gets the date
                        waitingRoomDocument.append("date", passenger.getDate());
                        // Gets the boarding station
                        waitingRoomDocument.append("from", passenger.getFrom());
                        // Gets the destination
                        waitingRoomDocument.append("to", passenger.getTo());
                        // Gets the ticket number
                        waitingRoomDocument.append("ticket", passenger.getTicketId());
                        // Add the document to the collection
                        waitingRoomCollection.insertOne(waitingRoomDocument);
                    }
                }
            }
            if(queueCollection.countDocuments() > 0) {
                // Gets all the documents in colomboCollection train route into findColomboDocument
                FindIterable<Document> findQueueDocument = queueCollection.find();

                // Loops through each document in colomboCollection and deletes them
                for (Document document : findQueueDocument) {
                    queueCollection.deleteOne(document);
                }
                // Loops through each inner list in colomboCustomers to get [Date, Start location, Destination,
                // Seat number, Name]
                for (Passenger passenger : trainQueueArray) {
                    if (passenger != null) {
                        // Creates a new document
                        Document queueDocument = new Document();
                        // Gets the train number
                        queueDocument.append("train", passenger.getTrain());
                        // Gets the seat number
                        queueDocument.append("seat", passenger.getSeatNumber());
                        // Gets the NIC
                        queueDocument.append("NIC", passenger.getNic());
                        // Gets the first name
                        queueDocument.append("firstname", passenger.getFirstName());
                        // Gets the surname
                        queueDocument.append("surname", passenger.getSurname());
                        // Gets the date
                        queueDocument.append("date", passenger.getDate());
                        // Gets the boarding station
                        queueDocument.append("from", passenger.getFrom());
                        // Gets the destination
                        queueDocument.append("to", passenger.getTo());
                        // Gets the ticket number
                        queueDocument.append("ticket", passenger.getTicketId());
                        // Gets the seconds in queue
                        queueDocument.append("seconds", String.valueOf(passenger.getSecondsInQueue()));
                        // Add the document to the collection
                        queueCollection.insertOne(queueDocument);
                    }
                }
            }
        }
        mongoClient.close(); // Closes the database connection
        System.out.println("Saved the details to the database successfully");
    }

    public void loadTrainQueue(){
        //Connecting to MongoDB then creating a database and then two collections for each train route
        MongoClient mongoClient = new MongoClient("localhost",27017);
        MongoDatabase trainDatabase = mongoClient.getDatabase("trainStation");
        MongoCollection<Document> waitingRoomCollection = trainDatabase.getCollection("waitingRoomDetails");
        MongoCollection<Document> queueCollection = trainDatabase.getCollection("queueDetails");
        System.out.println("Connected to the Database");

        // Gets all the documents in colomboCollection train route into findColomboDocument
        FindIterable<Document> findWaitingRoomDocument = waitingRoomCollection.find();

        // Loops through each document in colomboColletion and adds each value from the keys to colomboCustomers
        // and colomboBadullaDetails List
        Arrays.fill(waitingRoom, null);
        for(Document document : findWaitingRoomDocument){
            for(int i = 0; i < waitingRoom.length; i++) {
                if(waitingRoom[i]==null) {
                    Passenger passenger = new Passenger();
                    passenger.setTrain(document.getString("train"));
                    passenger.setSeatNumber(document.getString("seat"));
                    passenger.setNic(document.getString("NIC"));
                    passenger.setName(document.getString("firstname"), document.getString("surname"));
                    passenger.setDate(document.getString("date"));
                    passenger.setFrom(document.getString("from"));
                    passenger.setTo(document.getString("to"));
                    passenger.setTicketId(document.getString("ticket"));
                    waitingRoom[i] = passenger;
                    break;
                }
            }
        }

        // Gets all the documents in colomboCollection train route into findColomboDocument
        FindIterable<Document> findQueueDocument = queueCollection.find();

        Arrays.fill(trainQueueArray, null);
        for(Document document : findQueueDocument){
            Passenger passenger = new Passenger();
            passenger.setTrain(document.getString("train"));
            passenger.setSeatNumber(document.getString("seat"));
            passenger.setNic(document.getString("NIC"));
            passenger.setName(document.getString("firstname"), document.getString("surname"));
            passenger.setDate(document.getString("date"));
            passenger.setFrom(document.getString("from"));
            passenger.setTo(document.getString("to"));
            passenger.setTicketId(document.getString("ticket"));
            passenger.setSecondsInQueue(Integer.parseInt(document.getString("seconds")));
            trainQueue.add(passenger);
        }
        mongoClient.close(); // Closes the database connection
        System.out.println("Details loaded from the database successfully");
    }

    public void runSimulation(String userInput, List<String> stationStops, List<String> stationDetails, List<List<String>> customerDetails) {
        Stage stage = new Stage();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1600, 700);    // Size of the window
        stage.setTitle("Train Station Queue Application");

        Pane waitingRoomPane = createPane("Waiting Room", "#fcba03");
        Pane queuePane = createPane("Train Queue", "#00ad71");
        Pane boardedPane = createPane("Boarded Passengers", "#3670b5");

        Pane buttonPane = new Pane();
        buttonPane.setPrefSize(1100, 145);
        Button addButton = new Button("Run Simulation");
        addButton.setPrefSize(120, 50);
        addButton.setStyle("-fx-font: 14 arial;");
        addButton.setLayoutX(530);
        addButton.setLayoutY(40);
        buttonPane.getChildren().addAll(addButton);

        PauseTransition wait1 = new PauseTransition(Duration.millis(500));
        PauseTransition wait2 = new PauseTransition(Duration.millis(1000));

        Passenger[] boardedPassenger = new Passenger[1];
        int[] reportStats = new int[4];
        addButton.setOnAction(event -> {
            if(checkWaitingRoom()==0 && trainQueue.isEmpty()){
                selectStation(userInput, stationStops, stationDetails);
                loadCustomersFromBooking(stationDetails, customerDetails);
                waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
            }
            wait1.setOnFinished(event1 -> {
                while(checkWaitingRoom()>0) {
                    addPassengerToTrainQueue();
                    sortPassengers(trainQueueArray);
                    waitingRoomTableView.setItems(getPassengersToTableView(waitingRoom));
                    trainQueueTableView.setItems(getPassengersToTableView(trainQueueArray));
                }
                reportStats[0] = trainQueue.getLength();    // Maximum queue attained
                reportStats[1] = trainQueue.getMaxStayInQueue();    // Maximum waiting time
                reportStats[2] = trainQueue.getMinStayInQueue();    // Minimum waiting time
                reportStats[3] = trainQueue.getMaxStayInQueue()/trainQueue.getLength(); // Average waiting time
            });
            wait1.playFromStart();

            wait2.setOnFinished(event2 -> {
                for(int i =0; i<trainQueueArray.length;i++){
                    if(trainQueueArray[i]!=null){
                        boardedPassenger[0] = trainQueue.remove();
                        boardedPassengers[i] = boardedPassenger[0];
                        trainQueueArray[i] = null;
                        trainQueueTableView.setItems(getPassengersToTableView(trainQueueArray));
                        boardedTableView.setItems(getPassengersToTableView(boardedPassengers));
                    }
                }
                System.out.println("Max length : "+reportStats[0]);
                System.out.println("Max waiting : "+reportStats[1]);
                System.out.println("Min waiting : "+reportStats[2]);
                System.out.println("Avg waiting : "+reportStats[3]);
            });
            wait2.playFromStart();
        });

        root.setLeft(displayWaitingRoomTable(waitingRoomPane));
        root.setCenter(displayTrainQueueTable(queuePane));
        root.setRight(displayBoardedTable(boardedPane));
        root.setBottom(buttonPane);

        stage.setScene(scene);
        stage.showAndWait();
    }
}
