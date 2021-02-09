package org.hisp.dhis.tasks.tracker.importer;

import org.hisp.dhis.actions.AuthenticatedApiActions;
import org.hisp.dhis.cache.UserCredentials;
import org.hisp.dhis.response.dto.ApiResponse;
import org.hisp.dhis.tasks.DhisAbstractTask;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class QueryTrackerTeisTask extends DhisAbstractTask
{
    private String endpoint = "/api/tracker/trackedEntities";

    private String query = "?ou=DiszpKrYNg8&attribute=TfdH5KvFmMy&filter=TfdH5KvFmMy:GE:Karoline";

    private ApiResponse response;

    public QueryTrackerTeisTask( int weight )
    {
        this.weight = weight;
    }

    public QueryTrackerTeisTask( int weight, String query, UserCredentials userCredentials ) {
        this.weight = weight;
        this.query = query;
        this.userCredentials = userCredentials;
    }

    public int getWeight()
    {
        return this.weight;
    }

    @Override
    public String getName()
    {
        return endpoint;
    }

    @Override
    public String getType()
    {
        return "GET";
    }

    @Override
    public void execute()
    {
        this.response = new AuthenticatedApiActions( this.endpoint, getUserCredentials() ).get( this.query );

        if ( response.statusCode() == 200 )
        {
            recordSuccess( response.getRaw() );
            return;
        }

        recordFailure( response.getRaw() );
    }

    public ApiResponse executeAndGetResponse() {
        this.execute();
        return this.response;
    }
}