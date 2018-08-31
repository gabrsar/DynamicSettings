# Dynamic Settings

[![Build Status](https://travis-ci.org/gabrsar/DynamicSettings.svg?branch=master)](https://travis-ci.org/gabrsar/DynamicSettings) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/e16b2830fb9d4ecc9e0ec450d36b0a93)](https://www.codacy.com/app/gabriel-saraiva/DynamicSettings?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=gabrsar/DynamicSettings&amp;utm_campaign=Badge_Grade) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [ ![Download](https://api.bintray.com/packages/gabrsar/dynamic-settings/dynamic-settings/images/download.svg) ](https://bintray.com/gabrsar/dynamic-settings/dynamic-settings/_latestVersion)

Don't make deploys to change simple settings.

Simply put your settings on a database (only supports DynamoDB for now) and get it updated every minute.

# How To use (Complete)
For examples on how to use, please check this [Simple example application](https://github.com/gabrsar/DynamicSettingsExample)
# How To Use (TL;DR)

1. Add this to your pom.xml:
```xml
<dependencies>
    <dependency>
      <groupId>br.com.gabrielsaraiva.dynamicsettings</groupId>
      <artifactId>dynamic-settings</artifactId>
      <version>0.2.0</version>
    </dependency>
</dependencies>

<repositories>
  <repository>
    <id>jcenter</id>
    <url>https://jcenter.bintray.com/</url>
  </repository>
</repositories>
```

2. In DyanmoDB create a new table called `MySettings`, with index set to `module`.
3. Add an item with module value set to `Home`

5. Create a class like that
```java
public class Settings {
    public static class Home {
        public static final Setting<String> address = Setting.define("address", "This is my house");
  }
}
```

6. Add this in your project startup:
```java
DynamoDBProvider dynamodbProvider = new DynamoDBProvider("MySettings");
DynamicSettings ds = new DynamicSettings(dynamodbProvider, 5, Settings.class);
ds.start();

SettingsReader settingsReader = new SettingsReader();

System.out.println(settingsReader.get(Settings.Home.address));
```
7. Run it. Change your settings values in DynamoDB. Get it updated in your program.




# Features
- [X] Support basic types
- [X] Support DynamoDB 
- [X] Support Dummy Provider for Testing
- [X] Full test coverage
- [X] Make it testable inside host projects
- [X] JCenter
- [ ] Maven Central
- [ ] Support collections and maps
- [ ] Extract dependencies into separated modules
- [ ] Support for Redis
- [ ] Support MySql
- [ ] Support PostgreSql
- [ ] Support for MongoDB

- Test Travis CI
Suggestions and PullRequests are wellcome.
