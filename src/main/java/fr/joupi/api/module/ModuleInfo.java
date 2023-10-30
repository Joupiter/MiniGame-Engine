package fr.joupi.api.module;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ModuleInfo {

    private String name, version, path;
    private boolean enable;

    @Override
    public String toString() {
        return "ModuleInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                ", enable=" + enable +
                '}';
    }

}
