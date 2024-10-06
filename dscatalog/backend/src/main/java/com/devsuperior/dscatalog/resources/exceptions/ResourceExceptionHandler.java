package com.devsuperior.dscatalog.resources.exceptions;

import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

//    @Autowired
//    private CategoryRepository categoryRepository;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e , HttpServletRequest request){
HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Resource Not Found");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status.value()).body(err);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardError> dataBaseException (DataBaseException e, HttpServletRequest request){

        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("DataBase Exception.");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status.value()).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> Validation ( MethodArgumentNotValidException e, HttpServletRequest request){

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // UNPROCESSABLE_ENTITY é um retorno de validação da entidade
        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("MethodArgumentNotValidException Exception.");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()){
            err.addError(f.getField(),f.getDefaultMessage());// getDefaultMessage pega a mensagem do erro
        }

        return ResponseEntity.status(status.value()).body(err);
    }


}
