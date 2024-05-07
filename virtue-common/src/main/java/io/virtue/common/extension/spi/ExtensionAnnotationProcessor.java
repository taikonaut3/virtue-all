package io.virtue.common.extension.spi;

import io.virtue.common.exception.RpcException;
import io.virtue.common.util.FileUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.virtue.common.constant.Constant.EXTENSION_NAME;
import static io.virtue.common.constant.Constant.SPI_FIX_PATH;

/**
 * Extend JDK SPI.
 *
 * @see Extensible
 * @see Extension
 */
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({EXTENSION_NAME})
public class ExtensionAnnotationProcessor extends AbstractProcessor {

    private Map<String, File> fileMap;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> extensionElements = roundEnvironment.getElementsAnnotatedWith(Extension.class);
        for (Element element : extensionElements) {
            if (element instanceof TypeElement typeElement) {
                // Get @Extension
                AnnotationMirror extensionMirror = typeElement.getAnnotationMirrors().stream()
                        .filter(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(EXTENSION_NAME))
                        .toList().getFirst();
                // Get class's all interfaces.
                List<CharSequence> allInterfaces = getAllInterfaces(typeElement);
                // Get interfaces from @Extension#interfaces()
                List<CharSequence> interfaces = getInterfaces(extensionMirror);
                // Needs to be added to the SPI file.
                List<CharSequence> addedInterfaces = (interfaces == null)
                        ? allInterfaces
                        : allInterfaces.stream().filter(interfaces::contains).toList();
                for (CharSequence interfaceName : addedInterfaces) {
                    String path = SPI_FIX_PATH + interfaceName;
                    CharSequence content = typeElement.getQualifiedName();
                    writeServiceFile(path, content);
                }
            }
        }
        return true;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        fileMap = new HashMap<>();
        super.init(processingEnv);
    }

    private List<CharSequence> getAllInterfaces(TypeElement typeElement) {
        Set<TypeElement> allInterfaces = new HashSet<>();
        collectInterfacesRecursively(typeElement, allInterfaces);
        return allInterfaces.stream()
                .filter(item -> item.getAnnotation(Extensible.class) != null)
                .map(item -> (CharSequence) item.getQualifiedName())
                .toList();
    }

    private void collectInterfacesRecursively(TypeElement typeElement, Set<TypeElement> collectedInterfaces) {
        if (typeElement == null) {
            return;
        }
        for (TypeMirror interfaceMirror : typeElement.getInterfaces()) {
            if (interfaceMirror instanceof DeclaredType declaredType
                    && declaredType.asElement() instanceof TypeElement interfaceElement) {
                collectedInterfaces.add(interfaceElement);
                // Recursively gets the parent interface of the interface
                collectInterfacesRecursively(interfaceElement, collectedInterfaces);
            }
        }
        // Recursively get the interface of the parent class
        if (typeElement.getSuperclass() != null) {
            TypeElement superClassElement = getTypeElement(typeElement.getSuperclass());
            collectInterfacesRecursively(superClassElement, collectedInterfaces);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CharSequence> getInterfaces(AnnotationMirror annotationMirror) {
        var elementValues = annotationMirror.getElementValues();
        for (ExecutableElement key : elementValues.keySet()) {
            if (key.toString().equals("interfaces()")) {
                List<AnnotationValue> values = (List<AnnotationValue>) elementValues.get(key).getValue();
                return values.stream()
                        .map(annotationValue -> (TypeMirror) annotationValue.getValue())
                        .map(typeMirror -> (CharSequence) getTypeElement(typeMirror).getQualifiedName())
                        .toList();
            }
        }
        return null;
    }

    private TypeElement getTypeElement(TypeMirror typeMirror) {
        return (TypeElement) processingEnv.getTypeUtils().asElement(typeMirror);
    }

    private void writeServiceFile(String path, CharSequence content) {
        File file = fileMap.get(path);
        if (file == null) {
            file = creatrFile(path);
            fileMap.put(path, file);
        }
        FileUtil.writeLineFile(content, file);
    }

    private File creatrFile(String path) {
        FileObject fileObject;
        try {
            fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path);
        } catch (IOException e) {
            e.printStackTrace();
            throw RpcException.unwrap(e);
        }
        return new File(fileObject.toUri());
    }

}

