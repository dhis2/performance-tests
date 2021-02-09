package org.hisp.dhis.tasksets.tracker;

import org.hisp.dhis.actions.AuthenticatedApiActions;
import org.hisp.dhis.cache.*;
import org.hisp.dhis.dxf2.events.trackedentity.Attribute;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstances;
import org.hisp.dhis.random.RandomizerContext;
import org.hisp.dhis.random.TrackedEntityInstanceRandomizer;
import org.hisp.dhis.random.UserRandomizer;
import org.hisp.dhis.request.QueryParamsBuilder;
import org.hisp.dhis.response.dto.ApiResponse;
import org.hisp.dhis.tasks.DhisAbstractTask;
import org.hisp.dhis.tasks.tracker.GenerateAndReserveTrackedEntityAttributeValuesTask;
import org.hisp.dhis.tasks.tracker.GenerateTrackedEntityAttributeValueTask;
import org.hisp.dhis.tasks.tracker.tei.AddTeiTask;
import org.hisp.dhis.utils.DataRandomizer;

import java.util.List;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class Android_syncTeisTaskSet extends DhisAbstractTask
{
    public Android_syncTeisTaskSet( int weight, EntitiesCache cache ) {
        this.weight = weight;
        this.entitiesCache = cache;
    }

    @Override
    public String getName()
    {
        return "Android: sync teis";
    }

    @Override
    public String getType()
    {
        return "http";
    }

    @Override
    public void execute()
        throws Exception
    {
        User user = new UserRandomizer().getRandomUser( entitiesCache );
        String ou = new UserRandomizer().getRandomUserOrgUnit( user );
        Program program = DataRandomizer.randomElementFromList( entitiesCache.getTrackerPrograms() );

        RandomizerContext context = new RandomizerContext();
        context.setProgram( program );
        context.setOrgUnitUid( ou );

        TrackedEntityInstances teis = new TrackedEntityInstanceRandomizer().create( this.entitiesCache, context, 3, 10 );

        generateAttributes( program, teis.getTrackedEntityInstances(), user.getUserCredentials() );

        AuthenticatedApiActions authenticatedApiActions = new AuthenticatedApiActions( "/api/trackedEntityInstances", user.getUserCredentials() );

        ApiResponse response = authenticatedApiActions.post( teis, new QueryParamsBuilder().add( "strategy=SYNC" ) );

        if (response.statusCode() == 200)  {
           recordSuccess( response.getRaw() );
        }

        else
        {
            recordFailure( response.getRaw());
        }
    }


    private void generateAttributes(Program program, List<TrackedEntityInstance> teis, UserCredentials userCredentials ) {
        program.getAttributes().stream().filter( p ->
            p.isGenerated()
        ).forEach( att -> {
            ApiResponse response = new GenerateAndReserveTrackedEntityAttributeValuesTask(1, att.getTrackedEntityAttributeUid(), userCredentials, teis.size()).executeAndGetResponse();
            List<String> values = response.extractList( "value" );

            for ( int i = 0; i < teis.size(); i++ )
            {
                Attribute attribute = teis.get( i ).getAttributes().stream().filter( teiAtr -> teiAtr.getAttribute().equals( att.getTrackedEntityAttributeUid()))
                    .findFirst().orElse( null);

                attribute.setValue( values.get( i ) );
            }
        } );
    }
}