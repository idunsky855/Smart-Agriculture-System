package aii.logic.utilities;

import java.util.regex.Pattern;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;


public class EmailValidator {

    // Method to check if the email is valid
    public boolean isEmailValid(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate(); // Throws AddressException if invalid.

            // If passed jakarta validation, check edge cases by regex compiles:
            String emailRegex =
            "^" +
            "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~\\-]+" +
            "@" +
            "(?:[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*\\.)+" +
            "[a-zA-Z]{2,}" +
            "$";

            // Compile the regex
            Pattern p = Pattern.compile(emailRegex);

            // Check if email matches the pattern
            return email != null && p.matcher(email).matches();
        } catch (AddressException ex) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
