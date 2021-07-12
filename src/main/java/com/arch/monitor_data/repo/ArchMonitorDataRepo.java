package com.arch.monitor_data.repo;

import com.arch.monitor_data.entity.ArchMonitorDataPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchMonitorDataRepo extends JpaRepository<ArchMonitorDataPo, Integer>, JpaSpecificationExecutor<ArchMonitorDataPo> {
}
