import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import dynamicsettings.SettingsService;
import dynamicsettings.SettingsWorker;
import model.Settings;

public class SettingsController {

  public Result start() {

    AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
    SettingsService ss = new SettingsService(dynamoDB, "MySettings");

    SettingsWorker sw = new SettingsWorker(ss, Settings.class);
    sw.start();

    return ok();
  }

  public Result address() {

    return ok(Settings.Home.address.getValue());
  }

}
