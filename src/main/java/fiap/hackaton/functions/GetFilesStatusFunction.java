package fiap.hackaton.functions;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.mysql.annotation.CommandType;
import com.microsoft.azure.functions.mysql.annotation.MySqlInput;
import fiap.hackaton.functions.domain.models.Session;

import java.util.Optional;

public class GetFilesStatusFunction {
    @FunctionName("GetFilesStatusFunction")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", route = "v1/files/{sessionId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @MySqlInput(
                    name = "sessions",
                    commandText = "SELECT * FROM Sessions WHERE SessionId = @sessionId",
                    commandType = CommandType.Text,
                    parameters = "@sessionId={sessionId}",
                    connectionStringSetting = "MySqlConnectionString")
            Session[] sessions) {

        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(sessions).build();
    }
}

