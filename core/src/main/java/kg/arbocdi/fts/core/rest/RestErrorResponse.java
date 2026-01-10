package kg.arbocdi.fts.core.rest;

import kg.arbocdi.fts.core.exception.BusinessException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestErrorResponse {
    private Type type;
    private String message;
    private String code;

    public RestErrorResponse(Throwable ex) {
        type = Type.OTHER;
        message = ex.getMessage();
        code = ex.getClass().getSimpleName();
    }

    public RestErrorResponse(BusinessException ex) {
        type = Type.BUSINESS;
        message = ex.getMessage();
        code = ex.getCode();
    }

    public enum Type {
        BUSINESS, OTHER;
    }

    public RuntimeException convertToException() {
        if (type == Type.BUSINESS) return new BusinessException(message, code);
        else return new RuntimeException(message);
    }

}
