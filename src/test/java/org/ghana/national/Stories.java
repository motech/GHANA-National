package org.ghana.national;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehave.web.selenium.*;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;


public class Stories extends JUnitStories {
    public Stories() {
        CrossReference crossReference = new CrossReference().withJsonOnly().withOutputAfterEachStory(true)
                .excludingStoriesWithNoExecutedScenarios(true);
        PropertyWebDriverProvider.Browser browser = PropertyWebDriverProvider.Browser.valueOf(PropertyWebDriverProvider.Browser.class, System.getProperty("browser", "firefox").toUpperCase(Locale.getDefault()));
        ContextView contextView = browser.equals(PropertyWebDriverProvider.Browser.HTMLUNIT) ? new ContextView.NULL() : new LocalFrameContextView().sized(640, 120);
        SeleniumContext seleniumContext = new SeleniumContext();
        SeleniumStepMonitor stepMonitor = new SeleniumStepMonitor(contextView, seleniumContext,
                crossReference.getStepMonitor());
        Format[] formats = new Format[]{new SeleniumContextOutput(seleniumContext), CONSOLE, WEB_DRIVER_HTML, Format.XML};
        StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
                .withCodeLocation(codeLocationFromClass(Stories.class)).withFailureTrace(true)
                .withFailureTraceCompression(true).withDefaultFormats().withFormats(formats)
                .withCrossReference(crossReference);

        Configuration configuration = new SeleniumConfiguration().useSeleniumContext(seleniumContext)
                .useFailureStrategy(new FailingUponPendingStep())
                .useStoryControls(new StoryControls().doResetStateBeforeScenario(false)).useStepMonitor(stepMonitor)
                .useStoryLoader(new LoadFromClasspath(Stories.class))
                .useStoryReporterBuilder(reporterBuilder);
        useConfiguration(configuration);

        ApplicationContext context = new SpringApplicationContextFactory("ghana-steps.xml").createApplicationContext();
        useStepsFactory(new SpringStepsFactory(configuration, context));

    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()).getFile(), asList("**/" + System.getProperty("storyFilter", "*")
                + ".story"), null);
    }

}
