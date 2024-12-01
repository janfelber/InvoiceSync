package com.invoicesync.xml.utils;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Getter
public enum XmlTags {
    CENA_CENNIK("cena_cenik"),
    CASTKA_DPH("castka_dph"),
    CENA_CELKEM("cena_celkem"),
    VAR_SYMBOL("var_symbol"),
    EXT_NUMBER("ext_cislo"),
    DATE_OF_ISSUE("dat_vyst"),
    DATE_OF_TAX("dat_zd_pln"),
    DATE_OF_DUE("dat_splat"),
    PAYMENT_TYPE("forma_uhrady"),
    TITLE("nazev"),
    STREET("ulice"),
    ZIP("psc"),
    CITY("obec");

    private final String tagName;

    XmlTags(String tagName) {
        this.tagName = tagName;
    }

    public NodeList getNodes(Document document) {
        return document.getElementsByTagName(tagName);
    }
}
