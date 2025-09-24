package oocl.travelassistant.handler;

import oocl.travelassistant.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 400);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UsernameExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleUsernameExists(UsernameExistsException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 409);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleEmailExists(EmailExistsException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 409);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 404);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(PasswordErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handlePasswordError(PasswordErrorException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 401);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handlePostNotFound(PostNotFoundException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 404);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleAccessForbidden(UnauthorizedAccessException ex) {
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 403);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

}
