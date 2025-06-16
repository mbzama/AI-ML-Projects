package zama.learning.procureai.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zama.learning.procureai.api.model.RfxLineItem;

import java.util.List;

@Repository
public interface RfxLineItemRepository extends JpaRepository<RfxLineItem, Long> {
    List<RfxLineItem> findByRfxEventId(Long rfxEventId);
}
