package apiazureeventgridconsumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    /*
     @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }*/
    @FunctionName("ProcessEventGridEvent")
    public void run(
        @EventGridTrigger(name = "eventGridEvent") String content,
        final ExecutionContext context
    ) {
        Logger logger = context.getLogger();
        logger.info("Función con Event Grid trigger ejecutada.");

        //Deserializar el contenido
        Gson gson = new Gson();
        JsonObject eventGridEvent = gson.fromJson(content, JsonObject.class);

        logger.info("Evento recibido: " + eventGridEvent.toString());

        String eventType = eventGridEvent.get("eventType").getAsString();
        String data = eventGridEvent.get("data").toString();

        logger.info("Tipo de evento: " + eventType);
        logger.info("Data del evento: " + data);
    }
}
