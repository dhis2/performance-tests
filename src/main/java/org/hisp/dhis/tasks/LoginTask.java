package org.hisp.dhis.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hisp.dhis.RestAssured;

import static io.restassured.RestAssured.preemptive;

/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com>
 */
public class LoginTask
    extends
    DhisAbstractTask
{
    public int getWeight()
    {
        return 1;
    }

    public String getName()
    {
        return "Authenticate";
    }

    @Override
    public String getType()
    {
        return "http";
    }

    public void execute()
    {
        RestAssured.getRestAssured().authentication = preemptive().basic( "admin", "district" );

        Response apiResponse = RestAssured.getRestAssured().given().contentType( ContentType.TEXT ).when()
            .get( "api/me" ).thenReturn();

        if ( apiResponse.statusCode() == 200 )
        {
            recordSuccess( apiResponse );
            return;
        }

        recordFailure( apiResponse );
    }
}
