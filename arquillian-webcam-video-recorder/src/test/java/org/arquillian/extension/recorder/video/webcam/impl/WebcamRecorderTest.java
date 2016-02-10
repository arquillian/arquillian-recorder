package org.arquillian.extension.recorder.video.webcam.impl;

import org.arquillian.extension.recorder.video.webcam.configuration.WebcamVideoConfiguration;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class WebcamRecorderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    @Ignore
    public void shouldRecordWebcam() throws IOException, InterruptedException {

        ReporterConfiguration reporterConfiguration = new ReporterConfiguration();
        WebcamVideoConfiguration webcamVideoConfiguration = new WebcamVideoConfiguration(reporterConfiguration);

        WebcamRecorder webcamRecorder = new WebcamRecorder(webcamVideoConfiguration);

        File output = this.temporaryFolder.newFile("video.mp4");
        webcamRecorder.startRecording(output);
        Thread.sleep(6000);
        webcamRecorder.stopRecording();
        System.out.println(output.getAbsolutePath());
    }

}
