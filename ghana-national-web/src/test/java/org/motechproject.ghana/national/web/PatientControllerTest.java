package org.motechproject.ghana.national.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.helper.FacilityHelper;
import org.motechproject.ghana.national.service.IdentifierGenerationService;
import org.motechproject.ghana.national.service.PatientService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientControllerTest {
    PatientController patientController;
    @Mock
    PatientService mockPatientService;
    @Mock
    IdentifierGenerationService mockIdentifierGenerationService;
    @Mock
    MessageSource mockMessageSource;
    @Mock
    FacilityHelper mockFacilityHelper;

    @Before
    public void setUp() {
        initMocks(this);
        patientController = new PatientController(mockPatientService, mockIdentifierGenerationService, mockMessageSource, mockFacilityHelper);
    }

    @Test
    public void shouldRenderPageToCreateAPatient() {
        final ModelMap modelMap = new ModelMap();
        final String key = "key";
        when(mockFacilityHelper.locationMap()).thenReturn(new HashMap<String, Object>() {{
            put(key, new Object());
        }});
        patientController.newPatientForm(modelMap);
        verify(mockFacilityHelper).locationMap();
        assertNotNull(modelMap.get(key));
    }

    @Test
    public void shouldFormatDateInDD_MM_YYYYFormat() throws ParseException {
        WebDataBinder binder = mock(WebDataBinder.class);
        patientController.initBinder(binder);
        final ArgumentCaptor<CustomDateEditor> captor = ArgumentCaptor.forClass(CustomDateEditor.class);
        verify(binder).registerCustomEditor(eq(Date.class), captor.capture());

        CustomDateEditor registeredEditor = captor.getValue();
        final Field dateFormatField = ReflectionUtils.findField(CustomDateEditor.class, "dateFormat");
        dateFormatField.setAccessible(true);
        SimpleDateFormat dateFormat = (SimpleDateFormat) ReflectionUtils.getField(dateFormatField, registeredEditor);
        assertThat(dateFormat.parse("23/11/2001"), is(new SimpleDateFormat("dd/MM/yyyy").parse("23/11/2001")));
    }
}
