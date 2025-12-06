package revi1337.onsquad.backup.crew.application.initializer;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.backup.crew.config.CrewTopMemberProperties;
import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.backup.crew.domain.repository.CrewTopMemberRepository;

@Slf4j
@RequiredArgsConstructor
public class LocalCrewTopMemberInitializer {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperties crewTopMemberProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializedCrewTopMembers() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());

        log.info("[Initialize Crew Top Members]");
        crewTopMemberRepository.deleteAllInBatch();
        crewTopMemberRepository.batchInsert(
                crewTopMemberRepository.fetchAggregatedTopMembers(from, to, crewTopMemberProperties.rankLimit()).stream()
                        .map(Top5CrewMemberDomainDto::toEntity)
                        .toList()
        );
    }
}

//package revi1337.onsquad.backup.crew.application.initializer;
//
//import java.time.LocalDate;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.annotation.Profile;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import revi1337.onsquad.backup.crew.config.CrewTopMemberProperties;
//import revi1337.onsquad.backup.crew.domain.dto.Top5CrewMemberDomainDto;
//import revi1337.onsquad.backup.crew.domain.repository.CrewTopMemberRepository;
//
//@Slf4j
//@RequiredArgsConstructor
//@Profile({"local", "default"})
//@ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
//@Component
//public class LocalCrewTopMemberInitializer {
//
//    private final CrewTopMemberRepository crewTopMemberRepository;
//    private final CrewTopMemberProperties crewTopMemberProperties;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void initializedCrewTopMembers() {
//        LocalDate to = LocalDate.now();
//        LocalDate from = to.minusDays(crewTopMemberProperties.during().toDays());
//
//        log.info("[Initialize Crew Top Members]");
//        crewTopMemberRepository.deleteAllInBatch();
//        crewTopMemberRepository.batchInsert(
//                crewTopMemberRepository.fetchAggregatedTopMembers(from, to, crewTopMemberProperties.rankLimit()).stream()
//                        .map(Top5CrewMemberDomainDto::toEntity)
//                        .toList()
//        );
//    }
//}
