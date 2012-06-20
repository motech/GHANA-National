package org.motechproject.ghana.national.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

@Repository
public class AllSpringBeans {

    public static ApplicationContext applicationContext;

    @Autowired
    public AllSpringBeans(ApplicationContext applicationContext) {
        AllSpringBeans.applicationContext = applicationContext;
    }
}
