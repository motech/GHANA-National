package org.motechproject.ghana.national.web.tools.seed.data;

import org.apache.commons.io.IOUtils;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.web.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@Component
public class FacilityMigrationSeed extends Seed {

    @Autowired
    AllFacilities allFacilities;

    @Override
    protected void load() {
        try {
            final List<String> lines = IOUtils.readLines(new FileReader(new File(this.getClass().getClassLoader().getResource("migration-data/facility.txt").toURI())));
            for (String line : lines) {
                final String[] strings = line.split(",");
                allFacilities.saveLocally(new Facility().mrsFacilityId(strings[0]).motechId(strings[1]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
