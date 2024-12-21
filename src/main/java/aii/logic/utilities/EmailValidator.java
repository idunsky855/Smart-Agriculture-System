package aii.logic.utilities;

import java.util.regex.Pattern;

public class EmailValidator {
	
    // Method to check if the email is valid
    public boolean isEmailValid(String email) {
      
        // Regular expression to match valid email formats
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
        // Compile the regex
        Pattern p = Pattern.compile(emailRegex);
      
        // Check if email matches the pattern
        return email != null && p.matcher(email).matches();
    }

}
