package oocl.travelassistant.handler;

import oocl.travelassistant.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 400);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UsernameExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleUsernameExists(UsernameExistsException ex) {
        logger.warn("UsernameExistsException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 409);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, Object> handleEmailExists(EmailExistsException ex) {
        logger.warn("EmailExistsException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 409);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleUserNotFound(UserNotFoundException ex) {
        logger.warn("UserNotFoundException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 404);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(PasswordErrorException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, Object> handlePasswordError(PasswordErrorException ex) {
        logger.warn("PasswordErrorException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 401);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handlePostNotFound(PostNotFoundException ex) {
        logger.warn("PostNotFoundException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 404);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleAccessForbidden(UnauthorizedAccessException ex) {
        logger.warn("UnauthorizedAccessException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 403);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleCommentNotFound(CommentNotFoundException ex) {
        logger.warn("CommentNotFoundException occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 404);
        resp.put(MESSAGE, ex.getMessage());
        return resp;
    }

    // 添加通用异常处理，捕获所有未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGenericException(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        Map<String, Object> resp = new HashMap<>();
        resp.put(CODE, 500);
        resp.put(MESSAGE, "Internal server error occurred");
        return resp;
    }
}
