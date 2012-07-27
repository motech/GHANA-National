package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.IVRCallCenterNoMapping;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.repository.AllIvrCallCenterNoMappings;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IvrCallCenterNoMappingService {
    private AllIvrCallCenterNoMappings allIvrCallCenterNoMappings;

    Logger logger = LoggerFactory.getLogger(IvrCallCenterNoMappingService.class);

    @Autowired
    public IvrCallCenterNoMappingService(AllIvrCallCenterNoMappings allIvrCallCenterNoMappings) {
        this.allIvrCallCenterNoMappings = allIvrCallCenterNoMappings;
    }


    public String getCallCenterPhoneNumber(Language language, DayOfWeek dayOfWeek, Time time){
        List<IVRCallCenterNoMapping> allMappings = allIvrCallCenterNoMappings.allMappings();
        for (IVRCallCenterNoMapping mapping : allMappings) {
            if(mapping.getLanguage().equals(language) && mapping.getDayOfWeek().equals(dayOfWeek) && fallsBetweenIncludingBounds(time, mapping.getStartTime(), mapping.getEndTime()))
                return mapping.getPhoneNumber();
        }
        logger.warn("Unable to find a call center number for the provided criteria, " + language + ", " + dayOfWeek + ", " + time + ". Returning the first call center no: " + allMappings.get(0).getPhoneNumber());
        return null;
    }

    private boolean fallsBetweenIncludingBounds(Time time, Time startTime, Time endTime) {
        return time.compareTo(startTime) == 0 || time.compareTo(endTime) == 0 || (time.compareTo(startTime) > 0 && time.compareTo(endTime) < 0);
    }
}
