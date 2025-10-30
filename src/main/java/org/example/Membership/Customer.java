package org.example.Membership;

import java.time.LocalDate;

public class Customer {

    private final String socialSecurityNumber;
    private final String emailAddress;
    private final int age; //så jag kan göra andra typer av rabatter
    private final boolean student;
    // samling av checkar


    // gör member till egen grej
    private Membership membership;

    // ev? lista av köphistorik

    public Customer(String socialSecurityNumber, String emailAddress) {
        this(socialSecurityNumber, emailAddress, 18, false);
    }

    public Customer(String socialSecurityNumber, String emailAddress, int age, boolean student) {
        validateSSN(socialSecurityNumber);
        validateEmail(emailAddress);

        // validate SSN & email
        this.socialSecurityNumber = socialSecurityNumber;
        this.emailAddress = emailAddress;
        membership = null;
        this.age = age;
        this.student = student;
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        int atIndex = email.indexOf('@');
        int lastAtIndex = email.lastIndexOf('@');
        if (atIndex == -1 || atIndex != lastAtIndex) {
            throw new IllegalArgumentException("Email must contain exactly one @");
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);

        validateLocalPart(localPart);
        validateDomainPart(domainPart);
    }

    private void validateLocalPart(String localPart) {
        if (localPart.isEmpty() || localPart.length() > 64) {
            throw new IllegalArgumentException("Invalid local part length");
        }
        if (localPart.startsWith(".")) {
            throw new IllegalArgumentException("Local part cannot start with dot");
        }
        if (localPart.contains("..")) {
            throw new IllegalArgumentException("Local part cannot have consecutive dots");
        }

        boolean insideQuotes = false;

        for (int i = 0; i < localPart.length(); i++) {
            char c = localPart.charAt(i);
            if (c == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }
            if (c == '\\') {
                i++;
                continue;
            }
            if (insideQuotes) {
                continue;
            }
            if (!isValidLocalPartChar(c)) {
                throw new IllegalArgumentException("Invalid character in local part: " + c);
            }
        }
    }

    private boolean isValidLocalPartChar(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            return true;
        }
        if ("!#$%&'*+-/=?^_`{|}~.".contains(String.valueOf(c))) {
            return true;
        }
        return false;
    }

    private void validateDomainPart(String domainPart) {
        if (domainPart.isEmpty() || domainPart.length() > 255) {
            throw new IllegalArgumentException("Invalid domain part length");
        }
        if (!domainPart.contains(".")) {
            throw new IllegalArgumentException("Domain must have at least one dot");
        }
        if (domainPart.startsWith(".") || domainPart.endsWith(".") || domainPart.startsWith("-")) {
            throw new IllegalArgumentException("Invalid domain format");
        }
        String[] labels = domainPart.split("\\.");

        for (String label : labels) {
            if (label.length() > 63) {
                throw new IllegalArgumentException("Invalid domain label length");
            }
            if (label.equals(labels[labels.length - 1]) && label.length() < 2) {
                throw new IllegalArgumentException("Domain label must be at least 2 characters");
            }
            for (char c : label.toCharArray()) {
                if (!isValidDomainPartChar(c)) {
                    throw new IllegalArgumentException("Invalid character in domain part: " + c);
                }
            }
        }
    }

    private boolean isValidDomainPartChar(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            return true;
        }
        if ("!#$%&'*+-/=?^`{|}~.".contains(String.valueOf(c))) {
            return true;
        }
        return false;
    }

    private void validateSSN(String ssn) {
        if (ssn == null || !ssn.matches("^[0-9]{12}$")) {
            throw new IllegalArgumentException("Invalid format");
        }
        if (!validateDate(ssn.substring(0,8))) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!validateCheckDigit(ssn)) {
            throw new IllegalArgumentException("Invalid check digit");
        }
    }

    private boolean validateDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        if (year < 1582 || month < 1 || month > 12 || day < 1 || day > 31) {
            return false;
        }

        if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30){
            return false;
        }

        if (month == 2) {
            boolean leapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if ((leapYear && day > 29) || (!leapYear && day > 28)) {
                return false;
            }
        }

        return true;
    }


    private boolean validateCheckDigit(String checkDigit) {

        int nSum = 0;
        boolean isSecond = false;
        for (int i = checkDigit.length() - 1; i >= 0; i--){

            int d = checkDigit.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    //membership
    public Membership getMembership() {
        if (membership == null || membership.getExpirationDate().isBefore(LocalDate.now())) {
            membership = null;                              // tas den bort om du glömt att renew:a den, tappar då alla poäng
            return null;
        }
        return membership;
    }

    public void becomeMember() {
        if (membership == null) {
            LocalDate age = LocalDate.of(
                    Integer.parseInt(socialSecurityNumber.substring(0,4))+18, //flyttal
                    Integer.parseInt(socialSecurityNumber.substring(4,6)),
                    Integer.parseInt(socialSecurityNumber.substring(6,8))
            );
            if (age.isBefore(LocalDate.now())) {
                membership = new Membership(this);
            }
        }
    }

    public int getAge(){return age;}

    public boolean isStudent(){return student;}

    protected void cancelMembership() {
        membership = null;
    }
}
