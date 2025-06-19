package com.invoicesync.chartaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartAccountRepository extends JpaRepository<ChartAccount, Long> {

}
