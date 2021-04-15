package org.hisp.dhis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProgramStage
{
    private String uid;

    private List<DataElement> dataElements;

    private boolean repeatable;

    public ProgramStage()
    {
    }

    public ProgramStage( String uid )
    {
        this.uid = uid;
    }
}
