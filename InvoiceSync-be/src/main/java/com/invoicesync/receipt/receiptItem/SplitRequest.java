package com.invoicesync.receipt.receiptItem;

import java.util.List;

import lombok.Data;

@Data
public class SplitRequest {
  private List<SplitPartDTO> parts;
}
