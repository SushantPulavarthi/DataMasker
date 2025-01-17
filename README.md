# DataMasker

## Usage

```
java -jar <path-to-DataMasker.jar> <path-to-data.json> <path-to-rules.json>
```
where:
- data.json: a path to a json file of data
- rules:json: a path to a file containing a json array of strings in this format: "k:<regex>" OR "v:<regex>"
  - The first part refers to whether the regex should match the key "k" or the value "v".
    - If the key is used for matching (e.g. `k:^Foo`), replace **values** with strings of the same length but consisting of `*` only, where the **key** in the data file matches the regex pattern.
    - If the value is used for matching (e.g. `v:^bar$`), replace the parts of the **values** that match the regex pattern with strings of the same length but consisting of `*` only.

DataMasker.jar is currently located at `target/DataMasker-1.0.jar`. Ignore any `target/original-DataMasker-1.0.jar`.

Additionally, you can also manually build the jar by using `mvn clean package` in the root directory of the project
  - Requires maven to be installed