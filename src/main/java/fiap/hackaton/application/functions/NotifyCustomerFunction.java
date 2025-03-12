package fiap.hackaton.application.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.FixedDelayRetry;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.SendGridOutput;
import com.microsoft.azure.functions.rabbitmq.annotation.RabbitMQTrigger;
import fiap.hackaton.domain.events.NotificationEvent;


public class NotifyCustomerFunction {
    @FunctionName("NotifyCustomerFunction")
    @FixedDelayRetry(maxRetryCount = 4, delayInterval = "00:00:10")
    public void run(
            @RabbitMQTrigger(
                    connectionStringSetting = "RabbitMqConnectionString",
                    queueName = "%NotifyQueue%"
            ) String message,
            @SendGridOutput(
                    name = "email",
                    dataType = "String",
                    apiKey = "SendGridApiKey",
                    to = "<to mail address>",
                    from = "<from mail address>",
                    subject = "<subject>",
                    text = "<message>")
            OutputBinding<String> sendGridOutputBinding,
            final ExecutionContext context) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);

        final String fromEmail = "jc_mds@outlook.com";
        String toAddress = event.getEmail();
        String value = event.getMessage();

        context.getLogger().info("[Processor]: Initialize Noitify consumer from to-address: " + toAddress);

        if (!toAddress.isEmpty() && !value.isEmpty()) {
            final String template = "{\"personalizations\":" +
                    "[{\"to\":[{\"email\":\"%s\"}]," +
                    "\"subject\":\"Sending with SendGrid\"}]," +
                    "\"from\":{\"email\":\"%s\"}," +
                    "\"content\":[{\"type\":\"text/plain\",\"value\": \"%s\"}]}";


            final String body = String.format(template, toAddress, fromEmail, value);
            sendGridOutputBinding.setValue(body);
            context.getLogger().info("[Processor]: Finish Noitify consumer from to-address: " + toAddress);
        } else {
            context.getLogger().warning("[Processor]: Finish Noitify. No content message or address received.");
        }
    }
}