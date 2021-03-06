package utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Common utils for parsing url information.
 * 
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
    
    /**
     * Parse an input string to a proper URI instance.
     * 
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static URI parseURL(String url) throws URISyntaxException {
        return new URI(url);
    }

}
