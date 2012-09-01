package org.motechproject.ghana.national.tools.seed.data;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

@Component("ivrContentSeed")
public class IVRContentSeed extends Seed {

    @Autowired
    CMSLiteService cmsLiteService;

    Logger logger = LoggerFactory.getLogger(IVRContentSeed.class);

    @Override
    protected void load() {
        FileInputStream audioInputStream = null;
        try {
            File[] files = new File("audio").listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    Collection<File> audioFiles = FileUtils.listFiles(file, new String[]{"wav"}, false);
                    for (File audioFile : audioFiles) {
                        String checksum = calculateChecksum(audioFile.getAbsolutePath());
                        logger.info("file: " + audioFile.getAbsolutePath());
                        audioInputStream = new FileInputStream(audioFile);
                        cmsLiteService.addContent(new StreamContent(file.getName(), audioFile.getName(), audioInputStream, checksum, "audio/x-wav;charset=UTF-8"));
                        audioInputStream.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred while uploading audio files to cms", e);
        } finally {
            IOUtils.closeQuietly(audioInputStream);
        }
    }

    private String calculateChecksum(String fileName) throws IOException, NoSuchAlgorithmException {
        DigestInputStream digestInputStream = null;
        byte[] buffer = new byte[4096];
        FileInputStream fileInputStream = null;
        StringBuilder checksum = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(fileName);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            while (fileInputStream.read(buffer, 0, buffer.length) != -1) {
                //read till the end of file to calculate MD5 checksum.
            }
            byte[] digest = messageDigest.digest();
            for (byte aDigest : digest) {
                String digit = Integer.toHexString(0xff & aDigest);
                if (digit.length() == 1) {
                    checksum.append('0');
                }
                checksum.append(digit);
            }
        } finally {
            IOUtils.closeQuietly(digestInputStream);
            IOUtils.closeQuietly(fileInputStream);
        }
        return checksum.toString();
    }
}
