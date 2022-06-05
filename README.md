# spread-i18n-core 
**spread-i18n-core** is a library. The library provides a simple interface for copying translations saved in an Excel sheet to string files used by iOS or Android projects to support internationalization.

It is written in `kotlin` language and can be run in `jvm` environment.

#### API
```
try {
    //A path can be absolute or relative
    Project.onPath("/path/of/project").importFrom("/path/of/excel/file")
} catch (exc: TransferException) {
    //...
}
```

### Features

* can be used in jvm supporting OSes such as: `Linux`, `macOS`, `Windows` and others,
* simple API,
* built on top of [Apache POI - the Java API for Microsoft Documents](https://poi.apache.org/)
* customisable string transformation.

### Requirements
To build:
- JDK

To run
- Excel sheet
- JRE

### Spread sheet format requirements

It is expected to provide an Excel sheet in the expected format. The list below summarizes all format requirements:

- Translations are located in the first sheet (tab)
- The sheet contains a "config row"
- The "config row" contains at least one "key cell" and one "locale cell"
```console
┌─────────────────────────────────────┐
│Key              │English │Polish    │ <<-- Config Row
├─────────────────────────────────────┤
│btn_cancel_text  │Cancel  │Anuluj    │
├─────────────────────────────────────┤
│btn_apply_text   │Apply   │Zastosuj  │
└─────────────────────────────────────┘
```
- The key cell or cells are identified by one of texts: "Key", "Identifier", "Id", "Android", "iOS". Capitalization does not matter.
- Locale cell content is equal to one of the property value of java `Locale` class.

For instance if the locale cell contains polish translations the cell text should be equal to one of string: `pl`, `Poland`, `Polish`, `pl-PL`. Capitalization does not matter.

```kotlin
Locale.country = 'PL'
Locale.displayCountry = 'Poland'
Locale.language = 'pl'
Locale.displayLanguage = 'Polish'
Locale.toLanguageTag() = 'pl-PL'
```

The library supports different keys for Android and iOS kept in the same sheet. To differentiate between Android and iOS keys, there must be two key cells containing "Android" and "iOS" text.

```console
┌───────────────────────────────────────────────────────┐
│iOS             │Android           │English │Polish    │
├───────────────────────────────────────────────────────┤
│btn_cancel_text │cancel_button_text│Cancel  │Anuluj    │
├───────────────────────────────────────────────────────┤
│btn_apply_text  │apply_button_text │Apply   │Zastosuj  │
└───────────────────────────────────────────────────────┘
```

### Project requrements

Destination translation files should exist. The library does not create files. It overwrites content.

### Integration

The library can be integrated in two forms: as the jar file or the local source code dependency.

#### 1. Building and using the library as the jar file.

- `cd` to project directory
```console
cd spread-i18n-core
```
- Run gradle build script
```console
./gradlew build
```
If everything went fine, the newly built library is located in the `./build/libs/` directory.

Once the jar file is generated add the file to the `classpath` of the destination project. You are ready to use.

#### 2. Using the source code directly as the destination project dependency

The following instruction assumes that the destination project is based on `gradle` build tool. Setup examples are given in `Kotlin` language.

- Add the library project path to `settings.gradle.kts` file of the destiantion project.
    ```kotlin
    includeBuild("../path/to/spread-i18n-core")
    ```
- Add the library dependency to `build.gradle.kts` in following format: 
`group:name:version`, for instance: 

    ```
    dependencies {
        ...
        implementation("com.andro.spreadi18ncore:spread-i18n-core:1.0-SNAPSHOT")
        ...
    }
    ```

here:
- `group` = "com.andro.spreadi18ncore"
- `name` = "spread-i18n-core"
- `version` = "1.0-SNAPSHOT"

> `Group` and `name` are constant. `Version` can vary. To know the current version check the library gradle build scipt:

```kotlin
...
group = "com.andro.spreadi18ncore"
version = "1.0-SNAPSHOT"
...
```

> In the future the library will be published to Maven repository

### Integrations

- [Cli](https://github.com/rojarand/spread-i18n-cli)
- [Gradle plugin](https://github.com/rojarand/spread-i18n-gradle-plugin)

### License
MIT © [Robert Andrzejczyk](https://github.com/rojarand)

### Contribute
Contributions are always welcome!

