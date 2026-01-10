package kg.arbocdi.fts.transfers.db;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransferSagaRepository extends JpaRepository<TransferSaga, UUID> {
    @Query("select saga from TransferSaga saga where saga.id = :id and saga.inTerminalState = false")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TransferSaga> findActiveForUpdate(@Param("id") UUID id);
}
