package com.invoicesync.xml.utils;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Getter
public enum TemplateTags {
    UNIT_PRICE("typ:unitPrice"),
    PRICE("typ:price"),
    PRICE_VAT("typ:priceVAT"),
    PRICE_SUM("typ:priceSum"),
    NUMBER_REQUESTED("typ:numberRequested"),
    SYM_VAR("typ:symVar"),
    SYM_PAR("typ:symPar"),
    DATE("typ:date"),
    DATE_TAX("typ:dateTax"),
    DATE_DUE("typ:dateDue"),
    DATE_ACCOUNTING("typ:dateAccounting"),
    NAME("typ:name"),
    CITY("typ:city"),
    STREET("typ:street"),
    ZIP("typ:zip"),
    NUMBER_ORDER("typ:numberOrder");



    private final String tagName;

    TemplateTags(String tagName) {
        this.tagName = tagName;
    }

    public NodeList getNodes(Document document) {
        return document.getElementsByTagName(tagName);
    }
}
