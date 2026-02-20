package com.invoicesync.modules.receipt.model;

import java.util.List;

public record MergeItemsRequest(

    List<Long> itemIds,

    String description

) {

}
