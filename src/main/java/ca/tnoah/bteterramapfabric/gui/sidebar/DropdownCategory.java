package ca.tnoah.bteterramapfabric.gui.sidebar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

public class DropdownCategory<T> extends LinkedHashMap<String, T> {
    @Getter @Setter
    private transient boolean opened = false;

    public DropdownCategory() { }
}
