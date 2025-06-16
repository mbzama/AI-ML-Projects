package zama.learning.procureai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zama.learning.procureai.api.model.RfxEvent;
import zama.learning.procureai.api.model.enums.EventStatus;
import zama.learning.procureai.api.model.enums.EventType;

import java.util.List;

@Repository
public interface RfxEventRepository extends JpaRepository<RfxEvent, Long> {
    List<RfxEvent> findByEventType(EventType eventType);
    List<RfxEvent> findByStatus(EventStatus status);
    List<RfxEvent> findByCreatedById(Long createdById);
    List<RfxEvent> findByEventTypeAndStatus(EventType eventType, EventStatus status);

    @Query("SELECT r FROM RfxEvent r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    List<RfxEvent> findByStatusIn(@Param("statuses") List<EventStatus> statuses);

    @Query("SELECT r FROM RfxEvent r WHERE r.endDate > CURRENT_TIMESTAMP AND r.status = 'PUBLISHED' ORDER BY r.endDate ASC")
    List<RfxEvent> findActiveEvents();
}
