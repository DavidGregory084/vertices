package vertices.codegen;

import io.vertx.codegen.annotations.VertxGen;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class CodegenProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

            for (Element element : elements) {
                System.out.println("Generating API for " + element.toString());
                Codegen$.MODULE$.generate(processingEnv, roundEnv, element);
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(VertxGen.class.getName());
        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> set = new HashSet<>();
        set.add("codegen.output.dir");
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
