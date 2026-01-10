package kg.arbocdi.fts.api.transfers;

import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;

public interface CreateTransferPort {
    void create(CreateTransferCommand cmd);
}
