/**
 * Very stupid-simple message class.  By implementing the Serializable
 * interface, objects of this class can be serialized automatically by
 * Java to be sent across IO streams.
 */


class Message implements java.io.Serializable
{
    // The text string encoded in this Message object */
    String theMessage;

    /**
     * Constructor.
     * @param _msg The string to be encoded in this Message object
     */

    Message(String _msg) {
	theMessage = _msg;
    }

}  //-- End class Message