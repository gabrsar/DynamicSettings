package dynamicsettings;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import play.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class SettingsService implements SettingsLoader {


  private static final Set<Setting<?>> settings = new HashSet<>();

  private final AmazonDynamoDB dynamoClient;
  private final String tableName;

  public SettingsService(AmazonDynamoDB dynamoClient, String tableName) {
    this.tableName = tableName;
    this.dynamoClient = dynamoClient;
  }

  public void registerSetting(Setting<?> setting) throws NoSuchConverterFoundException {
    if (!ConverterProvider.hasConverter(setting.getType())) {
      throw new NoSuchConverterFoundException(setting);
    }
    settings.add(setting);
  }

  public void refreshAll() {

    ScanResult all = dynamoClient.scan(new ScanRequest(tableName));
    List<Map<String, AttributeValue>> items = all.getItems();

    Map<String, List<Map<String, AttributeValue>>> modules = items.stream()
        .collect(Collectors.groupingBy(a -> a.get("module").getS()));

    Map<String, Map<String, AttributeValue>> modulesByModuleName = modules.entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    settings.forEach(s -> updateSettingValue(modulesByModuleName, s));
  }


  protected void updateSettingValue(Map<String, Map<String, AttributeValue>> modulesByName, Setting<?> setting) {

    Optional<Object> rawValueOpt = Optional
        .ofNullable(modulesByName.get(setting.getModuleName()))
        .map(module -> module.get(setting.getName()))
        .flatMap(va -> ConverterProvider.convert(va, setting));

    boolean updated = setting.updateValue(rawValueOpt);

    Logger.debug(
        "{}.{}={} ({}), updated={}, found={}",
        setting.getModuleName(),
        setting.getName(),
        setting.getValue(),
        setting.getFallBackValue(),
        updated,
        rawValueOpt.isPresent()
    );

  }


}
