package org.hisp.dhis.tasks;

import io.restassured.response.Response;
import org.hisp.dhis.actions.RestApiActions;
import org.hisp.dhis.request.QueryParamsBuilder;
import org.hisp.dhis.response.dto.ApiResponse;

public class ReserveTrackedEntityAttributeValuesTask
    extends
    DhisAbstractTask
{
    private String endpoint = "/api/trackedEntityAttributes";

    public ReserveTrackedEntityAttributeValuesTask( int weight )
    {
        this.weight = weight;
    }

    public String getName()
    {
        return "/api/trackedEntityAttributes/{id}/generateAndReserve";
    }

    @Override
    public String getType()
    {
        return "POST";
    }

    public void execute()
    {
        RestApiActions trackedEntityAttributeActions = new RestApiActions( endpoint );

        String attributeID = "c5Mvtl3GuIb";

        ApiResponse response = trackedEntityAttributeActions.get( attributeID + "/generateAndReserve",
                new QueryParamsBuilder().add( "numberToReserve", "1" ) );

        record( response.getRaw() );
    }

    private void record( Response response )
    {
        if ( response.statusCode() == 200 )
        {
            recordSuccess( response );
        }
        else
        {
            recordFailure( response );
        }
    }
}
