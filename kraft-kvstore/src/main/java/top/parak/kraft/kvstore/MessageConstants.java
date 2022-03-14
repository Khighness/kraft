package top.parak.kraft.kvstore;

/**
 * @author KHighness
 * @since 2022-03-14
 * @email parakovo@gmail.com
 */
public class MessageConstants {

    public static final int MSG_TYPE_SUCCESS = 0;
    public static final int MSG_TYPE_FAILURE = 1;
    public static final int MSG_TYPE_REDIRECT = 2;
    public static final int MSG_TYPE_ADD_SERVER_COMMAND = 10;
    public static final int MSG_TYPE_REMOVE_SERVER_COMMAND = 11;
    public static final int MSG_TYPE_GET_COMMAND = 100;
    public static final int MSG_TYPE_GET_COMMAND_RESPONSE = 101;
    public static final int MSG_TYPE_SET_COMMAND = 102;

}
