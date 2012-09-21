package org.motechproject.ghana.national.repository;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.ghana.national.domain.json.MobileMidwifeCampaignRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;


@Component
public class CampaignJsonReader {

    @Value("#{messageCampaignProperties['messagecampaign.definition.directory']}")
    String definitionsDirectoryName;

    public List<MobileMidwifeCampaignRecord> records;

    @PostConstruct
    public void init() {
        String schedulesDirectoryPath = getClass().getResource(definitionsDirectoryName).getPath();
        File[] definitionFiles = new File(schedulesDirectoryPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(".json");
            }
        });
        List<String> scheduleDefinitionsFileNames = extract(definitionFiles, on(File.class).getName());
        String scheduleDefinitionsDirectoryName = definitionsDirectoryName;
        records = records(scheduleDefinitionsDirectoryName, scheduleDefinitionsFileNames);
    }

    private List<MobileMidwifeCampaignRecord> records(String scheduleDefinitionsDirectoryName, List<String> scheduleDefinitionsFileNames) {
        MotechJsonReader motechJsonReader = new MotechJsonReader();

        List<MobileMidwifeCampaignRecord> campaignRecords = new ArrayList<>();
        Type type = new TypeToken<List<MobileMidwifeCampaignRecord>>() {
        }.getType();
        for (String filename : scheduleDefinitionsFileNames) {
            List<MobileMidwifeCampaignRecord> campaignList = (List<MobileMidwifeCampaignRecord>) motechJsonReader.readFromFile(scheduleDefinitionsDirectoryName + "/" + filename, type);
            for (MobileMidwifeCampaignRecord mobileMidwifeCampaignRecord : campaignList) {
                campaignRecords.add(mobileMidwifeCampaignRecord);
            }
        }
        return campaignRecords;
    }

}
