package com.procure.repository;

import com.procure.model.RfxLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfxLineItemRepository extends JpaRepository<RfxLineItem, Long> {
    List<RfxLineItem> findByRfxEventId(Long rfxEventId);
}
