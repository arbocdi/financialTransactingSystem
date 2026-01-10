package kg.arbocdi.fts.core.exception;

public class AggregateAlreadyExistsException extends BusinessException {
    public AggregateAlreadyExistsException() {
        super("Aggregate already exists.", "aggregate.already.exists");
    }
}
