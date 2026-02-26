package revi1337.onsquad.category.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.category.application.CategoryService;
import revi1337.onsquad.common.dto.RestResponse;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService cachedCategoryService;

    @GetMapping
    public ResponseEntity<RestResponse<List<String>>> getAllCategories() {
        List<String> categories = cachedCategoryService.findCategories();
        return ResponseEntity.ok(RestResponse.success(categories));
    }
}
