package kg.arbocdi.fts.core.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class RestErrorHandler implements RestClient.ResponseSpec.ErrorHandler, Predicate<HttpStatusCode> {
    private final JsonMapper mapper;

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        byte[] body = response.getBody().readAllBytes();
        RuntimeException ex;
        try {
            ex = mapper.readValue(body, RestErrorResponse.class).convertToException();
        } catch (Exception e) {
            ex = new ParseException(new String(body), response.getStatusCode());
        }
        throw ex;
    }

    @Override
    public boolean test(HttpStatusCode httpStatusCode) {
        return httpStatusCode.value() == HttpStatus.NOT_ACCEPTABLE.value();
    }
}