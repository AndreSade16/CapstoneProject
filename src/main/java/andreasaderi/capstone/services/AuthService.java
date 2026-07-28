package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.UnauthorizedException;
import andreasaderi.capstone.requestDTOs.LoginDTO;
import andreasaderi.capstone.security.JWTTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder bcrypt;
    private final JWTTools jwtTools;

    public AuthService(UserService userService, PasswordEncoder bcrypt, JWTTools jwtTools) {
        this.userService = userService;
        this.bcrypt = bcrypt;
        this.jwtTools = jwtTools;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {

        User foundUser = userService.findByEmail(body.email());

        if (this.bcrypt.matches(body.password(), foundUser.getPassword())) {
            return this.jwtTools.generateToken(foundUser);
        } else {
            throw new UnauthorizedException("Wrong credentials");
        }
    }
}
