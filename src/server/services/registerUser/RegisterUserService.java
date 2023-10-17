package server.services.registerUser;

/**
 * Registers a new user.
 * Receives a RegisterUserRequest, parses it, and returns a RegisterUserResponse.
 * Must check if a user exists in the database, and if not, add the user.
 */
public class RegisterUserService {
    /**
     * @param request -- RegisterUserRequest containing username, password, and email.
     * @return RegisterUserResponse -- returns username, authToken
     */
    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        // if user already exists
            // response - error 403
        // else, create new user
        return null;
    }
}
