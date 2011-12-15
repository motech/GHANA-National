package org.motechproject.ghana.national.tools.seed.data;

import org.apache.commons.io.IOUtils;
import org.motechproject.ghana.national.domain.StaffType;
import org.motechproject.ghana.national.repository.AllStaffTypes;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Component
public class StaffTypeSeed extends Seed {

    @Autowired
    private AllStaffTypes allStaffTypes;

    @Override
    protected void load() {
        try {
            final List<String> lines = IOUtils.readLines(new FileReader(new File(this.getClass().getClassLoader().getResource("migration-data/staff.txt").toURI())));
            for (String line : lines) {
                final String[] strings = line.split(",");
                allStaffTypes.add(new StaffType(strings[0], strings[1]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
