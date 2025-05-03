package apiazureeventgridconsumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

import apiazureeventgridconsumer.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
     * using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    /*
     * @FunctionName("HttpExample")
     * public HttpResponseMessage run(
     * 
     * @HttpTrigger(
     * name = "req",
     * methods = {HttpMethod.GET, HttpMethod.POST},
     * authLevel = AuthorizationLevel.ANONYMOUS)
     * HttpRequestMessage<Optional<String>> request,
     * final ExecutionContext context) {
     * context.getLogger().info("Java HTTP trigger processed a request.");
     * 
     * // Parse query parameter
     * final String query = request.getQueryParameters().get("name");
     * final String name = request.getBody().orElse(query);
     * 
     * if (name == null) {
     * return request.createResponseBuilder(HttpStatus.BAD_REQUEST).
     * body("Please pass a name on the query string or in the request body").build()
     * ;
     * } else {
     * return request.createResponseBuilder(HttpStatus.OK).body("Hello, " +
     * name).build();
     * }
     * }
     */
    @FunctionName("ProcessEventGridEvent")
    public void run(
            @EventGridTrigger(name = "eventGridEvent") String content,
            final ExecutionContext context) {
        Logger logger = context.getLogger();
        logger.info("Función con Event Grid trigger ejecutada.");

        Gson gson = new Gson();
        JsonObject eventGridEvent = gson.fromJson(content, JsonObject.class);

        logger.info("Evento recibido: " + eventGridEvent.toString());

        String eventType = eventGridEvent.get("eventType").getAsString();
        String dataStr = eventGridEvent.get("data").toString(); // para imprimir
        JsonObject data = eventGridEvent.getAsJsonObject("data"); // para uso real

        logger.info("Tipo de evento: " + eventType);
        logger.info("Data del evento: " + dataStr);

        if ("UserCreated".equals(eventType)) {
            try {
                // Obtener campos con manejo seguro
                String name = data.has("name") ? data.get("name").getAsString() : null;
                String email = data.has("email") ? data.get("email").getAsString() : null;
                String password = data.has("password") ? data.get("password").getAsString() : null;

                logger.info("Procesando inserción: name=" + name + ", email=" + email + ", password=" + password);

                if (name != null && email != null) {
                    try (
                            Connection conn = DatabaseConnection.getConnection();
                            PreparedStatement stmt = conn.prepareStatement(
                                    "INSERT INTO USUARIOS (name, email, password) VALUES (?, ?, ?)")) {
                        stmt.setString(1, name);
                        stmt.setString(2, email);
                        stmt.setString(3, password); // puede ser null
                        stmt.executeUpdate();
                        logger.info("Usuario insertado en Oracle correctamente.");
                    }
                } else {
                    logger.warning("No se insertó usuario porque falta nombre o email.");
                }
            } catch (Exception e) {
                logger.severe("Error al procesar UserCreated: " + e.getMessage());
            }
        }
    }
}
