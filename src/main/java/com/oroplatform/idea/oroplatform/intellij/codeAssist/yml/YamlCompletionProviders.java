package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.ChoicesProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.CompletionProviders;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.completionProvider.SimpleCompletionProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ChoiceCompletionProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.completionProvider.ObjectInitializationOptionsCompletionProvider;
import com.oroplatform.idea.oroplatform.intellij.indexes.AssetsFiltersIndex;
import com.oroplatform.idea.oroplatform.intellij.indexes.ConfigurationIndex;
import com.oroplatform.idea.oroplatform.intellij.indexes.ServicesIndex;
import com.oroplatform.idea.oroplatform.intellij.indexes.TranslationIndex;
import com.oroplatform.idea.oroplatform.symfony.Service;

import java.util.function.Predicate;

class YamlCompletionProviders implements CompletionProviders {

    @Override
    public CompletionProvider<CompletionParameters> datagrid(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ConfigurationIndex.instance(project).getDatagrids());
    }

    @Override
    public CompletionProvider<CompletionParameters> acl(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ConfigurationIndex.instance(project).getAcls());
    }

    @Override
    public CompletionProvider<CompletionParameters> service(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ServicesIndex.instance(project).findServices());
    }

    @Override
    public CompletionProvider<CompletionParameters> service(Predicate<Service> predicate, InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ServicesIndex.instance(project).findServices(predicate));
    }

    @Override
    public CompletionProvider<CompletionParameters> choices(ChoicesProvider choicesProvider, InsertHandler<LookupElement> insertHandler) {
        return new ChoiceCompletionProvider(choicesProvider, insertHandler);
    }

    @Override
    public CompletionProvider<CompletionParameters> operation(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ConfigurationIndex.instance(project).getOperations());
    }

    @Override
    public CompletionProvider<CompletionParameters> translationDomain(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> TranslationIndex.instance(project).findDomains());
    }

    @Override
    public CompletionProvider<CompletionParameters> assetsFilter(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> AssetsFiltersIndex.instance(project).getFilters());
    }

    @Override
    public CompletionProvider<CompletionParameters> batchJob(InsertHandler<LookupElement> insertHandler) {
        return new SimpleCompletionProvider(insertHandler, project -> ConfigurationIndex.instance(project).getBatchJobs());
    }

    @Override
    public CompletionProvider<CompletionParameters> objectInitializationOptions(InsertHandler<LookupElement> insertHandler) {
        return new ObjectInitializationOptionsCompletionProvider(insertHandler);
    }

}
