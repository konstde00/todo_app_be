package com.konstde00.todo_app.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FeatureFlag {
  ANALYTICS(0, "analytics");

  int id;
  String name;

  @Override
  public String toString() {
    return String.valueOf(id);
  }

  public static FeatureFlag fromId(int id) {
    for (FeatureFlag flag : FeatureFlag.values()) {
      if (flag.id == id) {
        return flag;
      }
    }
    throw new IllegalArgumentException("No FeatureFlag with id " + id + " found");
  }
}
