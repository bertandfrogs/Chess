package server.services.old.registerUser;

/**
 * Manages the requests and responses to and from the server for the RegisterUser endpoint.
 * Registers a new user. Must check if a user exists in the database, and if not, add the user.
 * Receives a RegisterUserRequest, parses it, and returns a RegisterUserResponse.
 */
public class RegisterUserService {
    /**
     * @param request A RegisterUserRequest object to be sent to the server.
     * @return The RegisterUserResponse from the server.
     */
    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        return null;
    }
}
