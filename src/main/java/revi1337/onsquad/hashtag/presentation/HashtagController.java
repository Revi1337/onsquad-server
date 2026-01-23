package revi1337.onsquad.hashtag.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.hashtag.application.HashtagService;

@RestController
@RequestMapping("/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService cachedHashtagService;

    @GetMapping
    public ResponseEntity<RestResponse<List<String>>> getAllCategories() {
        return ResponseEntity.ok(RestResponse.success(cachedHashtagService.findHashtags()));
    }
}
