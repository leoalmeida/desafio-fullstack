package com.example.backend.factory;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SupportedAnnotationTypes("*") // Process all annotations
@SupportedSourceVersion(SourceVersion.RELEASE_17) // Adjust to your Java version
public class AnotationsProcessor extends AbstractProcessor{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;

                // Realiza check para confirmar que a classe de teste  
                // possui AbstractIntegrationTest como superclasse
                if (processingEnv.getTypeUtils().isSubtype(
                        typeElement.asType(),
                        processingEnv.getElementUtils()
                                .getTypeElement(AbstractIntegrationTest.class.getCanonicalName())
                                .asType())
                ) {
                    // Pula se for a classe AbstractIntegrationTest
                    if (!typeElement.getQualifiedName().toString()
                            .equals(AbstractIntegrationTest.class.getCanonicalName())) {

                        // Confirma se a classe de teste possui a annotation ContextConfiguration
                        if (typeElement.getAnnotation(ContextConfiguration.class) == null) {
                            processingEnv.getMessager().printMessage(
                                    Diagnostic.Kind.ERROR,
                                    "Classe " + typeElement.getSimpleName()
                                            + " deve ser anotada com @ContextConfiguration",
                                    element
                            );
                        }
                        // Confirma se a classe de teste possui a annotation ActiveProfiles
                        if (typeElement.getAnnotation(ActiveProfiles.class) == null) {
                            processingEnv.getMessager().printMessage(
                                    Diagnostic.Kind.ERROR,
                                    "Classe " + typeElement.getSimpleName()
                                            + " deve ser anotada com @ActiveProfiles",
                                    element
                            );
                        }
                    }
                } 
            }
        }
        return false; // Allow other processors to run
    }
}
