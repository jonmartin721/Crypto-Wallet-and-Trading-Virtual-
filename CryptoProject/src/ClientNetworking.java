/*
The purpose of this class is to be a purely static class used by the program to communicate with the server,
and to send data to save. We don't know yet what kind of data this will be, so for now I'll make data an int.
 */

public class ClientNetworking {


    //This method sends data of some kind to the server, any kind but save data.
    public static boolean sendData(int data) {
        return true;

    }

    //This method may be combined with the one above, the purpose of this method is to send save data.
    public static boolean sendSaveData(int data) {


        return true;

    }

    //This method will return the data being sent by the server.
    public static int receiveData() {

        return 0;

    }

    //This method will regularly check for connection status with the server.
    public static int getConnectionStatus() {

        return 0;

    }


}
