package kg.arbocdi.fts.accounts;

import kg.arbocdi.fts.core.exception.BusinessException;

public class NotEnoughBalanceException extends BusinessException {
    public NotEnoughBalanceException() {
        super("Not enough balance", "balance.not.enough");
    }
}
