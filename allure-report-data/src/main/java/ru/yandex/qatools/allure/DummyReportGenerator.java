package ru.yandex.qatools.allure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Artem Eroshenko eroshenkoam@yandex-team.ru
 *         Date: 1/22/14
 */
public final class DummyReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyReportGenerator.class);

    DummyReportGenerator() {
    }

    /**
     * Generate Allure report data from directories with allure report results.
     *
     * @param args a list of directory paths. First (args.length - 1) arguments -
     *             results directories, last argument - the folder to generated data
     */
    public static void main(String[] args) {
        if (args.length < 2) { // NOSONAR
            LOGGER.error("There must be at least two arguments");
            return;
        }
        int lastIndex = args.length - 1;
        AllureReportGenerator report = new AllureReportGenerator(
                getFiles(Arrays.copyOf(args, lastIndex))
        );
        report.generate(Paths.get(args[lastIndex]));
    }

    public static Path[] getFiles(String[] paths) {
        List<Path> files = new ArrayList<>();
        for (String path : paths) {
            files.add(Paths.get(path));
        }
        return files.toArray(new Path[files.size()]);
    }
}
