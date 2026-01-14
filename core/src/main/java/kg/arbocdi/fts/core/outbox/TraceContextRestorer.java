package kg.arbocdi.fts.core.outbox;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
//@ConditionalOnBean({Tracer.class, Propagator.class})
public class TraceContextRestorer {

    private final Tracer tracer;
    private final Propagator propagator;

    public <T> T withRestoredTrace(Map<String, String> carrier, String spanName, Callable<T> body) {

        if (carrier == null) carrier = Map.of();

        // extract возвращает именно Span.Builder
        Span.Builder builder = propagator.extract(carrier, Map::get);

        // стартуем span прямо из builder
        Span span = builder.name(spanName).start();

        try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
            return body.call();
        } catch (Exception e) {
            span.error(e);
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException(e);
        } finally {
            span.end();
        }
    }

    public void withRestoredTrace(Map<String, String> carrier, String spanName, Runnable body) {
        withRestoredTrace(carrier, spanName, () -> {
            body.run();
            return null;
        });
    }
}
