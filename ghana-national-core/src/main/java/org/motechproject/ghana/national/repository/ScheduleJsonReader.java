package org.motechproject.ghana.national.repository;

import com.google.gson.reflect.TypeToken;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.PeriodFormatterBuilder;
import org.joda.time.format.PeriodParser;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.ghana.national.domain.json.AlertRecord;
import org.motechproject.ghana.national.domain.json.MilestoneRecord;
import org.motechproject.ghana.national.domain.json.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Repository
public class ScheduleJsonReader {

    private final List<ScheduleRecord> records;

    private List<PeriodParser> parsers = new ArrayList<PeriodParser>(){{
        add(new PeriodFormatterBuilder().appendYears().appendSuffix(" year", " years").toParser());
        add(new PeriodFormatterBuilder().appendMonths().appendSuffix(" month", " months").toParser());
        add(new PeriodFormatterBuilder().appendWeeks().appendSuffix(" week", " weeks").toParser());
        add(new PeriodFormatterBuilder().appendDays().appendSuffix(" day", " days").toParser());
        add(new PeriodFormatterBuilder().appendHours().appendSuffix(" hour", " hours").toParser());
        add(new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minute", " minutes").toParser());
        add(new PeriodFormatterBuilder().appendSeconds().appendSuffix(" second", " seconds").toParser());
    }};

    @Autowired
    public ScheduleJsonReader(@Value("#{schedule_tracking['schedule.definitions.directory']}") String definitionsDirectoryName) {
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

    private List<ScheduleRecord> records(String scheduleDefinitionsDirectoryName, List<String> scheduleDefinitionsFileNames) {
        MotechJsonReader motechJsonReader = new MotechJsonReader();

        List<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
        Type type = new TypeToken<ScheduleRecord>() {
        }.getType();
        for (String filename : scheduleDefinitionsFileNames)
            scheduleRecords.add((ScheduleRecord) motechJsonReader.readFromFile(scheduleDefinitionsDirectoryName + "/" + filename, type));
        return scheduleRecords;
    }

    public Period validity(String scheduleName, String milestoneName, String windowName) {
        for (ScheduleRecord record : records) {
            if (record.name().equals(scheduleName)) {
                for (MilestoneRecord milestoneRecord : record.milestoneRecords()) {
                    if(milestoneRecord.name().equals(milestoneName)){
                        for (AlertRecord alertRecord : milestoneRecord.alerts()) {
                            if(alertRecord.window().equals(windowName)){
                                return parse(alertRecord.validity());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Period parse(String s) {
        ReadWritablePeriod period = new MutablePeriod();
        for (PeriodParser parser : parsers) {
            if (parser.parseInto(period, s, 0, Locale.getDefault()) > 0) {
                return period.toPeriod();
            }
        }
        return period.toPeriod();
    }
}
