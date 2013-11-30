package net.minecrell.serverlistplus.api.configuration.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public final class YAMLLoader {
    private YAMLLoader() {} // Static class

    public static Yaml createLoader(ClassLoader loader, Class<?>... tags) {
        Representer representer = new Representer();
        representer.setPropertyUtils(new FieldOrderPropertyUtils());
        for (Class<?> clazz : tags) {
            representer.addClassTag(clazz, new Tag(clazz.getSimpleName()));
        }

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);

        // Use a custom class loader constructor to allow the YAML loading for plugin classes!
        return new Yaml(new CustomClassLoaderConstructor(loader), representer, options);
    }
}
