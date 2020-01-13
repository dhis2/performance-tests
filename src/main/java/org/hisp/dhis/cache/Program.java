package org.hisp.dhis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Program
{
    private String uid;

    private List<String> orgUnits;

    private List<ProgramStage> stages;

    private List<ProgramAttribute> attributes;

    private String entityType;

    public Program()
    {
    }

    public String getOrgUnit( int index )
    {
        return this.orgUnits.get( index );
    }
}
