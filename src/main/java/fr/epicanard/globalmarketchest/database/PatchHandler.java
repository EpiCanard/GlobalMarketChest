package fr.epicanard.globalmarketchest.database;

import com.google.common.collect.ImmutableList;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PatchHandler {
  private DatabaseConnector connector;
  private final List<String> requiredTables = ImmutableList.of(
      DatabaseConnector.tableAuctions,
      DatabaseConnector.tableShops
  );

  public PatchHandler(DatabaseConnector connector) {
    this.connector = connector;
  }

  /**
   * Apply missing patches to database
   */
  public void applyPatches() {
    final List<String> tables = this.connector.listTables();
    final List<Pair<String, String>> availablePatches = this.getAvailablePatches();

    LoggerUtils.info("Searching for database patches...");
    if (tables.containsAll(this.requiredTables)) {
      this.applyMissingPatches(availablePatches, tables);
    } else {
      this.applyFull(availablePatches.stream().map(Pair::getLeft).collect(Collectors.toList()));
    }
  }

  /**
   * Apply full script that recreate all tables
   *
   * @param availablePatches List of existing patch to add inside patch table
   */
  private void applyFull(List<String> availablePatches) {
    LoggerUtils.info("Tables not found, creation of tables...");
    this.applyBatches("full");
    this.addPatches(availablePatches);
  }

  /**
   * Apply missing patches
   *
   * @param availablePatches All available patches
   */
  private void applyMissingPatches(List<Pair<String, String>> availablePatches, List<String> tables) {
    final List<String> applied = this.getAppliedPatches(tables);

    final List<Pair<String, String>> toApply = availablePatches.stream()
        .filter(patch -> !applied.contains(patch.getLeft()))
        .collect(Collectors.toList());
    if (toApply.size() == 0) {
      LoggerUtils.info("Not patches found to apply");
      return;
    }

    LoggerUtils.info(String.format("Found %d patches to apply : %s", toApply.size(),
        toApply.stream().map(Pair::getLeft).collect(Collectors.joining(", "))));

    toApply.stream().allMatch(patch -> {
      LoggerUtils.info(String.format("Applying patch : %s - %s", patch.getLeft(), patch.getRight()));

      final boolean ret = this.applyBatches(patch.getLeft());

      if (ret) {
        this.addPatches(Collections.singletonList(patch.getLeft()));
      }

      return ret;
    });
  }

  /**
   * Execute a script sql file
   *
   * @param file Path to file to read
   */
  private Boolean applyBatches(String file) {
    String queries = replaceScriptVariables(String.join(" ", readFileLines(file + ".sql")));

    return QueryExecutor.of().executeBatches(Arrays.asList(queries.split(";")));
  }

  /**
   * Add a list of patch inside database
   *
   * @param patches List of patch to apply
   */
  private void addPatches(List<String> patches) {
    final InsertBuilder builder = new InsertBuilder(DatabaseConnector.tablePatches);

    patches.forEach(patch -> builder.addValue("patch", patch));

    QueryExecutor.of().execute(builder);
  }

  /**
   * List all available patches with its changelog comment
   *
   * @return List of patch name and patch changelog
   */
  private List<Pair<String, String>> getAvailablePatches() {
    final List<String> changelog = readFileLines("_changelog");
    final List<Pair<String, String>>  availablePatches = new ArrayList<>();

    changelog.stream().filter(line -> !line.isEmpty()).forEach((line) -> {
      final String[] patch = line.split(":");
      if (patch.length > 0) {
        availablePatches.add(Pair.of(patch[0].trim(), (patch.length > 1) ? patch[1].trim() : ""));
      }
    });
    return availablePatches;
  }

  /**
   * List all patches already applied inside database
   *
   * @return List of patches
   */
  private List<String> getAppliedPatches(List<String> existingTables) {
    if (!existingTables.contains(DatabaseConnector.tablePatches)) {
      return Collections.emptyList();
    }

    final SelectBuilder builder = new SelectBuilder(DatabaseConnector.tablePatches);
    List<String> patches = new ArrayList<>();

    QueryExecutor.of().execute(builder, res -> {
      try {
        while (res.next()) {
          patches.add(res.getString("patch"));
        }
      } catch (SQLException e) {}
    });
    return patches;
  }

  /**
   * Replace scripts variable with table names
   *
   * @param query Query to convert
   * @return Query converted
   */
  private String replaceScriptVariables(String query) {
    return query
        .replace("{table_auctions}", DatabaseConnector.tableAuctions)
        .replace("{table_shops}", DatabaseConnector.tableShops)
        .replace("{table_patches}", DatabaseConnector.tablePatches);
  }

  /**
   * Read a file and return a list of not empty lines
   *
   * @param file Name to file
   * @return List of line not empty
   */
  private List<String> readFileLines(String file) {
    final String path = String.format("scripts/%s/%s", this.connector.getDatabaseType(), file);
    final List<String> lines = new ArrayList<>();

    String line;
    try {
      InputStream stream = GlobalMarketChest.plugin.getResource(path);
      BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      while((line = br.readLine()) != null) {
        if (line.length() > 0) {
          lines.add(line);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return lines;
  }
}
