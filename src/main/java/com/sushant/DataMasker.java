package com.sushant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DataMasker {
  private static List<Map<String, String>> data = null;

  // Check if key matches, and mask value
  private static void maskKey(Pattern pattern) {
    data.forEach(entry -> {
      entry.entrySet().forEach(e -> {
        if (pattern.matcher(e.getKey()).find()) {
          e.setValue("*".repeat(e.getValue().length()));
        }
      });
    });
  }

  // Find parts of value that match regex, and mask them
  private static void maskValue(Pattern pattern) {
    data.forEach(entry -> {
      entry.replaceAll((key, value) ->
          pattern.matcher(value).replaceAll(matchResult -> "*".repeat(matchResult.group().length()))
      );
    });
  }

  private static void maskData(Path dataPath, Path rulesPath) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();

    // Read data and rules from JSON files
    data = objectMapper.readValue(dataPath.toFile(), new TypeReference<>(){});
    List<String> rules = objectMapper.readValue(rulesPath.toFile(), new TypeReference<>() {
    });
    if (data == null || rules == null) {
      System.err.println("Error: Data or rules is null");
      return;
    }

    // Apply rules
    for (String rule : rules) {
      String[] ruleParts = rule.split(":", 2);
      if (ruleParts.length != 2) {
        System.err.println("Error: Invalid rule: " + rule);
        return;
      }
      String type = ruleParts[0];
      String regex = ruleParts[1];
      Pattern pattern = Pattern.compile(regex);

      switch (type) {
        case "k" -> maskKey(pattern);
        case "v" -> maskValue(pattern);
        default -> System.out.println("Error: Rule type must be 'k' or 'v', got: " + type);
      }
    }

    objectMapper.writeValue(dataPath.toFile(), data);
  }

  public static void main(String[] args) {
    if (args.length < 2 || args[0].equals("-h") || args[0].equals("--help")) {
      System.out.println("Usage: <data.json> <rules.json>");
      System.out.println("Argument 1: Path to data json file");
      System.out.println("Argument 2: Path to json array of strings in the following formats:");
      System.out.println("  k:<regex> - Match by key and mask entire value");
      System.out.println("  v:<regex> - Match by value and mask parts of value that match to regex");
      return;
    }

    Path dataPath = Paths.get(args[0]);
    Path rulesPath = Paths.get(args[1]);

    try {
      if (!Files.exists(dataPath)) {
        System.err.println("Error: Data file does not exist");
        return;
      }
      if (!Files.exists(rulesPath)) {
        System.err.println("Error: Rules file does not exist");
        return;
      }

      maskData(dataPath, rulesPath);
    } catch (Exception e) {
      System.err.println("Error reading files: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
