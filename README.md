# Train-Seat-Booking-Program
A train seat booking program using Java and JavaFX for the UI. Additionally MongoDB is used to save/load all the booking data to/from the database.

1st part of the program is to book seats of a train travelling from Colombo to Badulla and the 2nd part of the program is to simulate a queue in the trainstation with the data gathered from the booking part.

## Prerequisites

 - Java JDK 8
 - JavaFX
 - MongoDB v4.2.8 or higher
 - IntelliJ IDEA

## Steps to run Booking locally

1. Run the <code>Booking.java</code> inside <code>src/CW1</code>
2. Inisde this console menu you can add, view, delete, find, sort customers by name and save/load booking data to a mongoDB database

## Steps to run Queue simulation locally

1. Do the booking part before running the simulation
2. Run the <code>TrainStation.java</code> inside <code>src/CW2</code>
3. Inside this console menu you can add, view, delete and save/load queue data to a mongoDB database. Also the option to simulate the passesngers boarding to the train.
