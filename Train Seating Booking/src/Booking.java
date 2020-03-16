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

        Label title = new Label("Welcome to Sri Lanka Railways Department");
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
        root.getChildren().addAll(title, details);
        consoleMenu(root, stage, scene);
    }

    public Label createSeat(int row, int column, int seatNumber){
        int XCord = 60;
        int YCord = 60;
        Label seat = new Label("S-"+(seatNumber));
        seat.setId(String.valueOf(seatNumber));
        seat.setPrefSize(50, 50);
        seat.setLayoutX(column * XCord);
        seat.setLayoutY(row * YCord);
        seat.setStyle("-fx-background-color: GREEN; -fx-border-width: 2; -fx-border-style: solid; -fx-border-color: black; -fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: black;");
        return seat;
    }

    public void selectDestination(Stage stage, ArrayList<String> tempDateLocationList) {
        Label title = new Label("Welcome to Sri Lanka Railways Department");
        title.setStyle("-fx-font: 30 arial; -fx-font-weight: bold; -fx-text-fill: black");
        title.setLayoutX(95);
        title.setLayoutY(5);

        Label details = new Label(
                "Train name - Denuwara Menike\n"+
                        "Class - 1st Class A/C Compartment\n");
        details.setStyle("-fx-font: 18 arial; -fx-text-fill: black; -fx-font-weight: bold");
        details.setLayoutX(250);
        details.setLayoutY(100);

        ComboBox<String> colomboToBadullaRoutes = new ComboBox<>();
        colomboToBadullaRoutes.setPromptText("Colombo to Badulla");
        colomboToBadullaRoutes.setPrefSize(150, 40);
        colomboToBadullaRoutes.setLayoutX(220);
        colomboToBadullaRoutes.setLayoutY(250);
        String[] colomboToBadullaArray = new String[]{"Polgahawela","Peradeniya Junction","Gampola","Nawalapitiya","Hatton","Thalawakele","Nanuoya","Haputale","Diyatalawa","Bandarawela","Ella","Badulla"};
        for(String item : colomboToBadullaArray) {
            colomboToBadullaRoutes.getItems().add(item);
        }

        ComboBox<String> badullaToColomboRoutes = new ComboBox<>();
        badullaToColomboRoutes.setPromptText("Badulla to Colombo");
        badullaToColomboRoutes.setPrefSize(150, 40);
        badullaToColomboRoutes.setLayoutX(420);
        badullaToColomboRoutes.setLayoutY(250);
        String[] badullaToColomboArray = new String[]{"Ella","Bandarawela","Diyatalawa","Haputale","Nanuoya","Thalawakele","Hatton","Nawalapitiya","Gampola","Peradeniya Junction","Polgahawela","Maradana","Colombo Fort"};
        for(String item : badullaToColomboArray) {
            badullaToColomboRoutes.getItems().add(item);
        }

        DatePicker selectDate = new DatePicker(LocalDate.now());
        selectDate.setLayoutX(310);
        selectDate.setLayoutY(320);
        selectDate.setDayCellFactory(restrictDate -> new DateCell(){
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                LocalDate presentDay = LocalDate.now();
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

        ToggleGroup destinationStart = new ToggleGroup();
        colomboStart.setToggleGroup(destinationStart);
        badullaStart.setToggleGroup(destinationStart);

        Button confirmDestination = new Button("Confirm Destination");
        confirmDestination.setPrefSize(150, 40);
        confirmDestination.setLayoutX(310);
        confirmDestination.setLayoutY(400);

        colomboStart.selectedProperty().addListener((observable, oldValue, newValue) -> {
            badullaToColomboRoutes.setDisable(true);
            colomboToBadullaRoutes.setDisable(false);
        });

        badullaStart.selectedProperty().addListener((observable, oldValue, newValue) -> {
            badullaToColomboRoutes.setDisable(false);
            colomboToBadullaRoutes.setDisable(true);
        });
        confirmDestination.setOnAction(event -> {
            if(colomboStart.selectedProperty().getValue().equals(true)){
                String endLocation = colomboToBadullaRoutes.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocationList.set(0,"1001");
                tempDateLocationList.set(1,bookedDate);
                tempDateLocationList.set(2,"Colombo");
                tempDateLocationList.set(3,endLocation);
                stage.close();
            }
            else if(badullaStart.selectedProperty().getValue().equals(true)){
                String endLocation = badullaToColomboRoutes.getSelectionModel().getSelectedItem();
                String bookedDate = selectDate.getValue().toString();
                tempDateLocationList.set(0,"1002");
                tempDateLocationList.set(1,bookedDate);
                tempDateLocationList.set(2,"Badulla");
                tempDateLocationList.set(3,endLocation);
                stage.close();
            }
        });

        Pane root1 = new Pane();
        root1.setStyle("-fx-background-color: #1b87c2");
        Scene scene1 = new Scene(root1, 820, 500);
        root1.getChildren().addAll(title, details, colomboStart, badullaStart, colomboToBadullaRoutes, badullaToColomboRoutes, selectDate, confirmDestination);
        stage.setScene(scene1);
        stage.showAndWait();
    }

    public void trainDestination(Pane root, Stage stage, Scene scene,ArrayList<String> tempDateLocationList, HashMap<Integer,String> tempcustomerList, HashMap<Integer,String> customerList, List<List<String>> colomboCustomers, List<List<String>> badullaCustomers, String userInput){
        switch(userInput){
            case "a":
                if(tempDateLocationList.get(0).equals("1001")){
                    addCustomerToSeat(root, stage, scene, customerList, colomboCustomers,tempDateLocationList, tempcustomerList);

                }
                else if(tempDateLocationList.get(0).equals("1002")){
                    addCustomerToSeat(root, stage, scene, customerList, badullaCustomers, tempDateLocationList, tempcustomerList);
                }
                break;
            case "v":
                System.out.println("v");
                if(tempDateLocationList.get(0).equals("1001")){
                    viewAllSeats(root, stage, scene, customerList, colomboCustomers, tempDateLocationList);
                }
                else if(tempDateLocationList.get(0).equals("1002")){
                    viewAllSeats(root, stage, scene, customerList, badullaCustomers, tempDateLocationList);
                }
                break;
            case "e":
                System.out.println("e");
                if(tempDateLocationList.get(0).equals("1001")){
                    displayEmptySeats(root, stage, scene, customerList, colomboCustomers, tempDateLocationList);
                }
                else if(tempDateLocationList.get(0).equals("1002")){
                    displayEmptySeats(root, stage, scene, customerList, badullaCustomers, tempDateLocationList);
                }
                break;
            default:
                break;
        }
    }

    public void addCustomerToSeat(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames, List<List<String>> customerDetails, ArrayList<String> tempDateLocationList, HashMap<Integer,String> tempcustomerList) {
        int seatNumber = 0;
        for(int row = 1; row <=6; row++){
            for(int column = 1; column <=7; column++){
                Label seat = createSeat(row, column, ++seatNumber);
                if(customerNames.size()<seatingCapacity) {
                    customerNames.put(seatNumber,"nb");
                }
                for (List<String> detail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (!detail.contains(tempDateLocationList.get(1)) && !detail.contains(String.valueOf(item))) {
                            customerNames.put(seatNumber, "nb");
                        }
                    }
                }
                for (List<String> customerDetail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (customerDetail.contains(tempDateLocationList.get(1)) && customerDetail.contains(String.valueOf(item))) {
                            customerNames.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                int selectedSeat = seatNumber;
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
                        tempcustomerList.put(item,name);
                    }
                }
                for(int item : tempcustomerList.keySet()){
                    List<String> newRecord = new ArrayList<>();
                    newRecord.add(tempDateLocationList.get(1));
                    newRecord.add(tempDateLocationList.get(2));
                    newRecord.add(tempDateLocationList.get(3));
                    newRecord.add((String.valueOf(item)));
                    newRecord.add(name);
                    customerDetails.add(newRecord);
                    System.out.println(tempcustomerList);
                }
                tempcustomerList.clear();
                System.out.println(tempcustomerList);
                System.out.println(customerDetails);
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
        root.getChildren().removeAll(root,emptySeat, bookedSeat, bookButton, clearButton);
    }

    public void viewAllSeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames, List<List<String>> customerDetails, ArrayList<String> tempDateLocationList) {
        int seatNumber = 0;
        for(int row = 1; row <=6; row++) {
            for (int column = 1; column <= 7; column++) {
                Label seat = createSeat(row, column, ++seatNumber);
                if (customerNames.size() < seatingCapacity) {
                    customerNames.put(seatNumber, "nb");
                }
                for (List<String> detail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (!detail.contains(tempDateLocationList.get(1)) && !detail.contains(String.valueOf(item))) {
                            customerNames.put(seatNumber, "nb");
                        }
                    }
                }
                for (List<String> customerDetail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (customerDetail.contains(tempDateLocationList.get(1)) && customerDetail.contains(String.valueOf(item))) {
                            customerNames.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                if(!customerNames.get(seatNumber).equals("nb")){
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
        root.getChildren().addAll(emptySeat,bookedSeat);
        stage.setScene(scene);
        stage.showAndWait();
        stage.close();
        root.getChildren().removeAll(root,emptySeat,bookedSeat);
    }

    public void displayEmptySeats(Pane root, Stage stage, Scene scene, HashMap<Integer,String> customerNames, List<List<String>> customerDetails, ArrayList<String> tempDateLocationList){
        int seatNumber = 0;
        for(int row = 1; row <=6; row++) {
            for (int column = 1; column <= 7; column++) {
                Label seat = createSeat(row, column, ++seatNumber);
                if (customerNames.size() < seatingCapacity) {
                    customerNames.put(seatNumber, "nb");
                }
                for (List<String> detail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (!detail.contains(tempDateLocationList.get(1)) && !detail.contains(String.valueOf(item))) {
                            customerNames.put(seatNumber, "nb");
                        }
                    }
                }
                for (List<String> customerDetail : customerDetails) {
                    for (int item : customerNames.keySet()) {
                        if (customerDetail.contains(tempDateLocationList.get(1)) && customerDetail.contains(String.valueOf(item))) {
                            customerNames.put(item, "b");
                        }
                    }
                }
                root.getChildren().add(seat);

                if(!customerNames.get(seatNumber).equals("nb")){
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

    public void saveToFile(Scanner scanner, HashMap<Integer,String> customerNames) throws IOException {
        System.out.print("Do you want to save the details to a text file(T) or store it in the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        if(choice.equals("t")) {
            FileWriter writer = new FileWriter("src/customerData.txt");
            for (int item : customerNames.keySet()) {
                writer.write(item + "=" + customerNames.get(item) + "\n");
            }
            writer.close();
            System.out.println("Successfully save to file");
        }
        else if(choice.equals("d")) {
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> collection = customerDatabase.getCollection("customerDetails");
            System.out.println("Connected to the Database");

            FindIterable<Document> findDocument = collection.find();
            if(collection.countDocuments()==0){
                for(int item: customerNames.keySet()) {
                    Document customerDocument = new Document();
                    customerDocument.append("seatNumber",String.valueOf(item));
                    customerDocument.append("customerName",customerNames.get(item));
                    collection.insertOne(customerDocument);
                }
                System.out.println("Successfully stored 1st time");
            }else if(collection.countDocuments()>1) {
                for (Document document : findDocument) {
                    collection.deleteOne(document);
                }
                for(int item: customerNames.keySet()) {
                    Document customerDocument = new Document();
                    customerDocument.append("seatNumber",String.valueOf(item));
                    customerDocument.append("customerName",customerNames.get(item));
                    collection.insertOne(customerDocument);
                }
                System.out.println("Successfully updated 2nd time");
            }
            mongoClient.close();
            System.out.println("Saved the details to the database successfully");

        }
        else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    public void loadFromFile(Scanner scanner, HashMap<Integer,String> customerNames) throws FileNotFoundException {
        System.out.print("Do you want to load the details from the text file(T) or retrieve it from the database(D). Please select(T/D) : ");
        String choice = scanner.next().toLowerCase();
        if(choice.equals("t")) {
            Scanner read = new Scanner(new File("src/customerData.txt"));
            while (read.hasNextLine()) {
                String line = read.nextLine();
                String[] pairs = line.split("=");
                customerNames.put(Integer.parseInt(pairs[0]), pairs[1]);
            }
            read.close();
            System.out.println("Successfully loaded from file");
        }
        else if(choice.equals("d")){
            MongoClient mongoClient = new MongoClient("localhost",27017);
            MongoDatabase customerDatabase = mongoClient.getDatabase("customers");
            MongoCollection<Document> collection = customerDatabase.getCollection("customerDetails");
            System.out.println("Connected to the Database");

            FindIterable<Document> findDocument = collection.find();
            for (Document document : findDocument) {
                customerNames.put(Integer.parseInt(document.getString("seatNumber")),document.getString("customerName"));
            }
            System.out.println(customerNames);
            mongoClient.close();
            System.out.println("Details loaded from the database successfully");
        }
        else {
            System.out.println("Invalid input. Please try again.");
        }
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
        HashMap<Integer,String> tempcustomerList = new HashMap<>(seatingCapacity);
        List<List<String>> colomboCustomers = new ArrayList<>();
        List<List<String>> badullaCustomers = new ArrayList<>();
        ArrayList<String> tempDateLocationList = new ArrayList<>(Arrays.asList("0","0","0","0"));
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
                case "v":
                case "e":
                    selectDestination(stage,tempDateLocationList);
                    trainDestination(root, stage, scene, tempDateLocationList, tempcustomerList, customerList, colomboCustomers, badullaCustomers, userInput);
                    break;
                case "d":
                    deleteCustomer(scanner, customerList);
                    break;
                case "f":
                    findCustomer(scanner, customerList);
                    break;
                case "s":
                    saveToFile(scanner, customerList);
                    break;
                case "l":
                    loadFromFile(scanner, customerList);
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
