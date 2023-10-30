package fr.joupi.api.module;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Getter
public class ModuleManager {

    private final JavaPlugin plugin;

    private final List<Pair<ModuleInfo, Module>> modules;
    private final Yaml yaml;

    public ModuleManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.modules = new ArrayList<>();
        this.yaml = new Yaml();
        createFile();
    }

    public void loadModules() {
        for (File file : Objects.requireNonNull(getModulesDirectory().listFiles())) {
            if (file.getName().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(file)) {
                    JarEntry moduleEntry = jarFile.getJarEntry("module.yml");

                    try (InputStream in = jarFile.getInputStream(moduleEntry)) {
                        Map<String, Object> data = (Map<String, Object>) getYaml().load(in);
                        ModuleInfo moduleInfo = new ModuleInfo((String) data.get("name"), (String) data.get("version"), (String) data.get("path"), true);
                        Class<?> moduleClass = Class.forName(moduleInfo.getPath());

                        if (Module.class.isAssignableFrom(moduleClass)) {
                            Module module = (Module) moduleClass.getDeclaredConstructor().newInstance();
                            getModules().add(Pair.of(moduleInfo, module));
                            module.onEnable();
                            System.out.println("[Module] New module added with name " + moduleInfo.getName() + " - version " + moduleInfo.getVersion() + " - path " + moduleInfo.getPath());
                        }
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
    }

    public void enableModule(ModuleInfo moduleInfo) {
        getModules().stream().filter(pair -> pair.getLeft().equals(moduleInfo)).findFirst().ifPresent(pair -> {
            pair.getLeft().setEnable(true);
            System.out.println("[Module] enable " + moduleInfo.getName());
            pair.getRight().onEnable();
        });
    }

    public void disableModule(ModuleInfo moduleInfo) {
        getModules().stream().filter(pair -> pair.getLeft().equals(moduleInfo)).findFirst().ifPresent(pair -> {
            pair.getLeft().setEnable(false);
            System.out.println("[Module] disable " + moduleInfo.getName());
            pair.getRight().onDisable();
        });
    }

    public List<Pair<ModuleInfo, Module>> getEnabledModules() {
        return getModules().stream().filter(pair -> pair.getLeft().isEnable()).collect(Collectors.toList());
    }

    public List<Pair<ModuleInfo, Module>> getDisabledModules() {
        return getModules().stream().filter(pair -> !pair.getLeft().isEnable()).collect(Collectors.toList());
    }

    private void createFile() {
        CompletableFuture.runAsync(() -> Optional.ofNullable(getModulesDirectory()).stream().filter(((Predicate<? super File>) File::exists).negate()).findFirst().ifPresent(file -> file.getParentFile().mkdirs()))
                .whenComplete((unused, throwable) -> loadModules());
    }

    public File getModulesDirectory() {
        return new File(getPlugin().getDataFolder().getAbsolutePath() + "/modules");
    }

}
