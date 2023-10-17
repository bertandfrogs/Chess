package api.models;

/**
 * The AuthToken class is used by the server to keep track of users who are logged in and playing games. New AuthTokens are generated by the DataAccess class whenever a user logs in. AuthTokens are connected with usernames.
 */
public class AuthToken {
    String authToken;
    String username;
}
