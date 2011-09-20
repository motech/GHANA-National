package org.ghana.national.web;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PasswordResetControllerTest {
    @Test
    public void shouldRenderPasswordResetForm() throws Exception {
        assertThat(new PasswordResetController().passwordResetForm(), is(equalTo("password/reset")));
    }
}
