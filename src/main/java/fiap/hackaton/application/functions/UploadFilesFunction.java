package fiap.hackaton.application.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.mysql.annotation.MySqlOutput;
import com.microsoft.azure.functions.rabbitmq.annotation.RabbitMQOutput;
import fiap.hackaton.domain.entities.Session;
import fiap.hackaton.domain.enums.FileStatus;

import java.util.Optional;
import java.util.UUID;

public class UploadFilesFunction {

    @FunctionName("UploadFilesFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", route = "v1/files", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<byte[]>> request,
            @RabbitMQOutput(connectionStringSetting = "RabbitMqConnectionString", queueName = "%RabbitMqOutQueueName%") OutputBinding<Session> rabbitMqOutputBinding,
            @MySqlOutput(name = "sessions", commandText = "Sessions", connectionStringSetting = "MySqlConnectionString") OutputBinding<Session> mySqlOutputBinding,
            final ExecutionContext context) {

        context.getLogger().info("[Uploader]: Initialize Uploader Function");

        UUID sessionId = UUID.randomUUID();
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setEmail("jean.carlos@5by5.com.br"); // mock remover e buscar do jwt
        session.setStatus(FileStatus.PROCESSING);

        context.getLogger().info("[Uploader]: Push files into bucket");
        //TODO Read byte arrays (multipartStream) and upload files into bucket
        //uploadMultipartFiles(request, sessionId);

        context.getLogger().info("[Uploader]: Save status process into outbox table");
        mySqlOutputBinding.setValue(session);

        context.getLogger().info("[Uploader]: Publish event");
        rabbitMqOutputBinding.setValue(session);

        context.getLogger().info("[Uploader]: finish uploader function");
        return request.createResponseBuilder(HttpStatus.ACCEPTED).body(session).build();
    }
}
