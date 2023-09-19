package com.example.testingframework.yahoo_finance.stock_v3.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {
        Properties properties = load(resource);
        return new PropertiesPropertySource(getPropertyName(name, resource), properties);
    }

    private String getPropertyName(String name, EncodedResource resource) {
        return name != null ? name :
                Objects.requireNonNull(resource.getResource().getFilename(), "Property source name is null");
    }

    private Properties load(EncodedResource resource) throws IOException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException ex) {
            /*
             * Ignore resource not found.
             */
            Throwable cause = ex.getCause();
            if (cause instanceof FileNotFoundException) {
                throw (FileNotFoundException) cause;
            }
            throw ex;
        }
    }
}
