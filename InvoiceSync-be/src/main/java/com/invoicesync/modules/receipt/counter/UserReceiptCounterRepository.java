package com.invoicesync.modules.receipt.counter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserReceiptCounterRepository {

  private final JdbcTemplate jdbcTemplate;

  public UserReceiptCounterRepository(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Long getAndIncrementReceiptOrder(final String username) {
    return jdbcTemplate.queryForObject("""
        INSERT INTO invoice_sync.user_receipt_counter (username, next_order)
        VALUES (?, 2)
        ON CONFLICT (username) DO UPDATE
          SET next_order = user_receipt_counter.next_order + 1
        RETURNING next_order - 1
        """, Long.class, username);
  }

}
