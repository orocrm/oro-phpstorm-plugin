package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import com.oroplatform.idea.oroplatform.schema.PhpClass;
import com.oroplatform.idea.oroplatform.symfony.Service;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ServiceReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String text;
    private final InsertHandler<LookupElement> insertHandler;

    public ServiceReference(PsiElement psiElement, String text, InsertHandler<LookupElement> insertHandler) {
        super(psiElement);
        this.text = text;
        this.insertHandler = insertHandler;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final Project project = myElement.getProject();
        final Optional<Service> service = ServicesIndex.instance(project).findService(text);

        return service.flatMap(Service::getClassName)
            .flatMap(className -> className.isServiceParameter() ? className.getServiceParameter().flatMap(this::getParameterValue) : Optional.of(className.getClassName()))
            .map(className -> new PhpClassReference(myElement, PhpClass.any(), className, insertHandler, Collections.emptySet()).multiResolve(incompleteCode))
            .orElse(new ResolveResult[0]);
    }

    private Optional<String> getParameterValue(String parameterName) {
        final ServicesIndex servicesIndex = ServicesIndex.instance(myElement.getProject());
        return servicesIndex.findParameterValue(parameterName);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Project project = myElement.getProject();

        final Collection<String> services = ServicesIndex.instance(project).findServices();
        return services.stream()
            .map(service -> LookupElementBuilder.create(service).withInsertHandler(insertHandler))
            .toArray();
    }
}
