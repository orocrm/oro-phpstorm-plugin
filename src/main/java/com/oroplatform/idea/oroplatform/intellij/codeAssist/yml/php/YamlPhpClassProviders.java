package com.oroplatform.idea.oroplatform.intellij.codeAssist.yml.php;

import com.oroplatform.idea.oroplatform.intellij.codeAssist.php.PhpClassProvider;
import com.oroplatform.idea.oroplatform.intellij.codeAssist.php.PhpClassProviders;
import com.oroplatform.idea.oroplatform.schema.PropertyPath;

public class YamlPhpClassProviders implements PhpClassProviders {
    @Override
    public PhpClassProvider directProvider() {
        return new DirectPhpClassProvider();
    }

    @Override
    public PhpClassProvider fieldTypeProvider(PropertyPath classPropertyPath) {
        return new FieldTypePhpClassProvider(classPropertyPath);
    }
}
