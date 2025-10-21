package com.invoicesync.user.sidenav;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class SideNavUtils {

  private SideNavUtils() {
  }

  public static List<SidenavItemDto> buildMenuHierarchy(final Set<SideNav> items) {
    final List<SidenavItemDto> roots = new ArrayList<>();

    final Map<Long, SidenavItemDto> dtoMap = items.stream()
        .collect(Collectors.toMap(
            SideNav::getId,
            s -> new SidenavItemDto(s.getLabel(), s.getIcon(), s.getRoute(), new ArrayList<>())
        ));

    for (final SideNav item : items) {
      if (item.getParent() != null && dtoMap.containsKey(item.getParent().getId())) {
        dtoMap.get(item.getParent().getId()).getChildren().add(dtoMap.get(item.getId()));
      } else {
        roots.add(dtoMap.get(item.getId()));
      }
    }

    return roots;
  }
}
