package oocl.travelassistant.handler;

import oocl.travelassistant.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 400);
        resp.put("message", ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UsernameExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleUsernameExists(UsernameExistsException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 409);
        resp.put("message", ex.getMessage());
        return resp;
    }

    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleEmailExists(EmailExistsException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 409);
        resp.put("message", ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 404);
        resp.put("message", ex.getMessage());
        return resp;
    }

    @ExceptionHandler(PasswordErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handlePasswordError(PasswordErrorException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 401);
        resp.put("message", ex.getMessage());
        return resp;
    }

}
