package com.arch.monitor_data.service;

import com.arch.monitor_data.entity.ArchAssetPo;
import com.arch.monitor_data.repo.ArchAssetRepo;
import com.arch.monitor_data.repo.ArchMonitorDataRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
@Slf4j
public class ArchAssetService {

    @Resource
    private ArchAssetRepo archAssetRepo ;

    @Resource
    private ArchMonitorDataRepo archMonitorDataRepo ;

    public List<ArchAssetPo> findArchAssetList(String bridgeName) {
        List<ArchAssetPo> archAssetPoList = archAssetRepo.findAll((Specification<ArchAssetPo>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction() ;
            if (!StringUtils.isEmpty(bridgeName)) {
                predicate.getExpressions().add(criteriaBuilder.equal(root.get("bridgeName"), bridgeName)) ;
            }
            return predicate ;
        }) ;
        return archAssetPoList;
    }




}
