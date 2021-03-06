package ru.yandex.qatools.allure;

import com.google.common.reflect.ClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ru.yandex.qatools.allure.DummyReportGenerator.getFiles;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 29.07.15
 */
public class AllureMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureMain.class);

    public static final Pattern REPORT_RESOURCE_PATTERN =
            Pattern.compile("^allure\\.report\\.face/(.+)$");

    /**
     * There is no need to instance this class.
     */
    AllureMain() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) { // NOSONAR
            LOGGER.error("There must be at least two arguments");
            return;
        }
        int lastIndex = args.length - 1;

        Path[] inputDirectories = getFiles(Arrays.copyOf(args, lastIndex));
        Path outputDirectory = Paths.get(args[lastIndex]);

        AllureReportGenerator reportGenerator = new AllureReportGenerator(inputDirectories);
        reportGenerator.generate(outputDirectory);

        unpackFace(outputDirectory);
    }

    /**
     * Unpack report face to given directory.
     *
     * @param outputDirectory the output directory to unpack face.
     * @throws IOException if any occurs.
     */
    private static void unpackFace(Path outputDirectory) throws IOException {
        ClassLoader loader = AllureMain.class.getClassLoader();
        for (ClassPath.ResourceInfo info : ClassPath.from(loader).getResources()) {
            Matcher matcher = REPORT_RESOURCE_PATTERN.matcher(info.getResourceName());
            if (matcher.find()) {
                String resourcePath = matcher.group(1);
                Path dest = outputDirectory.resolve(resourcePath);
                Files.createDirectories(dest.getParent());
                try (InputStream input = info.url().openStream()) {
                    Files.copy(input, dest, REPLACE_EXISTING);
                    LOGGER.debug("{} successfully copied.", resourcePath);
                }
            }
        }
    }
}
