package zama.learning.procureai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import zama.learning.procureai.api.model.AuctionBid;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
    List<AuctionBid> findByRfxEventId(Long rfxEventId);
    List<AuctionBid> findByVendorId(Long vendorId);
    List<AuctionBid> findByRfxEventIdAndVendorId(Long rfxEventId, Long vendorId);

    @Query("SELECT a FROM AuctionBid a WHERE a.rfxEvent.id = :eventId AND a.lineItem.id = :lineItemId AND a.isActive = true ORDER BY a.bidAmount ASC, a.bidTime DESC")
    List<AuctionBid> findActiveByEventAndLineItemOrderByAmountAndTime(@Param("eventId") Long eventId, @Param("lineItemId") Long lineItemId);

    @Query("SELECT a FROM AuctionBid a WHERE a.rfxEvent.id = :eventId AND a.lineItem.id = :lineItemId AND a.isActive = true ORDER BY a.bidAmount ASC LIMIT 1")
    Optional<AuctionBid> findLowestBidByEventAndLineItem(@Param("eventId") Long eventId, @Param("lineItemId") Long lineItemId);

    @Query("SELECT a FROM AuctionBid a WHERE a.rfxEvent.id = :eventId AND a.vendor.id = :vendorId AND a.lineItem.id = :lineItemId AND a.isActive = true ORDER BY a.bidTime DESC LIMIT 1")
    Optional<AuctionBid> findLatestBidByEventAndVendorAndLineItem(@Param("eventId") Long eventId, @Param("vendorId") Long vendorId, @Param("lineItemId") Long lineItemId);
}
