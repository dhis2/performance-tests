package org.hisp.dhis.tasks.aggregate;

import org.hisp.dhis.RestAssured;
import org.hisp.dhis.actions.AuthenticatedApiActions;
import org.hisp.dhis.actions.RestApiActions;
import org.hisp.dhis.cache.AggregateDataValue;
import org.hisp.dhis.cache.EntitiesCache;
import org.hisp.dhis.cache.User;
import org.hisp.dhis.dxf2.datavalue.DataValue;
import org.hisp.dhis.random.DataValueRandomizer;
import org.hisp.dhis.random.UserRandomizer;
import org.hisp.dhis.request.QueryParamsBuilder;
import org.hisp.dhis.response.dto.ApiResponse;
import org.hisp.dhis.tasks.DhisAbstractTask;

import static io.restassured.RestAssured.preemptive;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class AddDataValueTask
    extends DhisAbstractTask
{
    private String endpoint = "/api/dataValues";

    public AddDataValueTask(final int weight, final EntitiesCache entitiesCache ) {
        this.weight = weight;
        this.entitiesCache = entitiesCache;
    }

    @Override
    public String getName()
    {
        return endpoint;
    }

    @Override
    public String getType()
    {
        return "POST";
    }

    @Override
    public void execute()
    {
        User user = getUser();
        AuthenticatedApiActions dataValueActions  = new AuthenticatedApiActions( endpoint, user.getUserCredentials() );

        DataValue aggregateDataValue = new DataValueRandomizer().create( new UserRandomizer().getRandomUserOrgUnit( user ), entitiesCache );

        ApiResponse response = dataValueActions.post(aggregateDataValue, new QueryParamsBuilder()
            .add( "de", aggregateDataValue.getDataElement())
            .add( "pe",  aggregateDataValue.getPeriod())
            .add( "value=", aggregateDataValue.getValue())
            //.add( "ds=" + aggregateDataValue.get())
            .add( "ou=", aggregateDataValue.getOrgUnit() ));

        if (response.statusCode() == 201 ) {
            recordSuccess( response.getRaw() );
            return;
        }

        recordFailure( response.getRaw() );
    }
}
