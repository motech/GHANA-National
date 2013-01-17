package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.repository.AllIvrCallCenterNoMappings;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("ivrCallCenterNoMappingSeed")
public class IVRCallCenterNoMappingSeed extends Seed {
    Logger logger = LoggerFactory.getLogger(IVRCallCenterNoMappingSeed.class);

    @Autowired
    AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;

    private DayOfWeek[] weekDays() {
        return new DayOfWeek[]{DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday, DayOfWeek.Thursday, DayOfWeek.Friday};
    }

    @Override
    protected void load() {

        try {
            allIvrCallCenterNoMappings.removeAll();
            ArrayList<IVRCallCenterNoMapping> mappings = new ArrayList<IVRCallCenterNoMapping>();
            for (DayOfWeek weekDay : weekDays()) {
                mappings.add(dayTime(weekDay, "0206369910", Language.EN));
                mappings.add(dayTime(weekDay, "0509039932", Language.KAS));
                //mappings.add(dayTime(weekDay, "0509039934", Language.FAN));
                mappings.add(dayTime(weekDay, "0208516183", Language.FAN));
                mappings.add(dayTime(weekDay, "0509039933", Language.NAN));
                mappings.add(nurseLine(weekDay, "0208516182"));
                mappings.add(evening(weekDay, "0544347554", Language.EN));
                mappings.add(evening(weekDay, "0544347554", Language.FAN));
                mappings.add(earlyMorning(weekDay, "0544347552", Language.EN));
                mappings.add(earlyMorning(weekDay, "0544347552", Language.FAN));
            }
            mappings.add(weekend(DayOfWeek.Saturday, "0544347552", Language.EN));
            mappings.add(weekend(DayOfWeek.Saturday, "0544347552", Language.FAN));

            for (IVRCallCenterNoMapping mapping : mappings) {
                allIvrCallCenterNoMappings.add(mapping);
            }

        } catch (Exception e) {
            logger.error("Exception occurred while uploading verboice channel to phone number pattern mapping", e);
        }
    }

    private IVRCallCenterNoMapping nurseLine(DayOfWeek weekDay, String phoneNumber) {
        return new IVRCallCenterNoMapping().phoneNumber(phoneNumber).nurseLine(true).dayOfWeek(weekDay).startTime(new Time(8, 0)).endTime(new Time(17, 0)).sipChannel(true);
    }

    private IVRCallCenterNoMapping weekend(DayOfWeek day, String phoneNumber, Language language) {
        return new IVRCallCenterNoMapping().phoneNumber(phoneNumber).language(language).dayOfWeek(day).startTime(new Time(8, 0)).endTime(new Time(16, 0));
    }

    private IVRCallCenterNoMapping earlyMorning(DayOfWeek weekDay, String phoneNumber, Language language) {
        return new IVRCallCenterNoMapping().phoneNumber(phoneNumber).language(language).dayOfWeek(weekDay).startTime(new Time(6, 0)).endTime(new Time(8, 0));
    }

    private IVRCallCenterNoMapping evening(DayOfWeek weekDay, String phoneNumber, Language language) {
        return new IVRCallCenterNoMapping().phoneNumber(phoneNumber).language(language).dayOfWeek(weekDay).startTime(new Time(17, 1)).endTime(new Time(21, 0));
    }

    private IVRCallCenterNoMapping dayTime(DayOfWeek weekDay, String phoneNumber, Language language) {
        return new IVRCallCenterNoMapping().phoneNumber(phoneNumber).language(language).dayOfWeek(weekDay).startTime(new Time(8, 1)).endTime(new Time(17, 0)).sipChannel(true);
    }
}
