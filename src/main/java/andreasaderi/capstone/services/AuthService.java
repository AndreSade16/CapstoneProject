package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.UnauthorizedException;
import andreasaderi.capstone.requestDTOs.LoginDTO;
import andreasaderi.capstone.requestDTOs.UserPasswordUpdateDTO;
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
        User foundUser;
        try {
            foundUser = userService.findByEmail(body.email());
        } catch (NotFoundException ex) {
            throw new UnauthorizedException("Wrong credentials.");
        }

        if (this.bcrypt.matches(body.password(), foundUser.getPassword())) {
            return this.jwtTools.generateToken(foundUser);
        } else {
            throw new UnauthorizedException("Wrong credentials.");
        }
    }

    public User updatePassword(User user, UserPasswordUpdateDTO body) {
        if (!this.bcrypt.matches(body.oldPassword(), user.getPassword()))
            throw new UnauthorizedException("Wrong credentials");
        if (!body.newPassword().equals(body.repeatNewPassword()))
            throw new ConflictException("Repeated password doesn't match the new password");
        if (this.bcrypt.matches(body.newPassword(), user.getPassword()))
            throw new ConflictException("This password was used recently, try to insert a new one");

        user.setPassword(bcrypt.encode(body.newPassword()));
        return userService.update(user);
    }
}
