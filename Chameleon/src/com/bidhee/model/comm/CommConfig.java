package com.bidhee.model.comm;

/**
 * Created by JinjinLee on 1/6/16.
 */
public class CommConfig {

    public static final String SERVER_ADDRESS                       = "23.21.223.226";
    public static final String BASE_URL                             = "http://" + SERVER_ADDRESS +"/newbackend/app/index.php?action=";

//    public static final String SERVER_ADDRESS                       = "192.168.1.110";
//    public static final String BASE_URL                             = "http://" + SERVER_ADDRESS +"/prophytes/app/index.php?action=";

    public static final String PROPHYTES_API_GETBASEDATA            = "getBaseData";

    public static final String PROPHYTES_API_SIGNIN                 = "signin";
    public static final String PROPHYTES_API_GETPASSWORD            = "getPassword";
    public static final String PROPHYTES_API_SIGNUP                 = "signup";
    public static final String PROPHYTES_API_REGDEVTOKEN            = "registerDeviceToken";

    public static final String PROPHYTES_API_ADDPOST                = "addPost";
    public static final String PROPHYTES_API_GETPOSTS               = "getPosts";
    public static final String PROPHYTES_API_GETCOMMENTS            = "getComments";
    public static final String PROPHYTES_API_GETCOMMENTSWITHINFO    = "getCommentsWithInfo";
    public static final String PROPHYTES_API_ADDCOMMENT             = "addComment";
    public static final String PROPHYTES_API_GETCHAPTERS            = "getChapters";
    public static final String PROPHYTES_API_SEARCHCONNECT          = "searchConnect";
    public static final String PROPHYTES_API_GETCONNECTIONCOUNT     = "getConnectionCount";
    public static final String PROPHYTES_API_DELETEPOST             = "deletePost";
    public static final String PROPHYTES_API_REPORTPOST             = "reportPost";

    public static final String PROPHYTES_API_SAVEPROFILE            = "saveProfile";
    public static final String PROPHYTES_API_GETUSERPROFILE         = "getUserProfile";
    public static final String PROPHYTES_API_SENDREQUEST            = "sendRequest";
    public static final String PROPHYTES_API_GETUSERCONNECTIONLIST  = "getUserConnectionList";
    public static final String PROPHYTES_API_GETUSERCONNECTIONLIST_SEARCHKEY = "getUserConnectionListWithSearchKey";
    public static final String PROPHYTES_API_GETUSERPOSTS           = "getUserPosts";
    public static final String PROPHYTES_API_UPDATECONNECTION       = "updateConnection";
    public static final String PROPHYTES_API_GETNOTIFICATIONS       = "getNotifications";

    public static final String PROPHYTES_API_GETCHATROOMS           = "getChatRooms";
    public static final String PROPHYTES_API_ADDCHATROOM            = "addChatRoom";
    public static final String PROPHYTES_API_ADDCHATMEMBERS         = "addChatMembers";
    public static final String PROPHYTES_API_LOGOUTFROMCHATROOM     = "logoutFromChatRoom";
    public static final String PROPHYTES_API_GETRECENTCHATROOMS     = "getRecentChatRooms";

    public static final String PROPHYTES_API_GETUNREADCHATCOUNT     = "getUnReadChatCount";
    public static final String PROPHYTES_API_GETCHATS               = "getChats";
    public static final String PROPHYTES_API_GETCHATUSERS           = "getChatUsers";
    public static final String PROPHYTES_API_SENDCHAT               = "sendChat";

    public static final String PROPHYTES_API_UPLOADMEDIA            = "uploadMedia";
    public static final String PROPHYTES_API_GETBADGES              = "getBadges";
    public static final String PROPHYTES_API_REMOVECHATBADGES       = "removeChatBadges";
    public static final String PROPHYTES_API_REMOVENOTIBADGES       = "removeNotiBadges";

    public static final String PROPHYTES_API_GETCITY                = "getCity";
    public static final String PROPHYTES_API_GETPROFESSION          = "getProfession";
    public static final String PROPHYTES_API_GETGOVERNING           = "getGoverning";
    public static final String PROPHYTES_API_GETORGANIZATION        = "getOrganization";

    public static final boolean RESPONSE_SUCCEED                    = true;
    public static final boolean RESPONSE_FAILED                     = false;

    public static final String NETWORK_CONNECTION_ERROR             = "Please check your network status";
    public static final String NETWORK_PLEASE_WAIT                  = "Please wait ...";

    public static final int CONNECTION_TIME                         = 60000;
    public static final int SOCKET_PORT                             = 4281;
    public static final int SOCKET_RECONNECT_TIME                   = 60000;
    public static final int SOCKET_CONNECT_TIMEOUT                  = 15000;

}
