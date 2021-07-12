package com.arch.monitor_data.repo;

import com.arch.monitor_data.entity.ArchMonitorRecordPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchMonitorRecordRepo extends JpaRepository<ArchMonitorRecordPo, Integer>, JpaSpecificationExecutor<ArchMonitorRecordPo> {
}
