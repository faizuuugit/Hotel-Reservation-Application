package ui;

import api.AdminResource;
import model.*;
import api.HotelResource;
import service.ReservationService;
import java.util.*;

public class AdminMenu {
    private static final AdminResource adminResource = AdminResource.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void displayAdminMenu() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> seeAllCustomers();
                case 2 -> seeAllRooms();
                case 3 -> seeAllReservations();
                case 4 -> addRoom();
                case 5 -> addTestData();
                case 6 -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("==================================");
        System.out.println("Admin Menu:");
        System.out.println("1. See all Customers");
        System.out.println("2. See all Rooms");
        System.out.println("3. See all Reservations");
        System.out.println("4. Add a Room");
        System.out.println("5. Add Test Data");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void seeAllCustomers() {
        Collection<Customer> customers = adminResource.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            customers.forEach(System.out::println);
        }
    }

    private static void seeAllRooms() {
        Collection<IRoom> rooms = adminResource.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
        } else {
            rooms.forEach(System.out::println);
        }
    }

    private static void seeAllReservations() {
        adminResource.displayAllReservations();
    }

    private static void addRoom() {
        boolean addingMore = true;
        while (addingMore) {
            try {
                String roomNumber;
                while (true) {
                    System.out.print("Enter room number: ");
                    roomNumber = scanner.nextLine();

                    try {
                        Integer.parseInt(roomNumber);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room number. Please enter a valid integer.");
                    }
                }

                String finalRoomNumber = roomNumber;
                if (adminResource.getAllRooms().stream().anyMatch(r -> r.getRoomNumber().equals(finalRoomNumber))) {
                    System.out.println("Room " + roomNumber + " already exists. Please enter a different number.");
                    continue;
                }


                Double price;
                String priceInput;
                while (true) {
                    System.out.print("Enter price per night (0 for free): ");
                    priceInput = scanner.nextLine();
                    try {
                        price=Double.parseDouble(priceInput);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price. Please enter a number (e.g., 100.0 or 0).");

                    }
                }

                String typeInput;
                while (true){
                System.out.print("Enter room type (1 for SINGLE, 2 for DOUBLE): ");
                typeInput= scanner.nextLine();
                    try {
                        int typeInt = Integer.parseInt(typeInput);
                        if (typeInt == 1 || typeInt == 2) {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room type. Please enter 1 for SINGLE or 2 for DOUBLE.");
                    }
                    }
                RoomType type = typeInput.equals("1") ? RoomType.SINGLE : RoomType.DOUBLE;

                IRoom room = price == 0 ? new FreeRoom(roomNumber, type) : new Room(roomNumber, price, type);
                adminResource.addRoom(Collections.singletonList(room));
                System.out.println("Room added successfully.");

                System.out.print("Would you like to add another room? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                addingMore = "y".equals(response);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }



    private static void addTestData() {
        try {
            ReservationService.getInstance().addRoom(new Room("101", 100.0, RoomType.SINGLE));
            ReservationService.getInstance().addRoom(new Room("102", 150.0, RoomType.DOUBLE));
            ReservationService.getInstance().addRoom(new FreeRoom("103", RoomType.SINGLE));

            HotelResource hotelResource = HotelResource.getInstance();
            hotelResource.createACustomer("ravi@yahoo.com", "Ravi", "Mandapati");
            hotelResource.createACustomer("karthik@pspk.com", "Karthik", "Kalyan");
            hotelResource.createACustomer("mahesh@mb.in", "Mahesh", "Babu");

            Date today = new Date();
            Date checkIn1 = today;
            Date checkOut1 = new Date(today.getTime() + 2 * 86400000L);
            hotelResource.bookARoom("ravi@yahoo.com", hotelResource.getRoom("101"), checkIn1, checkOut1);

            Date checkIn2 = new Date(today.getTime() + 2 * 86400000L);
            Date checkOut2 = new Date(today.getTime() + 4 * 86400000L);
            hotelResource.bookARoom("karthik@pspk.com", hotelResource.getRoom("102"), checkIn2, checkOut2);

            System.out.println("Test data added successfully: 3 customers, 3 rooms, 2 reservations.");
        } catch (Exception e) {
            System.out.println("Error adding test data: " + e.getMessage());
        }
    }
}