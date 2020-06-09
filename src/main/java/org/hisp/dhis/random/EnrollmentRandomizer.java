package org.hisp.dhis.random;

/*
 * Copyright (c) 2004-2020, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hisp.dhis.cache.EntitiesCache;
import org.hisp.dhis.cache.Program;
import org.hisp.dhis.cache.ProgramStage;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dxf2.events.enrollment.Enrollment;
import org.hisp.dhis.dxf2.events.enrollment.EnrollmentStatus;
import org.hisp.dhis.utils.DataRandomizer;

/**
 * @author Luciano Fiandesio
 */
public class EnrollmentRandomizer
    extends
        AbstractTrackerEntityRandomizer<Enrollment>
{
    private int maxEvent;
    private int minEVent;
    private EventRandomizer eventRandomizer = new EventRandomizer();

    public EnrollmentRandomizer( int minEVent, int maxEvent )
    {
        this.minEVent = minEVent;
        this.maxEvent = maxEvent;
    }

    public EnrollmentRandomizer()
    {
        this.minEVent = 1;
        this.maxEvent = 5;
    }

    @Override
    public Enrollment create( EntitiesCache cache, RandomizerContext ctx )
    {
        Program program = getProgramFromContextOrRnd( ctx, cache );
        String orgUnitUid = ctx.getOrgUnitUid();
        
        if ( orgUnitUid == null )
        {
            orgUnitUid = getRandomOrgUnitFromProgram( program );
        }

        // Pick a random program stage to pass to the events
        ProgramStage programStage = getProgramStageFromProgram( program );
        ctx.setProgramStage( programStage );
        
        Enrollment enrollment = new Enrollment();
        enrollment.setProgram( program.getUid() );
        enrollment.setOrgUnit( orgUnitUid );
        enrollment.setEnrollmentDate( new Date() );
        enrollment.setIncidentDate( new Date() );
        enrollment.setStatus( EnrollmentStatus.ACTIVE );
        enrollment.setFollowup( false );
        enrollment.setDeleted( false );

        // if program stage is NON-REPEATABLE generate just one event
        int eventsSize = programStage.isRepeatable() ? DataRandomizer.randomIntInRange( minEVent, maxEvent ) : 1;

        enrollment.setEvents( IntStream.rangeClosed( 1, eventsSize )
            .mapToObj( i -> eventRandomizer.create( cache, ctx ) )
            .collect( Collectors.toList() ) );

        return enrollment;
    }
}