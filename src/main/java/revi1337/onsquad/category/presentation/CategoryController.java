package revi1337.onsquad.category.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.category.application.CategoryService;
import revi1337.onsquad.common.dto.RestResponse;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

    private final CategoryService cachedCategoryService;

    @GetMapping
    public ResponseEntity<RestResponse<List<String>>> getAllCategories() {
        return ResponseEntity.ok(RestResponse.success(cachedCategoryService.findCategories()));
    }
}
