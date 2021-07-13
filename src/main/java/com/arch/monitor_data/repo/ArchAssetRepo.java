package com.arch.monitor_data.repo;

import com.arch.monitor_data.entity.ArchAssetPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArchAssetRepo extends JpaRepository<ArchAssetPo, String>, JpaSpecificationExecutor<ArchAssetPo> {

}