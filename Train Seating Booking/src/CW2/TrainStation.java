package CW2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrainStation extends Application {
    private Passenger[] waitingRoom = new Passenger[42];
    private PassengerQueue trainQueue = new PassengerQueue();

    public static void main(String[] args) {
        launch(args);
    }

    public void consoleMenu(Stage stage, Pane root, Scene scene) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
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

            String userInput = scanner.next().toUpperCase();

            if(waitingRoom.length==0){
                loadCustomersFromBooking(scanner);
            }
            // Switch case used to check which inputs were taken
            switch(userInput){
                /* For add, view and empty welcomeScreen is used to select route, destination and date. Then inside
                   trainDestination the relevant methods for adding, viewing and viewing only empty seats are called. */
                case "A":
                    selectStation(stage, root, scene);
                    addPassenger();
                    break;
                case "V":
                    selectStation(stage, root, scene);
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
                    runSimulation();
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
    public void start(Stage primaryStage) throws FileNotFoundException {
        Stage stage = new Stage();
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #1b87c2");
        Scene scene = new Scene(root, 1000, 500);    // Size of the window
        stage.setTitle("Train Station CW2.Train Queue Application");
        consoleMenu(stage, root, scene);
    }

    public void selectStation(Stage stage, Pane root, Scene scene){
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void loadCustomersFromBooking(Scanner scanner) throws FileNotFoundException {
        while (true) {
            System.out.println("Please select from where you would like to load the customer details. Text file(T)/ Database(D) : ");
            String choice = scanner.nextLine().toUpperCase();
            if (choice.equals("T")) {
                Scanner read = new Scanner(new File("src/customerData.txt"));
                if(!read.hasNextLine()){
                    // If the file is empty gives an error
                    System.out.println("Error file is empty! Please save booking data to file before loading");
                }
                else {
                    // If the file already has data execute the code block below
                    while (read.hasNextLine()) { // Checks if each line has data Add each line to the variable line
                        String line = read.nextLine();
                        // Uses a string array to get each set of characters separated by "/" and the output looks like[Date, Start location,
                        // Destination, Seat number, Name]
                        String[] holdDetails = line.split(",");
                        // Creates a new List called details and adds each element from holdDetails up to 7 elements each time
                        //List<String> details = new ArrayList<>(Arrays.asList(holdDetails).subList(0, 8));
                        // Add each details List to colomboBadullaDetails List
                        //colomboBadullaDetails.add(details);
                    }
                }
                break;
            }
            else if (choice.equals("D")) {
                break;
            }
            else {
                System.out.println("Please enter a valid input and try again. Text file(T)/ Database(D).");
            }
        }
    }

    public void addPassenger(){}

    public void viewPassenger(){}

    public void deletePassenger(){}

    public void saveTrainQueue(){}

    public void loadTrainQueue(){}

    public void runSimulation(){}
}
