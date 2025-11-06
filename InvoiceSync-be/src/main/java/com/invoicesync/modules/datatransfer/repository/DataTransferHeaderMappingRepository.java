package com.invoicesync.modules.datatransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.datatransfer.model.DataTransferHeader;

public interface DataTransferHeaderMappingRepository extends JpaRepository<DataTransferHeader, Long> {

}
