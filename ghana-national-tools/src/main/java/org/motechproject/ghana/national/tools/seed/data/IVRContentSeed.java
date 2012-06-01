package org.motechproject.ghana.national.tools.seed.data;

import org.apache.commons.io.IOUtils;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

@Component("ivrContentSeed")
public class IVRContentSeed extends Seed {

    @Autowired
    CMSLiteService cmsLiteService;

     Logger logger = LoggerFactory.getLogger(IVRContentSeed.class);

    @Override
    protected void load() {
        FileInputStream audioInputStream = null;
        CheckedInputStream checkedInputStream = null;
        try {
            audioInputStream = new FileInputStream(new File("/Users/sanjanabayya/Downloads/bugsbunny2.wav"));
            checkedInputStream = new CheckedInputStream(audioInputStream, new Adler32());

            cmsLiteService.addContent(new StreamContent(Language.EN.getValue().toLowerCase(), "welcome.wav",
                    audioInputStream, String.valueOf(checkedInputStream.getChecksum().getValue()), "audio/x-wav;charset=UTF-8"));
        } catch (Exception e) {
           logger.error("Exception occurred while uploading audio files to cms", e);
        } finally {
            IOUtils.closeQuietly(audioInputStream);
            IOUtils.closeQuietly(checkedInputStream);
        }
    }
}
