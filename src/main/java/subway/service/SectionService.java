package subway.service;

import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.AddOneSectionRequest;
import subway.dto.AddTwoSectionRequest;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.repository.SectionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionRepository sectionRepository;

    public SectionService(final LineDao lineDao, final StationDao stationDao, final SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionRepository = sectionRepository;
    }

    public void addOneSection(final Long lineId, final AddOneSectionRequest request) {
        Section section = createSection(lineId, request.getUpStationId(), request.getDownStationId(), request.getDistance());

        Sections sections = sectionRepository.findSameLine(section);
        sections.add(section);

        sectionRepository.save(section);
    }

    public void addTwoSections(final Long lineId, final AddTwoSectionRequest request) {
        Section upSection = createSection(lineId, request.getUpStationId(), request.getNewStationId(), request.getUpStationDistance());
        Section downSection = createSection(lineId, request.getNewStationId(), request.getDownStationId(), request.getDownStationDistance());

        Sections sections = sectionRepository.findSameLine(upSection);
        sections.addTwoSections(upSection, downSection);

        sectionRepository.update(lineId, sections);
    }

    private Section createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        return new Section(
                lineDao.findById(lineId),
                stationDao.findById(upStationId),
                stationDao.findById(downStationId),
                new Distance(distance)
        );
    }

    public void removeStation(final Long lineId, final Long stationId) {
        Line line = lineDao.findById(lineId);
        Station station = stationDao.findById(stationId);

        Sections sections = sectionRepository.findByLine(line);

        sections.removeStation(station);

        sectionRepository.update(lineId, sections);
    }

    public Map<String, Object> findLineRoute(final Long lineId) {
        Map<String, Object> result = new HashMap<>();

        Line line = lineDao.findById(lineId);
        result.put("line", LineResponse.of(line));

        Sections sections = sectionRepository.findAll();
        List<Station> stationValues = sections.allStations();
        result.put("stations", stationValues.stream()
                .map(StationResponse::of)
                .collect(toList()));

        return result;
    }
}
