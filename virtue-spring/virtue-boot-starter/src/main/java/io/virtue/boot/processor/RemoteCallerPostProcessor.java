package io.virtue.boot.processor;

import io.virtue.boot.EnableVirtue;
import io.virtue.boot.RemoteCallerFactoryBean;
import io.virtue.common.exception.RpcException;
import io.virtue.core.annotation.RemoteCaller;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RemoteCaller PostProcessor.
 */
public class RemoteCallerPostProcessor extends PreferentialCreateConfig implements BeanDefinitionRegistryPostProcessor {

    private static final List<Class<? extends Annotation>> REMOTE_CALL_ANNOTATION_TYPES = List.of(
            RemoteCaller.class
    );

    private final String[] remoteCallPackages;

    public RemoteCallerPostProcessor(EnableVirtue enableVirtue) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, enableVirtue.scanBasePackages());
        Collections.addAll(list, enableVirtue.clientScan());
        this.remoteCallPackages = list.toArray(String[]::new);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        RemoteCallerScanner remoteCallerScanner = new RemoteCallerScanner();
        for (String pkg : remoteCallPackages) {
            List<? extends Class<?>> remoteCallInterfaces = remoteCallerScanner.findClasses(pkg);
            for (Class<?> type : remoteCallInterfaces) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RemoteCallerFactoryBean.class);
                builder.addConstructorArgValue(type);
                AbstractBeanDefinition definition = builder.getBeanDefinition();
                String beanName = "";
                if (type.isAnnotationPresent(Qualifier.class)) {
                    beanName = type.getAnnotation(Qualifier.class).value();
                }
                if (!StringUtils.hasText(beanName)) {
                    beanName = StringUtils.uncapitalizeAsProperty(type.getSimpleName());
                }
                registry.registerBeanDefinition(beanName, definition);

            }
        }
    }

    private static final class RemoteCallerScanner extends ClassPathScanningCandidateComponentProvider {

        RemoteCallerScanner() {
            for (Class<? extends Annotation> annotationType : REMOTE_CALL_ANNOTATION_TYPES) {
                addIncludeFilter(new AnnotationTypeFilter(annotationType));
            }
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isInterface();
        }

        public List<? extends Class<?>> findClasses(String basePackage) {
            return findCandidateComponents(basePackage).stream().map(beanDefinition -> {
                try {
                    return ClassLoader.getSystemClassLoader().loadClass(beanDefinition.getBeanClassName());
                } catch (ClassNotFoundException e) {
                    throw RpcException.unwrap(e);
                }
            }).toList();
        }

    }

}
