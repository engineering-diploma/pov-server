package cloud.ptl.povserver.ffmpeg.convert;

import cloud.ptl.povserver.data.model.ResourceDAO;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public abstract class ResourceConverter {
    public abstract boolean supports(ConvertRequest.Format format);

    public abstract ResourceDAO convert(ConvertRequest convertRequest) throws IOException, InterruptedException;

    public void run(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        // connect to process streams
        BufferedReader outputBufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;

        while ((line = outputBufferedReader.readLine()) != null) {
            log.info(line);
        }
        while ((line = errorBufferedReader.readLine()) != null) {
            log.info(line);
        }

        process.waitFor();
        if (process.exitValue() == 0) {
            // processed successfully
            // rewrite data to resource
            return;
        } else {
            // some error occurred
            throw new RuntimeException("Cannot convert file to given resolution, ffmpeg exit code in none-zero");
        }
    }
}
