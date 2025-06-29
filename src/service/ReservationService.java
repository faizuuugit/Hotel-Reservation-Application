package service;

import model.Customer;
import model.IRoom;
import model.Reservation;

import java.util.*;

public class ReservationService {
    private static final ReservationService INSTANCE = new ReservationService();
    private final Map<String, IRoom> rooms = new HashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    private ReservationService() {}

    public static ReservationService getInstance() {
        return INSTANCE;
    }

    public void addRoom(IRoom room) {
        if (rooms.containsKey(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room " +room.getRoomNumber()+ " already exists.");
        }
        rooms.put(room.getRoomNumber(), room);
    }

    public IRoom getARoom(String roomId) {
        return rooms.get(roomId);
    }

    public Collection<IRoom> getAllRooms() {
        return rooms.values();
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        for (Reservation r : reservations) {
            if (r.getRoom().getRoomNumber().equals(room.getRoomNumber())) {
                boolean isOverlapping = !checkOutDate.before(r.getCheckInDate()) && !checkInDate.after(r.getCheckOutDate());

                if (isOverlapping) {
                    throw new IllegalArgumentException("Room " + room.getRoomNumber() + " is already booked for these dates."
                    );
                }
            }
        }
        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservations.add(reservation);
        return reservation;
    }

    public Map<Collection<IRoom>, Date[]> findAvailableRooms(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> presentRooms = findRooms(checkInDate, checkOutDate);

        if (presentRooms.isEmpty()) {

            Date newCheckIn = new Date(checkInDate.getTime() + 7 * 86400000L);
            Date newCheckOut = new Date(checkOutDate.getTime() + 7 * 86400000L);

            presentRooms = findRooms(newCheckIn, newCheckOut);

            return Map.of(presentRooms, new Date[]{newCheckIn, newCheckOut});
        }

        return Map.of(presentRooms, new Date[]{checkInDate, checkOutDate});
    }


    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        List<IRoom> availableRooms = new ArrayList<>(rooms.values());
        for (Reservation r : reservations) {

            if (!(checkOutDate.before(r.getCheckInDate()) || checkInDate.after(r.getCheckOutDate()))) {
                availableRooms.remove(r.getRoom());
            }
        }
        return availableRooms;
    }



    public Collection<Reservation> getCustomersReservation(Customer customer) {
        List<Reservation> customerReservations = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.toString().contains(customer.getEmail())) {
                customerReservations.add(r);
            }
        }
        return customerReservations;
    }

    public void printAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
        } else {
            reservations.forEach(System.out::println);
        }
    }
}