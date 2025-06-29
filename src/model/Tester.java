package model;

public class Tester {
    public static void main(String[] args) {
        try {
            Customer customer = new Customer("first", "second", "j@domain.com");
            System.out.println(customer); // Should print: first second (j@domain.com)
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            Customer customer = new Customer("first", "second", "email");
            System.out.println(customer); // This line won't execute due to exception
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage()); // Should print: an Error stating Invalid email format...
        }
    }
}