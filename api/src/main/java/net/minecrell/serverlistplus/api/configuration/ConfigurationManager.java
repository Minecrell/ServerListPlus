package net.minecrell.serverlistplus.api.configuration;

import net.minecrell.serverlistplus.api.ServerListPlusAPI;
import net.minecrell.serverlistplus.api.configuration.yaml.YAMLLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class ConfigurationManager {
    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;

    private static final String HEADER_FILENAME = "HEADER";
    private static String[] FILE_HEADER;

    private final ServerListPlusAPI api;
    private final Yaml yaml;

    private List<String> lines;
    private AdvancedConfiguration config;

    public ConfigurationManager(ServerListPlusAPI api) throws IOException {
        this.api = api;

        // Load file header if it wasn't loaded before
        if (FILE_HEADER == null) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(HEADER_FILENAME)) {
                List<String> header = readAllLines(in);
                FILE_HEADER = header.toArray(new String[header.size()]);
            } catch (IOException e) {
                api.getLogger().log(Level.WARNING, "Unable to read file header!", e);
            }
        }

        this.yaml = YAMLLoader.createLoader(this.getClass().getClassLoader(), AdvancedConfiguration.class);
    }

    public List<String> getLines() {
        return lines;
    }

    public AdvancedConfiguration getAdvanced() {
        return config;
    }

    public boolean reload() throws IOException {
        api.getLogger().info("Reloading configuration!");

        Path rootFolder = api.getPlugin().getDataFolder().toPath();

        try {
            api.getLogger().info("Loading server list lines...");
            Path linesPath = rootFolder.resolve(LINES_FILENAME);
            this.lines = this.loadLines(linesPath);
            api.getLogger().info("Done.");
        } catch (Exception e) {
            api.getLogger().log(Level.SEVERE, "An internal error occurred while reloading the plugin configuration!", e); throw e;
        }

        try {
            api.getLogger().info("Loading advanced configuration...");
            Path configPath = rootFolder.resolve(CONFIG_FILENAME);
            this.config = loadConfiguration(configPath);
            api.getLogger().info("Done.");
        } catch (Exception e) {
            api.getLogger().log(Level.WARNING, "An internal error occurred while loading the advanced plugin configuration!", e); return false;
        }

        return true;
    }

    private static final String LINES_FILENAME = "Lines.txt";

    private List<String> loadLines(Path linesPath) throws IOException {
        this.createDefault(linesPath, LINES_FILENAME, true);

        if (Files.exists(linesPath)) {
            try (BufferedReader reader = Files.newBufferedReader(linesPath, FILE_CHARSET)) {
                String line = null; List<String> lines = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#")) continue; // Skip comment lines
                    lines.add(line);
                }

                return lines;
            }
        } else {
            api.getLogger().info("Creating new configuration: " + LINES_FILENAME);
            writeHeader(linesPath);
            return new ArrayList<>();
        }
    }

    private static final String CONFIG_FILENAME = "Advanced.yml";
    private static final String[] CONFIG_COMMENTS = new String[] {
            "ADVANCED CONFIGURATION"
    };

    private AdvancedConfiguration loadConfiguration(Path configPath) throws IOException {
        this.createDefault(configPath, CONFIG_FILENAME, true);

        AdvancedConfiguration config = null;

        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath, FILE_CHARSET)) {
                config = yaml.loadAs(reader, AdvancedConfiguration.class);
            }
        } else {
            api.getLogger().info("Creating new configuration: " + LINES_FILENAME);
            config = new AdvancedConfiguration();
        }

        if (config != null) {
            api.getLogger().info("Advanced configuration successfully loaded!");
            api.getLogger().info("Saving advanced configuration...");

            try (BufferedWriter writer = Files.newBufferedWriter(configPath, FILE_CHARSET)) {
                writeHeader(writer);
                writeCommentLines(writer, Arrays.asList(CONFIG_COMMENTS));

                yaml.dump(config, writer);
            }

            api.getLogger().info("Advanced configuration saved!");
        } else throw new NullPointerException("YAML configuration loader has returned null!");

        return config;
    }

    private void createDefault(Path path, String fileName, boolean header) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());

            try (InputStream defaultContent = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (defaultContent != null) {
                    api.getLogger().info("Copying default configuration: " + fileName);

                    if (FILE_HEADER != null) {
                        // TODO: Is there a better way to prepend lines to a file?
                        try (BufferedWriter writer = Files.newBufferedWriter(path, FILE_CHARSET)) {
                            writeHeader(writer);

                            BufferedReader reader = new BufferedReader(new InputStreamReader(defaultContent, FILE_CHARSET));
                            String line = null;

                            while ((line = reader.readLine()) != null) {
                                writer.write(line); writer.newLine();
                            }
                        }
                    } else {
                        // Just copy the file instead!
                        Files.copy(defaultContent, path);
                    }
                }
            }
        }
    }

    private static void writeHeader(Path path, OpenOption... options) throws IOException {
        if (FILE_HEADER == null) return;
        try (BufferedWriter writer = Files.newBufferedWriter(path, FILE_CHARSET, options)) {
            writeHeader(writer);
        }
    }

    private static void writeHeader(BufferedWriter writer) throws IOException {
        if (FILE_HEADER == null) return;
        writeCommentLines(writer, Arrays.asList(FILE_HEADER));
        writer.newLine();
    }

    private static void writeCommentLines(BufferedWriter writer, Iterable<String> lines) throws IOException {
        for (String line : lines) writeCommentLine(writer, line);
    }

    private static void writeCommentLine(BufferedWriter writer, String comment) throws IOException {
        writer.write("# "); writer.write(comment); writer.newLine();
    }

    private static List<String> readAllLines(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, FILE_CHARSET));
        String line = null; List<String> result = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            result.add(line);
        } return result;
    }
}
