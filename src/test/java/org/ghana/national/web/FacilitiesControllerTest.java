package org.ghana.national.web;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FacilitiesControllerTest {
    @Test
    public void shouldRenderForm() {
        assertThat(new FacilitiesController().newFacilityForm(), is(equalTo("admin/facilities/new")));
    }

}
