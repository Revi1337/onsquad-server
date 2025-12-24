package revi1337.onsquad.member.application.dto.response;

public record EmailValidResponse(
        boolean valid
) {

    public static EmailValidResponse of(boolean valid) {
        return new EmailValidResponse(valid);
    }
}
