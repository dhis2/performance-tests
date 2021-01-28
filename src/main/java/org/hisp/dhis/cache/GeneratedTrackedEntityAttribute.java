package org.hisp.dhis.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GeneratedTrackedEntityAttribute
{
    private String aggregationType = "NONE";

    private boolean generated = true;

    private String name = "test generated value";

    private String pattern = "SEQUENTIAL(####)";

    private String shortName = "test_generated_value3";

    private boolean unique = true;

    private String valueType = "TEXT";

}
