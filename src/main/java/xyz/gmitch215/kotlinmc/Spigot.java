package xyz.gmitch215.kotlinmc;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Spigot extends JavaPlugin {

    @Override
    public void onEnable() {
        Properties fallback = new Properties();
        try (InputStream in = getResource("versions.properties")) {
            if (in != null) fallback.load(in);
        } catch (IOException ignored) {
        }

        getLogger().info("Bundled runtime: "
                + "kotlin-stdlib " + versionOf("kotlin.Unit", fallback.getProperty("kotlin", "unknown"))
                + ", kotlinx-coroutines " + versionOf("kotlinx.coroutines.Job", fallback.getProperty("kotlinx-coroutines", "unknown"))
                + ", kotlinx-serialization " + versionOf("kotlinx.serialization.json.Json", fallback.getProperty("kotlinx-serialization", "unknown"))
                + ", mccoroutine " + versionOf("com.github.shynixn.mccoroutine.bukkit.MCCoroutine", fallback.getProperty("mccoroutine", "unknown")));
    }

    private String versionOf(String className, String fallback) {
        try {
            Package p = Class.forName(className, false, getClass().getClassLoader()).getPackage();
            String version = p == null ? null : p.getImplementationVersion();
            return version != null ? version : fallback;
        } catch (ClassNotFoundException e) {
            return fallback + " (class missing!)";
        }
    }
}
