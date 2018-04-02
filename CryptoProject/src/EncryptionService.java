/*
The purpose of this class is to provide encryption services for each wallet.
It uses the techniques and most code from here: https://goo.gl/SSuy6a
 */

public class EncryptionService {

    private byte[] salt;
    private int derivedKeyLength;
    private byte[] encryptedAttemptedPassword;


    //TODO implement checking password
    //    This method returns the attempted password encrypted to be compared to a existing encrypted passwords.
    //    There is no need to do 2 way encryption, we will never need to see the plaintext password again.
    public byte[] getEncryptedAttemptedPassword(String password, byte[] salt) {

        return "Attempted password".getBytes();

    }


    //TODO implement salt generation
    //This generates a salt for applying onto the password.
    public byte[] generateSalt() {

        return "Hello".getBytes();

    }


    //TODO implement authentication
    public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt) {


        return true;

    }
}
