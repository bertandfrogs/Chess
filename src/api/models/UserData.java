package api.models;

/**
 * The UserData model is how users' data is stored in the server. AuthTokens are not stored in the UserData model because they are non-permanent (they get created and deleted every session)
 */
public class UserData {
    String username;
    String password;
    String email;
}
