package com.invoicesync.datatransfer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DataTransferHeaderMappingRepository extends JpaRepository<DataTransferHeader, Long> {

}
