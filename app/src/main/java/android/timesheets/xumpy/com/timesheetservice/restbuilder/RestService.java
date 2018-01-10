package android.timesheets.xumpy.com.timesheetservice.restbuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;

public class RestService extends NanoHTTPD {
    private List<RestObject> restObjects;

    public RestService() throws IOException{
        super(8888);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        restObjects = new ArrayList<>();
    }

    public void register(RestObject restObject){
        restObjects.add(restObject);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String text = "";
        for(RestObject restObject: restObjects){
            if (session.getUri().equals(restObject.getUrl())){
                System.out.println(restObject.getUrl());
                Map<String, List<String>> decodedQueryParameters = decodeParameters(session.getQueryParameterString());
                Object object = restObject.createObject(decodedQueryParameters);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                text = gson.toJson(object);
            }
        }
        Response response = newFixedLengthResponse(text);
        response.setMimeType("application/json");

        return response;
    }
}
