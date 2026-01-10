package kg.arbocdi.fts.core.exception;

public class AggregateNotFoundException extends BusinessException {
    public AggregateNotFoundException() {
        super("Aggregate not found", "aggregate.not.found");
    }
}
