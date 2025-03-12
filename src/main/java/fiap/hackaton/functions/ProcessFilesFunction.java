package fiap.hackaton.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.FixedDelayRetry;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.mysql.annotation.MySqlOutput;
import com.microsoft.azure.functions.rabbitmq.annotation.RabbitMQOutput;
import com.microsoft.azure.functions.rabbitmq.annotation.RabbitMQTrigger;
import fiap.hackaton.functions.domain.enums.FileStatus;
import fiap.hackaton.functions.domain.models.NotificationEvent;
import fiap.hackaton.functions.domain.models.Session;

public class ProcessFilesFunction {
    @FunctionName("ProcessFilesFunction")
    @FixedDelayRetry(maxRetryCount = 4, delayInterval = "00:01:00")
    public void run(
            @RabbitMQTrigger(connectionStringSetting = "RabbitMqConnectionString", queueName = "%RabbitMqOutQueueName%") String message,
            @MySqlOutput(name = "sessions", commandText = "Sessions", connectionStringSetting = "MySqlConnectionString") OutputBinding<Session> mySqlOutputBinding,
            @RabbitMQOutput(connectionStringSetting = "RabbitMqConnectionString", queueName = "%NotifyQueue%") OutputBinding<NotificationEvent> rabbitMqOutputBinding,
            final ExecutionContext context) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Session session = objectMapper.readValue(message, Session.class);

        if(session != null) {
            context.getLogger().info("[Processor]: Initialize Process Files Function from event: " + session.getSessionId());

            // TODO Get Files from Bucket and process zip.
            // TODO generate URL bucket

            session.setStatus(FileStatus.COMPLETED);
            mySqlOutputBinding.setValue(session);
            context.getLogger().info("[Processor]: Initialize Process Files Function from event: " + session.getSessionId());
            rabbitMqOutputBinding.setValue(new NotificationEvent(session.getEmail(), "Mock Message"));
        } else {
            context.getLogger().warning("[Processor]: Finish Process Files Function. No session found.");
        }
    }
}