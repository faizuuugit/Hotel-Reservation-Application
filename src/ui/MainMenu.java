package ui;

import api.HotelResource;
import model.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MainMenu {
    private static final HotelResource hotelResource = HotelResource.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String email_regex = "^[\\w+_.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> findAndReserveRoom();
                case 2 -> seeMyReservations();
                case 3 -> createAccount();
                case 4 -> AdminMenu.displayAdminMenu();
                case 5 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("==================================");
        System.out.println("Main Menu:");
        System.out.println("1. Find and reserve a room");
        System.out.println("2. See my reservations");
        System.out.println("3. Create an account");
        System.out.println("4. Admin");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void findAndReserveRoom() {
        try {
            Date checkIn = getValidDate("Enter check-in date (MM/dd/yyyy): ");
            Date checkOut = getValidDate("Enter check-out date (MM/dd/yyyy): ");
            if (checkIn.after(checkOut)) {
                System.out.println("Check-in date must be before check-out date.");
                return;
            }

            Map<Collection<IRoom>, Date[]> result = hotelResource.findAvailableRooms(checkIn, checkOut);
            Collection<IRoom> rooms = result.keySet().iterator().next();
            Date[] dates = result.values().iterator().next();

            if (rooms.isEmpty()) {
                System.out.println("No rooms available even with recommended dates.");
                return;
            }

            if (!dates[0].equals(checkIn)) {
                System.out.println("No rooms available for " + checkIn + " to " + checkOut);
                System.out.println("Searching recommended dates: " + dates[0] + " to " + dates[1]);
                String response;
                while(true){
                    System.out.print("Book on recommended dates? (y/n): ");
                    response = scanner.nextLine().trim().toLowerCase();
                    try{
                        if(response.charAt(0) =='y'|| response.charAt(0)=='n')
                            break;
                    }catch (NumberFormatException e) {
                        System.out.println("Invalid option. Please try again ");
                    }
                }

                if (!"y".equals(response)) {
                    System.out.println("Returning to menu.");
                    return;
                }
                checkIn = dates[0];
                checkOut = dates[1];
            }

            System.out.println("Available rooms: " + rooms);
            IRoom room = getValidRoomNumber(rooms);
            if (room == null) return;

            String email = getAccountStatus();
            if (email == null) return;
            if (!hotelResource.findARoom(checkIn, checkOut).contains(room)) {
                System.out.println("Room " + room.getRoomNumber() + " is no longer available for the selected dates.");
                return;
            }
            Reservation reservation = hotelResource.bookARoom(email, room, checkIn, checkOut);
            System.out.println("Reservation created: " + reservation);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private static IRoom getValidRoomNumber(Collection<IRoom> availableRooms) {
        while (true) {
            System.out.print("Enter room number to book: ");
            String roomNumber = scanner.nextLine().trim();
            IRoom room = hotelResource.getRoom(roomNumber);
            if (room == null || !availableRooms.contains(room)) {
                System.out.println("Invalid room number. Please enter a valid room number from the available list: " + availableRooms);
            } else {
                return room;
            }
        }
    }

    private static String getAccountStatus() {
        while (true) {
            System.out.print("Do you have an account? (y/n): ");
            String hasAccount = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(hasAccount)) {
                String email = getValidEmail("Enter your email: ");
                if (email == null) continue;
                if (hotelResource.getCustomer(email) == null) {
                    System.out.println("No account found for this email.");
                    System.out.print("Would you like to create an account? (y/n): ");
                    String createAccount = scanner.nextLine().trim().toLowerCase();
                    if ("y".equals(createAccount)) {
                        return createAccountOnTheFly(email);
                    } else if ("n".equals(createAccount)) {
                        System.out.println("Returning to menu.");
                        return null;
                    } else {
                        System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
                    }
                } else {
                    return email;
                }
            } else if ("n".equals(hasAccount)) {
                return createAccountOnTheFly();
            } else {
                System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    private static String getValidEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = scanner.nextLine().trim();
            if (Pattern.matches(email_regex, email)) {
                return email;
            } else {
                System.out.println("Invalid email format. Examples: name@gmail.com, name@cbi.in");
            }
        }
    }

    private static String createAccountOnTheFly() {
        return createAccountOnTheFly(null);
    }

    private static String createAccountOnTheFly(String prefilledEmail) {
        try {
            String email = prefilledEmail != null ? prefilledEmail : getValidEmail("Enter email: ");
            if (hotelResource.getCustomer(email) != null) {
                System.out.println("An account with this email already exists.");
                return null;
            }
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            hotelResource.createACustomer(email, firstName, lastName);
            System.out.println("Account created successfully.");
            return email;
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
            return null;
        }
    }

    private static void seeMyReservations() {
        try {
            String email = getValidEmail("Enter your email: ");
            Collection<Reservation> reservations = hotelResource.getCustomersReservations(email);
            if (hotelResource.getCustomer(email) == null) {
                System.out.println("No account found with email: "+ email);
            }
            else if (reservations == null || reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                reservations.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createAccount() {

        try {
            String email = getValidEmail("Enter email: ");
            if (hotelResource.getCustomer(email) != null) {
                System.out.println("An account with this email already exists.");
                return;
            }
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            hotelResource.createACustomer(email, firstName, lastName);
            System.out.println("Account created successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Date getValidDate(String prompt) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        sdf.set2DigitYearStart(new GregorianCalendar(2000, 0, 1).getTime());
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return sdf.parse(input);
            } catch (Exception e) {
                String currentDate = sdf.format(new Date());
                System.out.println("Invalid date format. Please use MM/dd/yyyy (e.g., " + currentDate + ").");
            }
        }
    }
}