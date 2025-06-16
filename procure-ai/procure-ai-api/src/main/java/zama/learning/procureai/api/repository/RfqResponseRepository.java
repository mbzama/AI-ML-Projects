package zama.learning.procureai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zama.learning.procureai.api.model.RfqResponse;
import zama.learning.procureai.api.model.enums.BidStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RfqResponseRepository extends JpaRepository<RfqResponse, Long> {
    List<RfqResponse> findByRfxEventId(Long rfxEventId);
    List<RfqResponse> findByVendorId(Long vendorId);
    List<RfqResponse> findByRfxEventIdAndVendorId(Long rfxEventId, Long vendorId);
    Optional<RfqResponse> findByRfxEventIdAndVendorIdAndLineItemId(Long rfxEventId, Long vendorId, Long lineItemId);

    @Query("SELECT r FROM RfqResponse r WHERE r.rfxEvent.id = :eventId AND r.lineItem.id = :lineItemId ORDER BY r.unitPrice ASC")
    List<RfqResponse> findByEventAndLineItemOrderByPrice(@Param("eventId") Long eventId, @Param("lineItemId") Long lineItemId);

    List<RfqResponse> findByStatus(BidStatus status);
}
