package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Line;

@JdbcTest
@Sql("/schema.sql")
class LineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Line line = new Line(1L, "2호선", "초록색");


    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(jdbcTemplate, jdbcTemplate.getDataSource());
    }

    @Test
    void 호선_삽입() {
        Line savedLine = lineDao.insert(line);

        assertThat(line).isEqualTo(savedLine);
    }

    @Test
    void ID가_없는_호선_삽입() {
        Line savedLine = lineDao.insert(line);

        assertAll(
                () -> assertThat(savedLine.getId()).isEqualTo(1L),
                () -> assertThat(savedLine.getName()).isEqualTo("2호선"),
                () -> assertThat(savedLine.getColor()).isEqualTo("초록색")
        );
    }

    @Test
    void 모든_호선_조회() {
        assertThat(lineDao.findAll()).hasSize(0);

        lineDao.insert(line);
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @Test
    void ID로_단일_호선_조회() {
        Line savedLine = lineDao.insert(line);
        Long id = savedLine.getId();

        Line foundLine = lineDao.findById(id);

        assertThat(savedLine).isEqualTo(foundLine);
    }

    @Test
    void 호선_수정() {
        // given
        Line savedLine = lineDao.insert(line);
        Long id = savedLine.getId();

        Line newLine = new Line(id, "신림선", "청색");

        // when
        lineDao.update(newLine);

        // then
        assertThat(lineDao.findById(id)).isEqualTo(newLine);
    }

    @Test
    void ID로_호선_삭제() {
        // given
        Line savedLine = lineDao.insert(line);
        Long id = savedLine.getId();

        // when
        lineDao.deleteById(id);

        // then
        assertThatThrownBy(() -> lineDao.findById(id));
    }
}
