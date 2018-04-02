/*
This class handles saving and loading data from file. It will be done on the server side and sent to the clients.
Data will be serialized out to a file. I'm not sure yet if each wallet will be its own file, or if all wallets will
be put into one file.
 */

public class FileOperations {

    //TODO -Amee-Serialize the wallet out to a file
    private Wallet saveData;

    //this method will save the wallet passed into it as a serialized object
    public boolean saveWallet(Wallet wallet) {

        return true;

    }

    //TODO -Amee-Deserialize a wallet from file into an object.
    public Wallet loadWallet() {


        return new Wallet("Bob", "Bobby", "Bob123");

    }

}
