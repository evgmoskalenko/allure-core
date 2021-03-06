package ru.yandex.qatools.allure;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.testng.TestNG;
import ru.yandex.qatools.allure.testng.AllureTestListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 28.01.15
 */
public abstract class BasicListenerTest {

    private static final String DEFAULT_SUITE_NAME = "suite";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public Path resultsDirectory;

    @Before
    public void setUp() throws Exception {
        resultsDirectory = folder.newFolder().toPath();
        AllureTestListener listener = new AllureTestListener();
        listener.setLifecycle(new Allure(new AllureConfig() {
            @Override
            public String getRemoveAttachmentsPattern() {
                return "a^";
            }

            @Override
            public Path getResultsDirectory() {
                return resultsDirectory;
            }

            @Override
            public Charset getAttachmentsEncoding() {
                return StandardCharsets.UTF_8;
            }

            @Override
            public int getMaxTitleLength() {
                return 120;
            }
        }));


        TestNG testNG = new TestNG(false);
        testNG.setDefaultSuiteName(DEFAULT_SUITE_NAME);
        testNG.setServiceLoaderClassLoader(null);
        testNG.addListener((Object) listener);

        configure(testNG);

        testNG.run();
    }

    public abstract void configure(TestNG testNG);
}
