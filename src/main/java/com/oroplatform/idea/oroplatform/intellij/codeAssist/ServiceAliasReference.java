package com.oroplatform.idea.oroplatform.intellij.codeAssist;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIcons;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class ServiceAliasReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private final String aliasTag;
    @NotNull
    private final String text;
    private final String prefix;
    private final Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues;
    private final InsertHandler<LookupElement> insertHandler;
    private final ServicesIndex servicesIndex;

    public ServiceAliasReference(String aliasTag, PsiElement psiElement, @NotNull String text, String prefix, Function<ServicesIndex, Optional<Collection<String>>> getAllowedValues, InsertHandler<LookupElement> insertHandler) {
        super(psiElement);
        this.aliasTag = aliasTag;
        this.text = text.replace(PsiElements.IN_PROGRESS_VALUE, "").trim().replace("\\\\", "\\");
        this.prefix = prefix;
        this.getAllowedValues = getAllowedValues;
        this.insertHandler = insertHandler;
        this.servicesIndex = ServicesIndex.instance(myElement.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return servicesIndex.getServiceAliasClasses(aliasTag, StringUtil.trimStart(text, prefix)).stream()
            .map(PsiElementResolveResult::new)
            .toArray(ResolveResult[]::new);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        final Optional<Collection<String>> allowedValues = getAllowedValues.apply(servicesIndex);
        return servicesIndex.getServiceAliasesByTag(aliasTag).stream()
            .filter(aliasedService -> !allowedValues.isPresent() || allowedValues.filter(values -> values.contains(aliasedService.getAlias())).isPresent())
            .map(aliasedService -> {
                final Optional<String> className = aliasedService.getService().getClassName()
                    .filter(c -> !c.isServiceParameter())
                    .flatMap(name -> Optional.ofNullable(name.getClassName()).map(n -> "[" + n + "]"));

                return LookupElementBuilder.create(prefix + aliasedService.getAlias())
                    .withTypeText(className.orElse(""), true)
                    .withIcon(PhpIcons.CLASS)
                    .withInsertHandler(insertHandler);
            })
            .toArray();
    }
}
