/*
 * mRGoogle aPR0926
 */

package org.mrgoogle.aFRICAUNITE;

import java.util.Random;

import javax.swing.JOptionPane;

import org.mindrot.jbcrypt.BCrypt;

public class App {
    
    static String input, username, email, password, otp;
    
    static byte choice;
    
    static int passwordAttempts = 0, generatedOTP;
    
    static int emailAttempts = 0;
    
    static int otpRequests = 0;
    
    public String getGreeting() {
        return """
               wELCOME Home! """;
    }

    public static void main(String[] args) {
        
        App obj = new App();
        
        JOptionPane.showMessageDialog(null, new App().getGreeting());
        
        /*
        *String hash = BCrypt.hashpw("12345", BCrypt.gensalt());
        *System.out.println(hash);
        */
       
        obj.gate();
    }
    
    //User initiates a start session.
    void gate(){
        
        App obj1 = new App();
        
        Error obj2 = new Error();
        
        input = JOptionPane.showInputDialog("""
                                    rEGISTER your presence: 
                                        1. Login 
                                        2. Sign up""");
        
         // Convert input String to byte
        choice = Byte.parseByte(input);
        
        switch (choice) {
            case 1 -> obj1.login();
            case 2 -> obj1.signUp();
            default -> {
                obj2.wrongInputError();
                    gate();
            }
        }
        
    }

    class Home{
        
        void home(){
            
            JOptionPane.showInputDialog("""
                                    wELCOME to aFRICA UNITE!
                                        1. View Profile
                                        2. View Posts
                                        3. View Friends
                                        4. Logout """);
            
        }

    }
    
    void login(){

        // Capture the input as one general "ID"
        String identifier = JOptionPane.showInputDialog("""
                                        lOGIN Here! 
                                            Enter your username or email: """);
        
        // Assign it to both so your password() method can use them
        username = identifier;
        email = identifier;
        
        password();
                                        
    }

    void password() {

        App obj1 = new App();
        Error obj2 = new Error();
        Home obj3 = new Home();

        password = JOptionPane.showInputDialog("""
                                               eNTER your password:""");

        String dbHash = Database.getPasswordHash(username, email); // <-- from DB

        if (dbHash == null) {
            passwordAttempts++;
            obj2.incorrectUsernamePassword();

            if (passwordAttempts >= 2) {
                forgotPassword();
            } else {
                obj1.gate();
            }

        }

        if (BCrypt.checkpw(password, dbHash)) {
            JOptionPane.showMessageDialog(null, """
                                                lOGIN successful! """);
            obj3.home();
        } else {
            passwordAttempts++;
            obj2.incorrectPasswordError();

            if (passwordAttempts >= 3) {
                forgotPassword();
            } else {
                password();
            }

        }
    }
   
    void forgotPassword(){

        Error obj2 = new Error();
        
        email = JOptionPane.showInputDialog(null, """
                                                  eNTER your email to reset your password: """);
        
        if(!email.contains("@")){
            emailAttempts++;
            obj2.invalidEmailError();

            if(emailAttempts >= 2){
                emailAttempts = 0;
                gate();
            } else {
                forgotPassword();
            }

        } else {
            emailAttempts = 0;
            passwordAttempts = 0;
            gate();
        }
    }

    void signUp() {
        
        username = JOptionPane.showInputDialog("""
                                        sIGN Up Here! 
                                            Enter your username: """);

        signUpEmail();
        signUpPassword();
        otpVerification();
        
    }
    
    void otpVerification(){
             
        Random rand = new Random();
        
        // 1. Generate a random 6-digit OTP (100000 to 999999)
        generatedOTP = 100000 + rand.nextInt(900000);

        // 2. Display it in a real-world scenario, you'd send this via email.
        System.out.println("""
                           DEBUG: Your OTP is  """ + generatedOTP);

        otpStatement();
        
    }
    
    void otpStatement(){
        
        Error obj2 = new Error();
        
        otp = JOptionPane.showInputDialog("""
                                          eNTER OTP code sent to your email: """);
        
        // 3. Convert input to integer and verify
        try {
            if (Integer.parseInt(otp) == generatedOTP) {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                Database.saveUser(username, email, hashed);

                JOptionPane.showMessageDialog(null, """
                                                    aCCOUNT created successfully!""");
                login();
            } else {
                otpRequests++;
                obj2.invalidOTPError();

                if(otpRequests >= 3){
                    obj2.otpRequestError();
                    otpRequests = 0;
                    gate();
                } 

            }
        } catch (NumberFormatException e) {
            otpRequests++; 
            
            if (otpRequests >= 3) {
                obj2.otpRequestError();
                otpRequests = 0;
                System.exit(0);
            }

            // If they haven't hit the limit, let them try again
            JOptionPane.showMessageDialog(null, "pLEASE enter numbers only");
            otpVerification(); 
        }
    }

    void signUpEmail(){

        Error obj2 = new Error();

        email = JOptionPane.showInputDialog("""
                                    sIGN Up Here! 
                                        Enter your email: """);

        if(!email.contains("@")){            
            emailAttempts++;
            obj2.invalidEmailError();

            if(emailAttempts >= 2){
                emailAttempts = 0;
                gate();
            } else {
                signUpEmail();
            }

        }else if(Database.userExists(email)){
            obj2.emailExistsError();
            signUpEmail();
        }

    }

    void signUpPassword(){

        Error obj2 = new Error();

        password = JOptionPane.showInputDialog("""
                                    sIGN Up Here! 
                                        Enter your password: """);

        if(password.length() < 6){
            passwordAttempts++;
            obj2.weakPasswordError();

            if(passwordAttempts >= 5){
                passwordAttempts = 0;
                gate();
            } else {
                signUpPassword();
            } 
            
        }

    }

    //Error Handling takes place here.
    class Error{
        
        void wrongInputError(){
            
            JOptionPane.showMessageDialog(null, """
                                                wRONG Input
                                                 Choose 1 or 2 """);
            
        }
        
        void incorrectUsernamePassword(){
            
            JOptionPane.showMessageDialog(null, """
                                                uSER not found!""");
            
        }

        void incorrectPasswordError(){
            
            JOptionPane.showMessageDialog(null, """
                                                iNCORRECT Password """);
            
        }
        
        void weakPasswordError(){
            
            JOptionPane.showMessageDialog(null, """
                                                wEAK Password """);
            
        }

        void invalidEmailError(){
            
            JOptionPane.showMessageDialog(null, """
                                                iNVALID Email """);
            
        }

        void emailExistsError(){
            
            JOptionPane.showMessageDialog(null, """
                                                eMAIL already exists!""");
            
        }
        
        void invalidOTPError(){
            
            JOptionPane.showMessageDialog(null, """
                                                iNVALID OTP!""");
            
        }
        
        void otpRequestError(){
            
            JOptionPane.showMessageDialog(null, """
                                                yOU have exhausted OTP request for today! """);
            
        }
        
    }
    
}