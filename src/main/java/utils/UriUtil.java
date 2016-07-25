package utils;

/**
 * Created by taihuynh on 25/7/16.
 */
public class UriUtil {

    /**
     * Some uri abstract UserInfo as username:password (i.e ftp://username:password@host:/path.
     * This method parse the exposed username and password.
     *
     * @param userInfo the piece of information contains username (and/without) password of the connection.
     * @return
     */
    public static String[] parseUserInfo(String userInfo) {
        String[] parsedInfo = new String[2];
        int splitIndex = 0;
        if ((splitIndex = userInfo.indexOf(":")) == - 1) {
            parsedInfo[0] = "anonymous";
            parsedInfo[1] = "";
            return parsedInfo;
        }

        parsedInfo[0] = userInfo.substring(0, splitIndex);
        parsedInfo[1] = userInfo.substring(splitIndex + 1);
        return parsedInfo;
    }
}
