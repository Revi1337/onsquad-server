package revi1337.onsquad.category.application;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CachedCategoryService implements CategoryService {

    private final List<String> cachedCategories;

    public CachedCategoryService(@Qualifier("defaultCategoryService") CategoryService delegate) {
        this.cachedCategories = Collections.unmodifiableList(delegate.findCategories());
    }

    @Override
    public List<String> findCategories() {
        return cachedCategories;
    }
}
