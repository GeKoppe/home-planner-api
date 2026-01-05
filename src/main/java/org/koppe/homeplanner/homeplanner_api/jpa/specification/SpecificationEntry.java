package org.koppe.homeplanner.homeplanner_api.jpa.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpecificationEntry<T extends Object> {
    /**
     * Value of the sepcification entry
     */
    private T value;

    public static enum Comparator {
        
    }
}
