package kg.arbocdi.fts.core.outbox;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
//@ConditionalOnBean({Tracer.class, Propagator.class})
public class TraceContextSnapshotter {

    private final Tracer tracer;
    private final Propagator propagator;

    public Map<String, String> capture() {
        Span span = tracer.currentSpan();
        Map<String, String> carrier = new HashMap<>();
        if (span == null) {
            return carrier;
        }

        propagator.inject(
                span.context(),
                carrier,
                Map::put // setter
        );
        return carrier;
    }
}

