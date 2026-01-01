package revi1337.onsquad.crew.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.application.file.FileStorageManager;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEvent;

/**
 * Creates a new Crew with an optional image upload. This Class handles both the image upload and the Crew creation in a synchronous manner, ensuring that the
 * user receives a response only after the upload and database operation are complete. If the image upload fails, an exception is thrown immediately to notify
 * the user.
 * <p>
 * Transaction and consistency:
 * <ul>
 *     <li>The Crew is created within a database transaction.</li>
 *     <li>If the operation fails after the image has been uploaded, a {@link FileDeleteEvent} is published
 *         to remove the uploaded image, maintaining consistency between S3 and the database.</li>
 * </ul>
 * <p>
 * Simplicity and clarity:
 * <ul>
 *     <li>No unnecessary asynchronous processing or event listeners are used; the control flow is kept in a single method.</li>
 *     <li>The flow is straightforward, making it easy to maintain, test, and handle exceptions.</li>
 * </ul>
 * </p>
 * <p>
 * Extensibility:
 * <ul>
 *     <li>The use of {@link FileDeleteEvent} allows future extensions, such as queue-based deletion or logging.</li>
 * </ul>
 * </p>
 */
@RequiredArgsConstructor
@Component
public class CrewCreationCoordinator {

    private final FileStorageManager crewS3StorageManager;
    private final CrewCommandService crewCommandService;
    private final ApplicationEventPublisher eventPublisher;

    public void newCrew(Long memberId, CrewCreateDto dto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            crewCommandService.newCrew(memberId, dto, null);
            return;
        }
        String imageUrl = null;
        try {
            imageUrl = crewS3StorageManager.upload(file);
            crewCommandService.newCrew(memberId, dto, imageUrl);
        } catch (CrewBusinessException exception) {
            if (imageUrl != null) {
                eventPublisher.publishEvent(new FileDeleteEvent(imageUrl));
            }
            throw exception;
        }
    }
}
