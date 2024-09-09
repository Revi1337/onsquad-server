package revi1337.onsquad.squad.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.squad.application.category.CategoryService;
import revi1337.onsquad.squad.dto.response.category.CategoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<RestResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findCategories().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(RestResponse.success(categories));
    }
}
