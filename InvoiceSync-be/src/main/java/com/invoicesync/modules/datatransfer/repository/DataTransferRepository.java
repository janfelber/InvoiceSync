package com.invoicesync.modules.datatransfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.modules.datatransfer.model.DataTransfer;

@Repository
public interface DataTransferRepository
    extends JpaRepository<DataTransfer, Long>, JpaSpecificationExecutor<DataTransfer> {

}
