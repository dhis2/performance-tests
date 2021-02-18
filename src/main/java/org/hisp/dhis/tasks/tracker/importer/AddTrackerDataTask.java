package org.hisp.dhis.tasks.tracker.importer;

import org.hisp.dhis.actions.AuthenticatedApiActions;
import org.hisp.dhis.actions.RestApiActions;
import org.hisp.dhis.cache.EntitiesCache;
import org.hisp.dhis.cache.Program;
import org.hisp.dhis.cache.User;
import org.hisp.dhis.cache.UserCredentials;
import org.hisp.dhis.dxf2.events.trackedentity.Attribute;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstances;
import org.hisp.dhis.models.TrackedEntities;
import org.hisp.dhis.random.RandomizerContext;
import org.hisp.dhis.random.TrackedEntityInstanceRandomizer;
import org.hisp.dhis.request.QueryParamsBuilder;
import org.hisp.dhis.response.dto.ApiResponse;
import org.hisp.dhis.tasks.DhisAbstractTask;
import org.hisp.dhis.tasks.tracker.GenerateAndReserveTrackedEntityAttributeValuesTask;
import org.hisp.dhis.tracker.domain.mapper.TrackedEntityMapperImpl;
import org.hisp.dhis.utils.DataRandomizer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class AddTrackerDataTask extends DhisAbstractTask
{
    private String endpoint = "/api/tracker";
    public AddTrackerDataTask(int weight, EntitiesCache entitiesCache ) {
        this.weight = weight;
        this.entitiesCache = entitiesCache;
    }
    @Override
    public String getName()
    {
        return endpoint ;
    }

    @Override
    public String getType()
    {
        return "POST";
    }

    @Override
    public void execute()
        throws Exception
    {
        user = getUser();
        RandomizerContext context = new RandomizerContext();
        context.setOrgUnitUid( DataRandomizer.randomElementFromList( user.getOrganisationUnits() ) );

        AuthenticatedApiActions trackerActions = new AuthenticatedApiActions( endpoint, user.getUserCredentials() );

        TrackedEntityInstances instances = new TrackedEntityInstanceRandomizer().create( entitiesCache, context, 2, 3  );

        generateAttributes( context.getProgram(), instances.getTrackedEntityInstances(), user.getUserCredentials() );

        TrackedEntities trackedEntities = TrackedEntities.builder()
            .trackedEntities( instances.getTrackedEntityInstances().stream().
                map( p-> {
                    return new TrackedEntityMapperImpl().from( p );
                } ).collect( Collectors.toList()))
            .build();

        performTaskAndRecord( () -> trackerActions.post( trackedEntities, new QueryParamsBuilder().add( "async=false" ) ) );
    }


    private void generateAttributes( Program program, List<TrackedEntityInstance> teis, UserCredentials userCredentials ) {
        program.getAttributes().stream().filter( p ->
            p.isGenerated()
        ).forEach( att -> {
            ApiResponse response = new GenerateAndReserveTrackedEntityAttributeValuesTask(1, att.getTrackedEntityAttribute(), userCredentials, teis.size()).executeAndGetResponse();
            List<String> values = response.extractList( "value" );

            for ( int i = 0; i < teis.size(); i++ )
            {
                Attribute attribute = teis.get( i ).getAttributes().stream().filter( teiAtr -> teiAtr.getAttribute().equals( att.getTrackedEntityAttribute()))
                    .findFirst().orElse( null);

                attribute.setValue( values.get( i ) );
            }
        } );
    }
}
