import java.sql.*;
import java.util.Scanner;


public class JDBC {
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		int task=0;
		while(task!=8){
			System.out.println("What would you like to do? Please enter the corresponding number: ");
			System.out.println("1: Get Schedule of all trips for a given StartLocation, DestinationName, and Date");
			System.out.println("2: Edit the Trip Offering Table. This includes Deleting, Adding, and Updating");
			System.out.println("3: Display the stops of a given trip");
			System.out.println("4: Display the weekly schedule of a given driver and date");
			System.out.println("5: Add data to a table");
			System.out.println("6: Delete data from a table");
			System.out.println("7: Record actual data of given trip offering");
			System.out.println("8: Exit\n");
			task = keyboard.nextInt();
			keyboard.nextLine(); //clearing the buffer
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/PomonaTransitSystem", "Alpha", "16453");
				Statement stmt = con.createStatement();
				ResultSet rs;
				String tripNumber;
				String date;
				String scheduledStartTime;
				String tableName;
				switch(task) {
					case 1:
						System.out.print("Please enter StartLocationName: ");
						String startLocationName = keyboard.nextLine();
						System.out.print("Please enter DestinationName: ");
						String destinationName = keyboard.nextLine();
						System.out.print("Please enter Date(format is YYYY-MM-DD): ");
						date = keyboard.nextLine();
						try{
							rs = stmt.executeQuery(
									"SELECT T.StartLocationName, T.DestinationName, TT.Date, TT.ScheduledStartTime, TT.ScheduledArrivalTime, TT.DriverName, TT.BusID\r\n"
									+ "FROM TripOffering TT\r\n"
									+ "LEFT JOIN Trip T on TT.TripNumber = T.TripNumber\r\n"
									+ "WHERE T.StartLocationName = \'" + startLocationName + "\'\r\n"
									+ "AND T.DestinationName = \'" + destinationName + "\'\r\n"
									+ "AND TT.Date = \'" + date + "\';");
							printRSResult(rs, 8);
						}
						catch(Exception e) {
							System.out.println("Failed to find TripOffering...");
							e.printStackTrace();
						}
						break;
					case 2:
						System.out.println("How would you like to edit?:");
						System.out.println("1. Delete a Trip Offering");
						System.out.println("2. Add set of Trip Offerings");
						System.out.println("3. Edit specific field on a Trip Offering\n");
						int toEdit = keyboard.nextInt();
						keyboard.nextLine(); //clear buffer
						System.out.println();
						switch(toEdit) {
							case 1:
								System.out.print("Please enter TripNumber: ");
								tripNumber = keyboard.nextLine();
								System.out.print("Please enter Date(format is YYYY-MM-DD): ");
								date = keyboard.nextLine();
								System.out.print("Please enter ScheduledStartTime(format hh:mm:ss): ");
								scheduledStartTime = keyboard.nextLine();
								try {
									stmt.executeUpdate(
											"DELETE FROM TripOffering\r\n"
											+ "WHERE TripNumber = \'" + tripNumber + "\'\r\n"
											+ "AND Date = \'" + date + "\'\r\n"
											+ "AND ScheduledStartTime = \'" + scheduledStartTime + "\';");
									System.out.print("Delete Successful");
								}catch(Exception e) {
									System.out.print("Delete Failed");
									e.printStackTrace();
								}
								break;
							case 2:
								boolean cont = true;
								while(cont) {
									System.out.println("Please enter the TripOffering data in the following order:");
									System.out.println("TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID");
									System.out.print("Follow this format :(1, '2023-05-01', '07:00:00', '10:00:00', 'John Doe', 1): ");
									String tripToAdd = keyboard.nextLine();
									try{
										stmt.executeUpdate(
												"INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES\r\n"
												+ tripToAdd + ";");
										System.out.println("Adding Successful");
									}
									catch(Exception e) {
										System.out.println("Adding Failed");
										e.printStackTrace();
									}
									System.out.println("Would you like to add more?(answer with yes or no): ");
									tripToAdd = keyboard.nextLine();
									if(tripToAdd.toLowerCase().charAt(0)=='n') {
										cont = false;
									}
								}
								break;
							case 3:
								System.out.print("Please enter TripNumber: ");
								tripNumber = keyboard.nextLine();
								System.out.print("Please enter Date(format is YYYY-MM-DD): ");
								date = keyboard.nextLine();
								System.out.print("Please enter ScheduledStartTime(format hh:mm:ss): ");
								scheduledStartTime = keyboard.nextLine();
								System.out.print("Please enter the field to be changed(ie: DriverName, BusID): ");
								String field = keyboard.nextLine();
								System.out.print("Please enter the updated field value(ie: Dave, 23424): ");
								String fieldValue = keyboard.nextLine();
								try{
									stmt.executeUpdate(
											"UPDATE TripOffering\r\n"
											+ "SET " + field + "=\'" + fieldValue + "\'\r\n"
											+ "WHERE TripNumber = \'" + tripNumber + "\'\r\n"
											+ "AND date = \'" + date + "\'\r\n"
											+ "AND ScheduledStartTime = \'" + scheduledStartTime + "\';");
									System.out.println("Update Successful");
								}
								catch(Exception e) {
									System.out.println("Update Failed");
									e.printStackTrace();
								}
								break;
						}
						break;
					case 3:
						System.out.print("Please enter TripNumber: ");
						tripNumber = keyboard.nextLine();
						rs = stmt.executeQuery(
								"SELECT S.TripNumber, S.StopNumber, S.SequenceNumber, S.DrivingTime\r\n"
								+ "FROM TripStopInfo S\r\n"
								+ "WHERE S.TripNumber = \'" + tripNumber + "\';");
						printRSResult(rs, 5);
						break;
					case 4:
						System.out.print("Please enter DriverName: ");
						String driverName = keyboard.nextLine();
						System.out.print("Please enter Date(format is YYYY-MM-DD): ");
						date = keyboard.nextLine();
						rs = stmt.executeQuery(
								"SELECT T.StartLocationName, T.DestinationName, TT.Date, TT.ScheduledStartTime, TT.ScheduledArrivalTime, TT.DriverName, TT.BusID\r\n"
								+ "FROM TripOffering TT\r\n"
								+ "LEFT JOIN Trip T on TT.TripNumber = T.TripNumber\r\n"
								+ "WHERE TT.DriverName = \'" + driverName + "\'\r\n"
								+ "AND TT.Date = \'" + date + "\';");
						printRSResult(rs, 8);
						break;
					case 5:
						System.out.print("Please enter the Table name to add to: ");
						tableName = keyboard.nextLine();
						System.out.print("Please enter the value names following the general format: (<valuename>, <valuename>, ...): ");
						String valuesToAdd = keyboard.nextLine();
						System.out.print("Please enter the values following the general format: (<value>, <value>, ...): ");
						String dataToAdd = keyboard.nextLine();
						try{
							stmt.executeUpdate(
									"INSERT INTO " + tableName.toUpperCase() + " " + valuesToAdd + " VALUES\r\n"
									+ dataToAdd + ";");
							System.out.println("Adding Successful");
						}
						catch(Exception e) {
							System.out.println("Adding Failed");
							e.printStackTrace();
						}
						break;
					case 6:
						System.out.print("Please enter the Table name to delete from: ");
						tableName = keyboard.nextLine();
						System.out.print("Please enter a search clause (ie: BusId = 2)(add additional clauses by using AND <clause>): ");
						String searchClause = keyboard.nextLine();
						try {
							stmt.executeUpdate(
									"DELETE FROM " + tableName + "\r\n"
									+ "WHERE " + searchClause + ";");
							System.out.print("Delete Successful");
						}catch(Exception e) {
							System.out.print("Delete Failed");
							e.printStackTrace();
						}
						break;
					case 7:
						System.out.print("Please enter the TripNumber: ");
						tripNumber = keyboard.nextLine();
						System.out.print("Please enter the Actual Start Time(format hh:mm:ss): ");
						String actualStartTime = keyboard.nextLine();
						System.out.print("Please enter the Actual Arrival Time(format hh:mm:ss): ");
						String actualArrivalTime = keyboard.nextLine();
						System.out.print("Please enter the Number of Passengers that boarded: ");
						String numOfPassIn = keyboard.nextLine();
						System.out.print("Please enter the Number of Passengers that left: ");
						String numOfPassOut = keyboard.nextLine();
						try{
							rs = stmt.executeQuery(
									"SELECT T.StopNumber\r\n"
									+ "FROM TripStopInfo T\r\n"
									+ "WHERE T.TripNumber = " + tripNumber + ";");
							rs.next();
							//System.out.println("Hello");
							String stopNumber = rs.getString(1);
							//System.out.println("Hello1");
							rs = stmt.executeQuery(
									"SELECT TT.Date, TT.ScheduledStartTime, TT.ScheduledArrivalTime\r\n"
											+ "FROM TripOffering TT\r\n"
											+ "WHERE TT.TripNumber = \'" + tripNumber + "\';");
							rs.next();
							//System.out.println("Hello2");
							stmt.executeUpdate(
							        "INSERT INTO ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut) VALUES\r\n"
							                + "(" + tripNumber + ", '" + rs.getString(1) + "', '" + rs.getString(2) + "', "
							                + stopNumber + ", '" + rs.getString(3) + "', '" + actualStartTime + "', '"
							                + actualArrivalTime + "', " + numOfPassIn + ", " + numOfPassOut + ");");
							System.out.println("Adding Successful");
						}
						catch(Exception e) {
							System.out.println("Adding Failed");
							e.printStackTrace();
						}
						break;
				}
				System.out.println("\n");
				con.close();	
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void printRSResult(ResultSet rs, int lengthOfInput) throws SQLException {
		while(rs.next()){
			for(int i=1; i<lengthOfInput; i++) { //SQL starts at 1 for results
				System.out.print(rs.getString(i));
				if(i!=lengthOfInput-1) {
					System.out.print(", ");
				}
			}
			System.out.println();
		}
	}
}
