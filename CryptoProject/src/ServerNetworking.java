/*
This class performs the networking operations on the server side. I'm not sure how this is going to be implemented in
our project, we don't have all the details yet.
 */

public class ServerNetworking {

    private int networkData;
    private FileOperations fileOperations;

    //This method may be combined with the one above, the purpose of this method is to send save data.
    public static boolean receiveSaveData(int data) {

        return true;

    }

    //This method sends data of some kind to the server, any kind but save data.
    public boolean sendData(int data) {

        return true;

    }

    //This method will return the data being sent by the server.
    public int receiveData() {

        return 0;

    }

    //This method will regularly check for connection status with the server.
    public int getConnectionStatus() {

        return 0;

    }

}
