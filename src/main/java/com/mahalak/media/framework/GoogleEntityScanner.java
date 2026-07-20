package com.mahalak.media.framework;

import com.mahalak.media.annotations.Sheet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class GoogleEntityScanner {

    private static final String BASE_PACKAGE = "com.mahalak.media.entity";

    public Set<Class<?>> scan() {

        Set<Class<?>> entities = new HashSet<>();

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Sheet.class));

        for (BeanDefinition bean : scanner.findCandidateComponents(BASE_PACKAGE)) {

            try {

                entities.add(Class.forName(bean.getBeanClassName()));

            } catch (ClassNotFoundException e) {

                throw new RuntimeException(e);
            }
        }

        return entities;
    }
}
