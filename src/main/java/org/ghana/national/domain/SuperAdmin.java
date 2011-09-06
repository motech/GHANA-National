package org.ghana.national.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'SUPER_ADMIN'")
public class SuperAdmin extends MotechAuditableDataObject {
}
