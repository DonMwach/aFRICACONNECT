package org.mrgoogle.aFRICAUNITE;

import java.util.Random;

import javax.swing.JOptionPane;

import org.mindrot.jbcrypt.BCrypt;

public class App {

    // Shared state
    String username, email, password, otp;
    int generatedOTP;

    byte choice;

    int loginAttempts = 0;
    int signupPasswordAttempts = 0;
    int emailAttempts = 0;
    int otpRequests = 0;

    // Reusable objects (NO MORE new everywhere)
    Error error = new Error();
    Home home = new Home();

    public static void main(String[] args) {
        App app = new App();
        JOptionPane.showMessageDialog(null, "wELCOME Home!");
        app.gate();
    }

    // ========================= GATE =========================
    void gate() {

        String input = JOptionPane.showInputDialog("""
                rEGISTER your presence:
                    1. Login
                    2. Sign up""");

        try {
            choice = Byte.parseByte(input);

            switch (choice) {
                case 1 -> login();
                case 2 -> signUp();
                default -> {
                    error.wrongInputError();
                    gate();
                }
            }
        } catch (Exception e) {
            error.wrongInputError();
            gate();
        }
    }

    // ========================= HOME =========================
    class Home {
        void home() {
            JOptionPane.showInputDialog("""
                    wELCOME to aFRICA UNITE!
                        1. View Profile
                        2. View Posts
                        3. View Friends
                        4. Logout""");
        }
    }

    // ========================= LOGIN =========================
    void login() {

        String identifier = JOptionPane.showInputDialog("""
                lOGIN Here!
                    Enter your username or email:""");

        username = identifier;
        email = identifier;

        handlePassword();
    }

    void handlePassword() {

        password = JOptionPane.showInputDialog("eNTER your password:");

        String dbHash = Database.getPasswordHash(username, email);

        if (dbHash == null) {
            loginAttempts++;
            error.incorrectUsernamePassword();

            if (loginAttempts >= 2) {
                forgotPassword();
            } else {
                gate();
            }
            return;
        }

        if (BCrypt.checkpw(password, dbHash)) {
            JOptionPane.showMessageDialog(null, "lOGIN successful!");
            loginAttempts = 0;
            home.home();
        } else {
            loginAttempts++;
            error.incorrectPasswordError();

            if (loginAttempts >= 3) {
                forgotPassword();
            } else {
                handlePassword();
            }
        }
    }

    // ========================= FORGOT PASSWORD =========================
    void forgotPassword() {

        email = JOptionPane.showInputDialog("eNTER your email to reset:");

        if (!email.contains("@")) {
            emailAttempts++;
            error.invalidEmailError();

            if (emailAttempts >= 2) {
                emailAttempts = 0;
                gate();
            } else {
                forgotPassword();
            }
        } else {
            emailAttempts = 0;
            loginAttempts = 0;
            gate();
        }
    }

    // ========================= SIGN UP =========================
    void signUp() {

        username = JOptionPane.showInputDialog("Enter username:");

        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty!");
            signUp();
            return;
        }

        signUpEmail();
        signUpPassword();
        generateOTP();
    }

    void signUpEmail() {

        email = JOptionPane.showInputDialog("Enter email:");

        if (!email.contains("@")) {
            emailAttempts++;
            error.invalidEmailError();

            if (emailAttempts >= 2) {
                emailAttempts = 0;
                gate();
            } else {
                signUpEmail();
            }

        } else if (Database.userExists(email)) {
            error.emailExistsError();
            signUpEmail();
        }
    }

    void signUpPassword() {

        password = JOptionPane.showInputDialog("Enter password:");

        if (password.length() < 6) {
            signupPasswordAttempts++;
            error.weakPasswordError();

            if (signupPasswordAttempts >= 5) {
                signupPasswordAttempts = 0;
                gate();
            } else {
                signUpPassword();
            }
        }
    }

    // ========================= OTP =========================
    void generateOTP() {

        Random rand = new Random();
        generatedOTP = 100000 + rand.nextInt(900000);

        System.out.println("DEBUG OTP: " + generatedOTP);

        verifyOTP();
    }

    void verifyOTP() {

        otp = JOptionPane.showInputDialog("Enter OTP:");

        try {
            if (Integer.parseInt(otp) == generatedOTP) {

                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                Database.saveUser(username, email, hashed);

                JOptionPane.showMessageDialog(null, "Account created successfully!");
                login();

            } else {
                otpRequests++;
                error.invalidOTPError();

                if (otpRequests >= 3) {
                    error.otpRequestError();
                    otpRequests = 0;
                    gate();
                } else {
                    verifyOTP();
                }
            }

        } catch (NumberFormatException e) {
            otpRequests++;

            if (otpRequests >= 3) {
                error.otpRequestError();
                otpRequests = 0;
                gate();
            } else {
                JOptionPane.showMessageDialog(null, "Numbers only!");
                verifyOTP();
            }
        }
    }

    // ========================= ERRORS =========================
    class Error {

        void wrongInputError() {
            JOptionPane.showMessageDialog(null, "Wrong input. Choose 1 or 2.");
        }

        void incorrectUsernamePassword() {
            JOptionPane.showMessageDialog(null, "User not found!");
        }

        void incorrectPasswordError() {
            JOptionPane.showMessageDialog(null, "Incorrect password!");
        }

        void weakPasswordError() {
            JOptionPane.showMessageDialog(null, "Weak password!");
        }

        void invalidEmailError() {
            JOptionPane.showMessageDialog(null, "Invalid email!");
        }

        void emailExistsError() {
            JOptionPane.showMessageDialog(null, "Email already exists!");
        }

        void invalidOTPError() {
            JOptionPane.showMessageDialog(null, "Invalid OTP!");
        }

        void otpRequestError() {
            JOptionPane.showMessageDialog(null, "Too many OTP attempts!");
        }
    }
}