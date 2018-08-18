package br.com.gabriel.saraiva.dynamicsettings.example;

import br.com.gabriel.saraiva.dynamicsettings.dynamicsettings.SettingsService;
import br.com.gabriel.saraiva.dynamicsettings.dynamicsettings.providers.dynamodb.DynamodbProvider;
import br.com.gabriel.saraiva.dynamicsettings.example.Settings.Home;

public class App {

    public static void main(String[] args) throws InterruptedException {

        DynamodbProvider dynamodbProvider = new DynamodbProvider("MySettings");
        SettingsService ss = new SettingsService(dynamodbProvider, Settings.class);
        ss.start();

        System.out.println("Go in dynamo and change with the value of this setting. Check what happen here :)");
        System.out.println("It will take a while to syncronize, so... be patient ;)");

        while (true) {
            System.out.println(Home.address.getValue());
            Thread.sleep(1000);
        }

    }
}
