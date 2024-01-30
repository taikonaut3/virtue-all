package io.github.astro.virtue.boot;

import io.github.astro.virtue.boot.processor.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

public class VirtuePostProcessorRegister implements ImportBeanDefinitionRegistrar {

    private final static List<Class<?>> SCAN_POSTPROCESSORS = List.of(
            RemoteServicePostProcessor.class,
            RemoteCallerPostProcessor.class
    );

    private final static List<Class<?>> GENERAL_POSTPROCESSORS = List.of(
            VirtuePostProcessor.class,
            FilterPostProcessor.class,
            ServerConfigPostProcessor.class,
            RegistryConfigPostProcessor.class,
            ServiceRegistrationPostProcessor.class
    );

    /**
     * 注册后置处理器
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MergedAnnotation<EnableVirtue> annotation = importingClassMetadata.getAnnotations().get(EnableVirtue.class);
        EnableVirtue enablevirtue = annotation.synthesize();
        registerPostProcessor(registry, enablevirtue);

    }

    private void registerPostProcessor(BeanDefinitionRegistry registry, EnableVirtue enablevirtue) {
        for (Class<?> postprocessor : SCAN_POSTPROCESSORS) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(postprocessor);
            builder.addConstructorArgValue(enablevirtue);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
        }
        for (Class<?> postprocessor : GENERAL_POSTPROCESSORS) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(postprocessor);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
        }
    }

}