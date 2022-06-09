package pt.tecnico.bicloin.app;

import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.util.List;
import java.util.Scanner;

public class AppMain {

	public static void main(String[] args) throws InterruptedException, ZKNamingException {

		// check arguments
		if (args.length < 3) {
			System.out.println("ERRO Argument(s) missing!");
			System.out.printf("Usage: java %s host port%n", AppMain.class.getName());
			return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		final String username = args[2];
		final String phone = args[3];
		String latitude = args[4];
		String longitude = args[5];


		App app = new App(zooHost, zooPort, "/grpc/bicloin/hub", username, phone,latitude, longitude );

		System.out.print("> ");

		Scanner in = new Scanner(System.in);

		while (in.hasNextLine()) {
			String[] cmdArgs = in.nextLine().split(" ");

			if (cmdArgs.length == 0 || cmdArgs[0].length() == 0 || cmdArgs[0].charAt(0) == '#')
			{
				System.out.print("> ");
				continue; // command is a comment
			}

			switch (cmdArgs[0]) {
				case "balance":
					try {
						System.out.printf("%s %d BIC%n", username, app.getBalance());
						break;
					} catch (StatusRuntimeException e) {
						System.out.println(e.getStatus().getDescription());
						break;
					}
				case "top-up":
					try {
						Integer amount = 10*Integer.parseInt(cmdArgs[1]);
						app.topUp(amount);
						System.out.printf("%s %d BIC%n", username, app.getBalance());

					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						System.out.println("ERRO Invalid argument, try again.");
					}
					catch (StatusRuntimeException e) {
						System.out.println(e.getStatus().getDescription());
					}

					break;

				case "tag":
					try {
						app.setTag(cmdArgs[3], new Coordinates(cmdArgs[1], cmdArgs[2]));
						System.out.println("OK");
					}
						catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("ERRO Not enough arguments, try again.");
							break;
						}
					 catch (Exception e) {
						 System.out.println("ERRO Invalid arguments, try again.");
						break;
					}
					break;

				case "move":

					try {
						if (cmdArgs.length == 3) { // if given coordinates
							app.setCoordinates(new Coordinates(cmdArgs[1], cmdArgs[2]));
							System.out.printf("%s em %s%n", username, app.getCoordinates().getGoogleMapsRepr());
							break;
						}
					} catch (NumberFormatException e) {
						System.out.println("ERRO. Coordinates not correctly formatted.");
						break;
					}
					if (cmdArgs.length == 2 && !app.tagExists(cmdArgs[1])){ // if tag given
						System.out.println(cmdArgs[1] + " is not defined.");
						break;
					}
					else
						app.setCoordinates(app.getTagCoordinates(cmdArgs[1]));
					System.out.printf("%s em %s%n", username, app.getCoordinates().getGoogleMapsRepr());
					break;
						// continues to at command
				case "at":
					System.out.printf("%s em %s%n", username, app.getCoordinates().getGoogleMapsRepr());
					break;
				case "scan":
					try {
						List<String> results = app.locateStation(
								app.getLatitude(),
								app.getLongitude(),
								Integer.valueOf(cmdArgs[1])
						);
						for (String line: results)
							System.out.println(line);
					}
					catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("ERRO Invalid argument, try again.");
						break;
					}
					catch (StatusRuntimeException e) {
						System.out.println(e.getStatus().getDescription());
						break;
					}
					break;

				case "info":
					try {
						System.out.println(app.infoStation(cmdArgs[1]));
					}
					catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("ERRO No station was given");
						break;
					}

					catch (StatusRuntimeException e) {
						System.out.println("ERRO "+ e.getStatus().getDescription());
					}

					catch (Exception e) {
						System.out.println("ERRO Fatal error occurred.");
					}
					break;
				case "bike-up":
					if (cmdArgs.length != 2) {
						System.out.println("ERRO Invalid argument. Try again.");
						break;
					}
					try {
						app.bikeUp(cmdArgs[1]);
						System.out.println("OK");
					}
					catch (StatusRuntimeException e) {
						System.out.println("ERRO "+ e.getStatus().getDescription());
					}
					break;

					case "bike-down":
					if (cmdArgs.length != 2) {
						System.out.println("ERRO Invalid argument. Try again.");
						break;
					}
					try {
						app.bikeDown(cmdArgs[1]);
						System.out.println("OK");
					}
					catch (StatusRuntimeException e) {
						System.out.println("ERRO "+e.getStatus().getDescription());
					}
					break;
				case "ping":
					try {
						System.out.println(app.ping());
					}
					catch (Exception e) {
						System.out.println("ERRO Ping return down");
					}
					break;

				case "sys-status":
					try {
						System.out.println(app.sysStatus());
					} catch (StatusRuntimeException e) {
						System.out.println(e.getStatus().getDescription());
					}
					catch (Exception e) {
						System.out.println("Fatal error ocurred with sys-status.");
					}
					break;
				case "help":
					System.out.println("Complete instructions for running this system available at the README.md file");
					break;
				case "zzz":
					try {
						Thread.sleep(Integer.valueOf(cmdArgs[1]));
					} catch (InterruptedException e) {
						System.out.println("Drank too much coffee. Can't sleep");
						throw e;
					}
					catch (Exception e) {
						System.out.println("Invalid argument try again");
					}
					break;
				default:
					System.out.println("Command not found. Try again.");
					break;

			}
			System.out.print("> ");

		}
		Thread.currentThread().interrupt();

	}

}

