package com.oroplatform.idea.oroplatform.intellij.indexes;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.PsiElements;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.YamlPsiElements;
import com.oroplatform.idea.oroplatform.settings.OroPlatformSettings;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.oroplatform.idea.oroplatform.Functions.toStream;

public class ImportFileBasedIndex extends FileBasedIndexExtension<String, Collection<String>> {

    public static final ID<String, Collection<String>> KEY = ID.create("com.oroplatform.idea.oroplatform.import");
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Collection<String>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Collection<String>, FileContent> getIndexer() {
        return new DataIndexer<String, Collection<String>, FileContent>() {
            @NotNull
            @Override
            public Map<String, Collection<String>> map(@NotNull FileContent inputData) {
                final Map<String, Collection<String>> index = new THashMap<>();

                if(!OroPlatformSettings.getInstance(inputData.getProject()).isPluginEnabled()) {
                    return index;
                }

                final Set<String> importedFilePaths = getImportedFilePaths(inputData.getFile().getParent(), (YAMLFile) inputData.getPsiFile());
                if(!importedFilePaths.isEmpty()) {
                    index.put(inputData.getFile().getPath(), importedFilePaths);
                }

                return index;
            }

            private Set<String> getImportedFilePaths(VirtualFile parent, YAMLFile file) {
                return YamlPsiElements.getMappingsFrom(file).stream()
                    .flatMap(mapping -> toStream(mapping.getKeyValueByKey("imports")))
                    .flatMap(importsMapping -> toStream(importsMapping::getValue))
                    .flatMap(importsValue -> YamlPsiElements.getSequenceItems(Collections.singletonList(importsValue)).stream())
                    .flatMap(PsiElements.elementFilter(YAMLMapping.class))
                    .flatMap(sequenceItem -> toStream(sequenceItem.getKeyValueByKey("resource")))
                    .map(resource -> parent.getPath() + "/" + resource.getValueText())
                    .collect(Collectors.toSet());
            }

        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Collection<String>> getValueExternalizer() {
        return new CollectionExternalizer<>(keyDescriptor);
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(YAMLFileType.YML) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return file.getFileType().equals(YAMLFileType.YML) && file.getPath().contains("/Resources/config/");
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
