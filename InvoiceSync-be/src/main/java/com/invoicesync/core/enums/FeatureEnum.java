package com.invoicesync.core.enums;

import lombok.Getter;


@Getter
public enum FeatureEnum {
  EKON_SPECIALTY(1, "Turns on various specifics for Ekon Consult.");

  private final long id;
  private final String description;

  FeatureEnum(final int id, final String description) {
    this.id = id;
    this.description = description;
  }

  public static FeatureEnum fromId(final long id) {
    for (final FeatureEnum f : values()) {
      if (f.getId() == id) return f;
    }
    throw new IllegalArgumentException("Unknown Feature id: " + id);
  }

}
