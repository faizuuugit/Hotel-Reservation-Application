package api;
import java.util.Map;

import model.Customer;
import model.IRoom;
import model.Reservation;
import service.CustomerService;
import service.ReservationService;

import java.util.Collection;
import java.util.Date;

public class HotelResource {
    private static final HotelResource INSTANCE =new HotelResource();
    private static final CustomerService customerService = CustomerService.getInstance();
    private static final ReservationService reservationService = ReservationService.getInstance();

    private HotelResource(){}

    public static HotelResource getInstance(){

        return  INSTANCE;
    }
    public Customer getCustomer(String email) {

        return customerService.getCustomer(email);
    }

    public void createACustomer(String email, String firstName, String lastName) {
        customerService.addCustomer(email, firstName, lastName);
    }

    public IRoom getRoom(String roomNumber) {

        return reservationService.getARoom(roomNumber);
    }

    public Reservation bookARoom(String customerEmail, IRoom room, Date checkInDate, Date checkOutDate) {
        Customer customer = customerService.getCustomer(customerEmail);
        if (customer == null) throw new IllegalArgumentException("Customer not found.");
        return reservationService.reserveARoom(customer, room, checkInDate, checkOutDate);
    }

    public Collection<Reservation> getCustomersReservations(String customerEmail) {
        Customer customer = customerService.getCustomer(customerEmail);
        if (customer == null) return null;
        return reservationService.getCustomersReservation(customer);
    }

    //findARoom() --- Returns all rooms that are available for a specific date range
    public Collection<IRoom> findARoom(Date checkIn, Date checkOut) {
        return reservationService.findRooms(checkIn, checkOut);
    }


    /*findAvailableRooms() --- if no rooms are free for the selected dates, it shifts the dates by 7 days forward and checks again.*/
    public Map<Collection<IRoom>, Date[]> findAvailableRooms(Date checkInDate, Date checkOutDate) {
        return reservationService.findAvailableRooms(checkInDate, checkOutDate);
    }
}