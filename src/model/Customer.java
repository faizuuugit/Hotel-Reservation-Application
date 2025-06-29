package model;

import java.util.regex.Pattern;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final String email;
    private static final String email_regex = "^[\\w+_.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    public Customer(String firstName, String lastName, String email) {
        if (!Pattern.matches(email_regex, email)) {
            throw new IllegalArgumentException("Invalid email format. Example: name@gmail.com");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return firstName+" "+lastName+" ("+email +")";
    }
}