package ru.yandex.qatools.allure.data.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.allure.data.AllureTestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for all tab plugins.
 *
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 17.04.15
 * @see ProcessPlugin
 */
public abstract class TabPlugin implements ProcessPlugin<AllureTestCase> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TabPlugin.class);

    /**
     * Plugin name should be always specified
     */
    private String pluginName = getClass().isAnnotationPresent(Name.class) ?
            getClass().getAnnotation(Name.class).value() : null;

    @Override
    public Class<AllureTestCase> getType() {
        return AllureTestCase.class;
    }

    /**
     * This method creates a {@link PluginData} for each field with
     * {@link Plugin.Data} annotation.
     *
     * @see #getFileName(Field)
     * @see #getFieldValue(Field)
     */
    @Override
    public List<PluginData> getPluginData() {
        List<PluginData> results = new ArrayList<>();
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Data.class)) {
                String fileName = getFileName(field);
                results.add(new PluginData(fileName, getFieldValue(field)));
            }
        }
        return results;
    }

    /**
     * Try to get field value. Field with {@link Plugin.Data} annotation should
     * be accessable.
     */
    private Object getFieldValue(Field field) {
        try {
            field.setAccessible(true);
            return field.get(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error("Can't access to field value", e);
            return null;
        }
    }

    /**
     * Get file name for field with {@link Plugin.Data} annotation. If name
     * is default then use {@link #pluginName} as file name and <code>.json</code>
     * as file extension.
     *
     * @see #getPluginName()
     */
    private String getFileName(Field field) {
        String fileName = field.getAnnotation(Data.class).value();
        if ("##default".equals(fileName)) {
            fileName = getPluginName() + ".json";
        }
        return fileName;
    }

    /**
     * Get name of plugin. Name should be specified in {@link Plugin.Name}
     * annotation.
     */
    public String getPluginName() {
        return pluginName;
    }

    /**
     * Verify plugin class. Tab plugin should not be private, abstract or interface.
     * Also class should be annotated with {@link Plugin.Name}
     * annotation. Returns true if given class is valid tab plugin, false otherwise.
     * Plugin class should not be null.
     */
    public static boolean isValid(Class<? extends TabPlugin> pluginClass) {
        return pluginClass != null && pluginClass.isAnnotationPresent(Name.class) &&
                checkModifiers(pluginClass) && checkFieldsWithDataAnnotation(pluginClass);
    }

    /**
     * Check given class modifiers. Tab plugin should not be private or abstract
     * or interface.
     */
    private static boolean checkModifiers(Class<? extends TabPlugin> pluginClass) {
        int modifiers = pluginClass.getModifiers();
        return !Modifier.isAbstract(modifiers) &&
                !Modifier.isInterface(modifiers) &&
                !Modifier.isPrivate(modifiers);
    }

    /**
     * Check fields with {@link Data} annotation. Firstly filter all declared fields
     * and then check it using {@link #shouldHasUniqueValues(List)}
     */
    private static boolean checkFieldsWithDataAnnotation(Class<? extends TabPlugin> pluginClass) {
        List<Field> dataFields = new ArrayList<>();
        for (Field field : pluginClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Data.class)) {
                dataFields.add(field);
            }
        }
        return shouldHasUniqueValues(dataFields);
    }

    /**
     * Check that each given field has unique value in {@link Data} annotation.
     */
    private static boolean shouldHasUniqueValues(List<Field> dataFields) {
        Set<String> dataValues = new HashSet<>();
        for (Field field : dataFields) {
            String value = field.getAnnotation(Data.class).value();
            if (dataValues.contains(value)) {
                return false;
            }
            dataValues.add(value);
        }
        return true;
    }
}
