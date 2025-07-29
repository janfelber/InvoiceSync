package com.invoicesync.convert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConvertRepository extends JpaRepository<Convert, Long>, JpaSpecificationExecutor<Convert> {

}
