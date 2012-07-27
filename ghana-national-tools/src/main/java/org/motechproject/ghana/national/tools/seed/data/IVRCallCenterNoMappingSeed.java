package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.IVRChannelMapping;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.repository.AllIvrCallCenterNoMappings;
import org.motechproject.ghana.national.repository.AllIvrChannelMappings;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("ivrCallCenterNoMappingSeed")
public class IVRCallCenterNoMappingSeed extends Seed {
    Logger logger = LoggerFactory.getLogger(IVRCallCenterNoMappingSeed.class);

    @Autowired
    AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;

    @Override
    protected void load() {

        try {
            List<IVRCallCenterNoMapping> ivrCallCenterNoMappings = Arrays.asList(new IVRCallCenterNoMapping().phoneNumber("0111111111").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(0, 0)).endTime(new Time(9, 30)),
                    new IVRCallCenterNoMapping().phoneNumber("0222222222").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(9, 31)).endTime(new Time(17, 45)),
                    new IVRCallCenterNoMapping().phoneNumber("0111111111").language(Language.EN).dayOfWeek(DayOfWeek.Monday).startTime(new Time(17, 46)).endTime(new Time(23, 59)));

            for (IVRCallCenterNoMapping mapping : ivrCallCenterNoMappings) {
                allIvrCallCenterNoMappings.add(mapping);
            }

        } catch (Exception e) {
            logger.error("Exception occurred while uploading verboice channel to phone number pattern mapping", e);
        }
    }
}
