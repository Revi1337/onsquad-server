package revi1337.onsquad.hashtag.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.hashtag.application.HashtagService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/hashtags")
@RestController
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping
    public ResponseEntity<RestResponse<List<String>>> getAllCategories() {
        return ResponseEntity.ok(RestResponse.success(hashtagService.findHashtags()));
    }
}
