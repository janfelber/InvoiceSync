package com.invoicesync.datatransfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DataTransferRepository
    extends JpaRepository<DataTransfer, Long>, JpaSpecificationExecutor<DataTransfer> {

}
