package org.arquillian.extension.recorder.screenshooter;

import org.arquillian.extension.recorder.screenshooter.event.TakeScreenshot;
import org.jboss.arquillian.core.api.annotation.Observes;

public class TakeScreenshotObserverStub {

    public void populateMetaData(@Observes TakeScreenshot takeScreenshot) {
        Screenshot screenshot = new Screenshot() {
        };
        screenshot.setResource(BlurLevelTestCase.OUTPUT_FILE);
        takeScreenshot.setScreenshot(screenshot);
    }
}
