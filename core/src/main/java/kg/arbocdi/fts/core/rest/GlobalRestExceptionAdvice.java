package kg.arbocdi.fts.core.rest;

import kg.arbocdi.fts.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@Slf4j
@RestControllerAdvice(basePackages = "kg.arbocdi.fts")
public class GlobalRestExceptionAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestErrorResponse> handleAll(
            Throwable ex,
            ServletWebRequest request
    ) {
        log.error(
                "REST error: {} {}",
                request.getRequest().getMethod(),
                request.getRequest().getRequestURI(),
                ex
        );
        RestErrorResponse body;
        if (ex instanceof BusinessException) body = new RestErrorResponse((BusinessException) ex);
        else body = new RestErrorResponse(ex);
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE) // 406
                .body(body);
    }

}
