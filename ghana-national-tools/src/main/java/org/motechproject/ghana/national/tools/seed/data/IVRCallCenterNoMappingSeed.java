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

import java.util.Arrays;
import java.util.List;

@Component("ivrCallCenterNoMappingSeed")
public class IVRCallCenterNoMappingSeed extends Seed {
    Logger logger = LoggerFactory.getLogger(IVRCallCenterNoMappingSeed.class);

    @Autowired
    AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;

    @Override
    protected void load() {

        try {
            List<IVRCallCenterNoMapping> ivrCallCenterNoMappings = Arrays.asList(
                    //Mon-Friday	06-08	English	054 434 7552
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),

                    //Mon-Fri	08-17	English	020 636 9910
                    new IVRCallCenterNoMapping().phoneNumber("0206369910").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206369910").language(Language.EN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206369910").language(Language.EN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206369910").language(Language.EN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206369910").language(Language.EN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),

                    //Mon-Fri	17-21	English	054 434 7554
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.EN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.EN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.EN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.EN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),

                    //Sat	08-16	English	054 434 7552
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.EN).dayOfWeek(DayOfWeek.Saturday).startTime(new Time(8, 0)).endTime(new Time(16, 0)),

                    //Mon-Friday	06-08	Fanti	054 434 7552
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(6, 0)).endTime(new Time(8, 0)),

                    //Mon-Fri	08-17	Fanti	020 851 6183
                    new IVRCallCenterNoMapping().phoneNumber("0208516183").language(Language.FAN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0208516183").language(Language.FAN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0208516183").language(Language.FAN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0208516183").language(Language.FAN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0208516183").language(Language.FAN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(8, 1)).endTime(new Time(17, 0)),

                    //Mon-Fri	17-21	Fanti 054 434 7554
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.FAN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.FAN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.FAN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.FAN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0544347554").language(Language.FAN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(17, 1)).endTime(new Time(21, 0)),

                    //Sat	08-16	Fanti	054 434 7552
                    new IVRCallCenterNoMapping().phoneNumber("0544347552").language(Language.FAN).dayOfWeek(DayOfWeek.Saturday).startTime(new Time(8, 0)).endTime(new Time(16, 0)),

                    //Mon-Fri	08-17	Kassim	020 639 9908
                    new IVRCallCenterNoMapping().phoneNumber("0206399908").language(Language.KAS).dayOfWeek(DayOfWeek.Monday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399908").language(Language.KAS).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399908").language(Language.KAS).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399908").language(Language.KAS).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399908").language(Language.KAS).dayOfWeek(DayOfWeek.Friday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),

                    //Mon-Fri	08-17	Nankam	020 639 9906
                    new IVRCallCenterNoMapping().phoneNumber("0206399906").language(Language.NAN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399906").language(Language.NAN).dayOfWeek(DayOfWeek.Tuesday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399906").language(Language.NAN).dayOfWeek(DayOfWeek.Wednesday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399906").language(Language.NAN).dayOfWeek(DayOfWeek.Thursday).startTime(new Time(8, 0)).endTime(new Time(17, 0)),
                    new IVRCallCenterNoMapping().phoneNumber("0206399906").language(Language.NAN).dayOfWeek(DayOfWeek.Friday).startTime(new Time(8, 0)).endTime(new Time(17, 0)));

            for (IVRCallCenterNoMapping mapping : ivrCallCenterNoMappings) {
                allIvrCallCenterNoMappings.add(mapping);
            }

        } catch (Exception e) {
            logger.error("Exception occurred while uploading verboice channel to phone number pattern mapping", e);
        }
    }
}
